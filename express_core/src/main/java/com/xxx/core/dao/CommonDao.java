package com.xxx.core.dao;

import com.xxx.core.persist.respository.HibernateRepository;
import com.xxx.core.persist.respository.MybatisRepository;

public class CommonDao extends HibernateRepository {
    protected MybatisRepository mybatisRepository = new MybatisRepository(false);
    protected MybatisRepository mybatisReadonlyRepository = new MybatisRepository(true);
    protected HibernateRepository hibernateReadonlyRepository = new HibernateRepository(true);
}
