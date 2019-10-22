package com.xxx.core.service;

import com.xxx.core.persist.respository.HibernateRepository;
import org.springframework.stereotype.Service;

@Service
public class CommonService extends HibernateRepository {
    protected HibernateRepository hibernateReadonlyRepository = new HibernateRepository(true);
}

