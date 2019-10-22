package com.xxx.core.query;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.entity.BaseEntity;
import com.xxx.utils.ReflectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by wangh on 2015/12/9.
 */
public class ExtFilter {
    public ExtFilter() {
    }

    public ExtFilter(String field,String type, String value,  ExtFilterComparison comparison, ExtFilterOperator operator) {
        this.field = field;
        this.type = type;
        this.value = value;
        this.comparison = comparison;
        this.operator = operator;
    }

    public enum ExtFilterComparison {lt, gt, eq, le, ge, like, in, isnull, isnotnull}

    public enum ExtFilterOperator {and, or}

    public String type;  //extjs grid filter过滤器插件支持的数据类型，共有5个：string、boolean、numeric、date、list
    public String value;  //过滤值。注意：若 extjs grid 的 filter 插件在 list 查询时，请先设置 phpMode: true 模式，如果不用此模式，则请求参数中，集合不是直接用逗号隔开的字符串，而是用[]括起来的数组，后台还没有支持这种格式的解析
    public String field;  //过滤字段
    public ExtFilterComparison comparison;  //比较符
    public ExtFilterOperator operator;  //逻辑符。注意：使用此字段时，其他字段都没有意义

    public String xeditableValue;

    /**
     * 将 ExtFilter 型数据过滤格式解析成 Hibernate 的 Criterion 查询表达式。
     * 注意：
     * 1.若ExtFilter之间没有提供operator，则与下个对象（ExtFilter或者List<ExtFilter>都有可能）的合并默认使用 and 连接。
     * 2.比较日期时，可选格式有:yyyy-MM、yyyy-MM-dd、yyyy-MM-dd hh:mm、yyyy-MM-dd hh:mm:ss。
     *
     * JSONArray 数据格式如下：
     *
     * 一维查询格式（Extjs Grid 的 filter 插件会在请求数据时携带这种格式数据）：
     * [{"field":"Staff_type1","type":"numeric","value":"107230101","comparison":"eq"},{"field":"Staff_type2","type":"numeric","value":"107230102","comparison":"eq"}]
     *
     * 多维查询格式：
     * [
     *   {"value":"aaaa","field":"Case_workaddress","type":"string","comparison":"like"},
     *   {"operator":"or"},
     *   [{"type":"list","value":"107240200,107240100","field":"Staff_sex"}]
     *   {"value":"bbbb","field":"Case_name","type":"string","comparison":"like"},
     *   [{"field":"Staff_type1","type":"numeric","value":"107230101","comparison":"eq"},{"operator":"and"},{"field":"Staff_type2","type":"numeric","value":"107230102","comparison":"eq"}],
     *   {"operator":"or"},
     *   {"value":"gggg","field":"Case_workaddress","type":"string","comparison":"like"},
     *   [{"field":"Staff_type3","type":"numeric","value":"107230103","comparison":"eq"},{"field":"Staff_type4","type":"numeric","value":"107230104","comparison":"eq"}]
     * ]
     */
    public static AliasMappingWrapper parse(String filter,Class entityClass)  {
        AliasMappingWrapper wrapper = new AliasMappingWrapper();
        if (StringUtils.isBlank(filter)) return wrapper;
        JSONArray jsonArray = JSON.parseArray(filter);
        if (jsonArray.size() == 0) return wrapper;

        wrapper.criterion = convertExtFiltersToCriterion(jsonArray, wrapper, entityClass);
        return wrapper;
    }

    /**
     * 解析 sourceFieldName 属性的值类型
     *
     * @param fieldName 要解析的属性名称，支持关联实体字段，如：user.roleList.name
     * @param val    要解析属性的值，此值只有两种类型：String 和 String[]
     * @param entityClass 主实体
     * @return Query 解析后的值
     */
    public static Object parseFieldVal(String fieldName,Object val,Class entityClass,boolean isEnumConvertToString) {
        if (val instanceof String) {
            return parseStringFieldVal(fieldName, val, entityClass, isEnumConvertToString);
        } else if (val instanceof String[]) {
            List<Object> oList = new ArrayList<>();
            String[] vals = (String[]) val;
            for (int i = 0; i < vals.length; i++) {
                oList.add(parseFieldVal(fieldName, vals[i], entityClass, isEnumConvertToString));
            }
            return oList.toArray();
        } else {
            throw new RuntimeException("无法解析值类型");
        }
    }

    /**
     * 解析字符串 sourceFieldName 属性的值类型（其实就是处理下枚举的情况）
     *
     * @param fieldName 要解析的属性名称，支持关联实体字段，如：user.roleList.name
     * @param val    要解析属性的值，此值只有两种类型：String 和 String[]，如："123", new String[]{"123","345"}
     * @param entityClass 主实体
     * @return Query 解析后的值
     */
    public static  Object parseStringFieldVal(String fieldName,Object val,Class entityClass,boolean isEnumConvertToString) {
        if (fieldName.contains(".")) {
            int firstPointPosition = fieldName.indexOf(".");
            String leftProp = fieldName.substring(0, firstPointPosition);
            String rightProp = fieldName.substring(firstPointPosition + 1);
            Object obj = null;
            try {
                Type tp = entityClass.getDeclaredField(leftProp).getGenericType();
                if (tp instanceof ParameterizedType)  //泛型要特殊处理下，否则强转Class会报错。例如：public List<Z_Course> courseList = new ArrayList();
                    tp = ((ParameterizedType) tp).getActualTypeArguments()[0];   //只取第一个泛型参数 Z_Course
                obj = parseFieldVal(rightProp, val, (Class) tp, isEnumConvertToString);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            return obj;
        } else {
            Object obj = null;
            List<Field> fields = ReflectUtils.getDeclaredFields(entityClass);
            try {
                for (int i = 0; i < fields.size(); i++) {
                    Field field = fields.get(i);
                    if (field.getName().equals(fieldName)) {
                        obj = ReflectUtils.parseActualTypeValue(field, val, isEnumConvertToString);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return obj;
        }
    }

    //JSONArray 转 Criterion
    private static Criterion convertExtFiltersToCriterion(JSONArray jsonArray, AliasMappingWrapper wrapper, Class entityClass)  {
        ExtFilterOperator op = ExtFilterOperator.and; //默认 and 连接

        Criterion leftCriterion = null;

        for (int i = 0; i < jsonArray.size(); i++) {
            Object obj = jsonArray.get(i);
            if (obj instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) obj;
                ExtFilter extFilter = new ExtFilter();
                if (jsonObject.get("type") != null)
                    extFilter.type = jsonObject.get("type").toString();
                if (jsonObject.get("value") != null)
                    extFilter.value = jsonObject.get("value").toString();
                if (jsonObject.get("field") != null)
                    extFilter.field = jsonObject.get("field").toString();
                if (jsonObject.get("comparison") != null)
                    extFilter.comparison = ExtFilterComparison.valueOf(jsonObject.get("comparison").toString());
                if (jsonObject.get("operator") != null)
                    extFilter.operator = ExtFilterOperator.valueOf(jsonObject.get("operator").toString());

                if (extFilter.operator == null) {
                    Criterion cri = convertExtFilterToCriterion(extFilter, wrapper, entityClass);
                    if (leftCriterion == null) {
                        leftCriterion = cri;
                    } else {
                        if (op == ExtFilterOperator.and)
                            leftCriterion = Restrictions.and(leftCriterion, cri);
                        else
                            leftCriterion = Restrictions.or(leftCriterion, cri);
                        op = ExtFilterOperator.and; //恢复默认
                    }
                } else {
                    op = extFilter.operator;
                }
            } else if (obj instanceof JSONArray) {
                Criterion cri = convertExtFiltersToCriterion((JSONArray) obj, wrapper, entityClass);
                if (leftCriterion == null) {
                    leftCriterion = cri;
                } else {
                    if (op == ExtFilterOperator.and)
                        leftCriterion = Restrictions.and(leftCriterion, cri);
                    else
                        leftCriterion = Restrictions.or(leftCriterion, cri);
                    op = ExtFilterOperator.and; //恢复默认
                }
            }
        }
        return leftCriterion;
    }

    //ExtFilter 转 Criterion
    private static Criterion convertExtFilterToCriterion( ExtFilter extFilter,AliasMappingWrapper wrapper,Class entityClass)   {
        if (extFilter.comparison != null) {
            if (extFilter.comparison.toString().equals("isnull"))
                return Restrictions.isNull(replaceAlias(extFilter.field, wrapper));
            else if (extFilter.comparison.toString().equals("isnotnull"))
                return Restrictions.isNotNull(replaceAlias(extFilter.field, wrapper));
        }

        Criterion criterion = null;
        switch (extFilter.type) {
            case "string":   //如果是字符串，还需要进一步判断是否枚举类型(枚举类型必须用eq,不能是like)
                switch (extFilter.comparison) {
                    case eq:
                        criterion = Restrictions.eq(replaceAlias(extFilter.field, wrapper),parseFieldVal(extFilter.field, extFilter.value, entityClass, false));  //parseActualTypeValue 会
                        break;
                    case in:
                        List<Object> objs = new ArrayList<>();
                        String[] lt = extFilter.value.split(",");
                        if (lt != null) {
                            for (int i = 0; i < lt.length; i++) {
                                objs.add(parseFieldVal(extFilter.field,lt[i],entityClass, false));
                            }
                        }
                        if (objs.size() > 0) {
                            criterion = Restrictions.in(replaceAlias(extFilter.field, wrapper), objs);
                        }
                        break;
                    case  like:
                        criterion = Restrictions.like(replaceAlias(extFilter.field, wrapper), extFilter.value, MatchMode.ANYWHERE);
                        break;
                    default:
                        criterion = Restrictions.like(replaceAlias(extFilter.field, wrapper), extFilter.value);
                }
                break;
            case "numeric":  //如果numeric，还需要进一步判断是否枚举类型(枚举类型必须用eq)
                switch (extFilter.comparison) {
                    case lt:
                        criterion = Restrictions.lt(replaceAlias(extFilter.field, wrapper), parseFieldVal(extFilter.field, extFilter.value, entityClass,false));
                        break;
                    case gt:
                        criterion = Restrictions.gt(replaceAlias(extFilter.field, wrapper), parseFieldVal(extFilter.field, extFilter.value, entityClass,false));
                        break;
                    case eq:
                        criterion = Restrictions.eq(replaceAlias(extFilter.field, wrapper), parseFieldVal(extFilter.field, extFilter.value, entityClass,false));
                        break;
                    case le:
                        criterion = Restrictions.le(replaceAlias(extFilter.field, wrapper), parseFieldVal(extFilter.field, extFilter.value, entityClass,false));
                        break;
                    case ge:
                        criterion = Restrictions.ge(replaceAlias(extFilter.field, wrapper), parseFieldVal(extFilter.field, extFilter.value, entityClass,false));
                        break;
                    case in:
                        criterion = Restrictions.in(replaceAlias(extFilter.field, wrapper), (Collection) parseFieldVal(extFilter.field, extFilter.value.split(","), entityClass,false));
                        break;
                    default:
                        throw new RuntimeException("numeric 解析失败：未知的 comparison 类型。");
                }
                break;
            case "date":
                switch (extFilter.comparison) {
                    case lt:
                        criterion = Restrictions.lt(replaceAlias(extFilter.field, wrapper), parseFieldVal(extFilter.field, extFilter.value, entityClass,false));
                        break;
                    case gt:
                        criterion = Restrictions.gt(replaceAlias(extFilter.field, wrapper), parseFieldVal(extFilter.field, extFilter.value, entityClass,false));
                        break;
                    case eq:
                        criterion = Restrictions.eq(replaceAlias(extFilter.field, wrapper), parseFieldVal(extFilter.field, extFilter.value, entityClass,false));
                        break;
                    case le:
                        criterion = Restrictions.le(replaceAlias(extFilter.field, wrapper), parseFieldVal(extFilter.field, extFilter.value, entityClass,false));
                        break;
                    case ge:
                        criterion = Restrictions.ge(replaceAlias(extFilter.field, wrapper), parseFieldVal(extFilter.field, extFilter.value, entityClass,false));
                        break;
                    case in:
                        criterion = Restrictions.in(replaceAlias(extFilter.field, wrapper), (Collection) parseFieldVal(extFilter.field, extFilter.value.split(","), entityClass,false));
                        break;
                    default:
                        throw new RuntimeException("date 解析失败：未知的 comparison 类型。");
                }
                break;
            case "boolean":
                switch (extFilter.comparison) {
                    case eq:
                        criterion = Restrictions.eq(replaceAlias(extFilter.field, wrapper), parseFieldVal(extFilter.field, extFilter.value, entityClass,false));
                        break;
                    case in:
                        criterion = Restrictions.in(replaceAlias(extFilter.field, wrapper), (Collection) parseFieldVal(extFilter.field, extFilter.value.split(","), entityClass,false));
                        break;
                    default:
                        throw new RuntimeException("boolean 解析失败：未知的 comparison 类型。");
                }
                break;
            case "list":
                extFilter.value = extFilter.value.replace("\"", "");
                criterion = Restrictions.in(replaceAlias(extFilter.field, wrapper), (Object[])parseFieldVal(extFilter.field, extFilter.value.split(","), entityClass,false));
                break;
        }
        return criterion;
    }

    //将 sourceFieldName 字段转换为别名字段返回，源字段与别名之间的关系存放在 wrapper 中
    private static String replaceAlias(String sourceFieldName,AliasMappingWrapper wrapper) {
        if (sourceFieldName.contains(".")) {
            int lastPointPosition = sourceFieldName.lastIndexOf(".");
            String leftStr = sourceFieldName.substring(0, lastPointPosition);
            String rightStr = sourceFieldName.substring(lastPointPosition);
            if (!wrapper.aliasMapping.containsKey(leftStr)) {
                String f = "";
                String[] sps = leftStr.split("\\.");
                for (int i = 0; i < sps.length; i++) {
                    f += sps[i];
                    if (!wrapper.aliasMapping.containsKey(f))
                        wrapper.aliasMapping.put(f, "c" + String.valueOf(wrapper.aliasMapping.size()));
                    f += ".";
                }
            }
            return wrapper.aliasMapping.get(leftStr) + rightStr;
        } else {
            return sourceFieldName;
        }
    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public ExtFilterComparison getComparison() {
        return comparison;
    }

    public void setComparison(ExtFilterComparison comparison) {
        this.comparison = comparison;
    }

    public ExtFilterOperator getOperator() {
        return operator;
    }

    public void setOperator(ExtFilterOperator operator) {
        this.operator = operator;
    }

    public String getXeditableValue() {
        return xeditableValue;
    }

    public void setXeditableValue(String xeditableValue) {
        this.xeditableValue = xeditableValue;
    }

    public static void main(String[] args) {
        try {
            Object val = ExtFilter.parseFieldVal("id", "123", BaseEntity.class, false);
            System.out.println(val);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}