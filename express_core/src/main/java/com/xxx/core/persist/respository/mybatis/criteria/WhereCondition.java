package com.xxx.core.persist.respository.mybatis.criteria;

import com.xxx.utils.LogUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WhereCondition {
    private List<String> conditions;
    private List<WhereColumn> properties;
    private Map<String, String> replaceMap = new HashMap<>();

    public List<String> getConditions() {
        return conditions;
    }

    public List<WhereColumn> getProperties() {
        return properties;
    }

    public Map<String, String> getReplaceMap() {
        return replaceMap;
    }

    WhereCondition(List<String> conditions, List<WhereColumn> properties) {
        this.conditions = conditions;
        this.properties = properties;
    }

    public void appendWhere(EntityScan.ClassMapper classMapper, String cascadeName, String prefix, String alias, Object data) {
        if (CollectionUtils.isEmpty(getProperties())) {
            return;
        }
        String aliasDot = "";
        if (StringUtils.isNotBlank(alias)) {
            aliasDot = alias + ".";
        }
        if (StringUtils.isNotBlank(cascadeName)) {
            cascadeName = cascadeName + "@";
        } else {
            cascadeName = "";
        }
        for (WhereColumn wc : getProperties()) {
            if (StringUtils.isBlank(cascadeName) && StringUtils.isBlank(wc.getPrefix())) {
                if (data == null || StringUtils.isBlank(prefix)) {
                    addReplaceMap(cascadeName, aliasDot, prefix, wc, classMapper, true, null);
                } else {
                    Object obj = getProperty(data, wc.getName());
                    if (obj == null) {
                        addReplaceMap(cascadeName, aliasDot, prefix, wc, classMapper, false, null);
                    } else {
                        addReplaceMap(cascadeName, aliasDot, prefix, wc, classMapper, true, obj);
                    }
                }
            } else if (StringUtils.isNotBlank(cascadeName) && cascadeName.equals(wc.getPrefix() + "@")) {
                if (data == null || StringUtils.isBlank(prefix)) {
                    addReplaceMap(cascadeName, aliasDot, prefix, wc, classMapper, true, null);
                } else {
                    Object obj = getProperty(data, wc.getPrefix() + "@" + wc.getName());
                    if (obj == null) {
                        addReplaceMap(cascadeName, aliasDot, prefix, wc, classMapper, false, null);
                    } else {
                        addReplaceMap(cascadeName, aliasDot, prefix, wc, classMapper, true, obj);
                    }
                }
            }
        }
    }

    private void addReplaceMap(String cascadeName, String aliasDot, String prefix, WhereColumn wc, EntityScan.ClassMapper classMapper, boolean flag, Object data) {
        if (!flag) {
            if (Operator.NOT_EQ.getOp().equals(wc.getOperator())) {
                replaceMap.put(cascadeName + wc.getName(), aliasDot + classMapper.getColumnMap().get(wc.getName()) + " is not null");
            } else {
                replaceMap.put(cascadeName + wc.getName(), aliasDot + classMapper.getColumnMap().get(wc.getName()) + " is null");
            }
        } else {
            if (Operator.IN.getOp().equals(wc.getOperator()) || Operator.NOT_IN.getOp().equals(wc.getOperator())) {
                List<Object> list = (List<Object>) data;
                StringBuilder sb = new StringBuilder();
                for (Object o : list) {
                    if (o instanceof String) {
                        sb.append(",").append("'").append(o).append("'");
                    } else {
                        sb.append(",").append(o);
                    }
                }
                replaceMap.put(cascadeName + wc.getName(), aliasDot + classMapper.getColumnMap().get(wc.getName()) + wc.getOperator() + "( " + sb.substring(1) + " )");
            } else {
                replaceMap.put(cascadeName + wc.getName(), aliasDot + classMapper.getColumnMap().get(wc.getName()) + wc.getOperator() + "#{" + prefix + cascadeName + wc.getName() + "}");
            }
        }
    }

    public String getWhere() {
        StringBuilder whereStr = new StringBuilder();
        for (String str : getConditions()) {
            if (WhereBuilder.isOperator(str)) {
                whereStr.append(str);
            } else {
                whereStr.append(replaceMap.get(str));
            }
        }
        return whereStr.toString();
    }

    private Object getProperty(Object data, String property) {
        try {
            if (data instanceof Map) {
                return ((Map) data).get(property);
            }
            return BeanUtils.getProperty(data, property);
        } catch (Exception e) {
            LogUtils.errorFormat(e, "WhereCondition", "getProperty");
        }
        return null;
    }
}
