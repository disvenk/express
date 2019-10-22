package com.xxx.clients.service;

import com.alibaba.fastjson.JSONObject;
import com.xxx.clients.dao.UserHomeDao;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.ExpSchool;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SchoolService extends CommonService {


	/**
	 * @Description: 网点列表
	 * @Author: Chen.zm
	 * @Date: 2018/1/4 0004
	 */
	public List<ExpSchool> findSchoolList() {
		Criterion cri = Restrictions.and(Restrictions.eq("logicDeleted", false));
		return getCurrentSession().createCriteria(ExpSchool.class)
				.add(cri)
				.addOrder(Order.desc("id"))
				.list();
	}


}
