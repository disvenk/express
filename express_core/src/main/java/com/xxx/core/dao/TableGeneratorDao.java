package com.xxx.core.dao;

import com.xxx.core.persist.respository.MybatisUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class TableGeneratorDao extends CommonDao {

    public Long getValue(String name) {
        Map<String, Object> params = new HashMap();
        params.put("name", name);

        return MybatisUtils.sqlSessionTemplate.selectOne("mybatis.mappers.TableGeneratorMapper.getValue", params);
    }

    public void setValue(String name, long value) {
        Map<String, Object> params = new HashMap();
        params.put("name", name);
        params.put("value", String.valueOf(value));
        MybatisUtils.sqlSessionTemplate.update("mybatis.mappers.TableGeneratorMapper.setValue", params);
    }
}
