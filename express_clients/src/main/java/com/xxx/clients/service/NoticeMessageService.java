package com.xxx.clients.service;

import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.ExpNoticeMessageLog;
import com.xxx.model.business.ExpSchool;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class NoticeMessageService extends CommonService {


	/**
	 * @Description: 站内信列表
	 * @Author: Chen.zm
	 * @Date: 2018/1/4 0004
	 */
	public List<ExpNoticeMessageLog> findNoticeMessageLogList(Integer userId, Integer schoolId) {
		Criterion cri = Restrictions.and(Restrictions.eq("logicDeleted", false));
        cri = Restrictions.and(cri, Restrictions.eq("userId", userId));
//        cri = Restrictions.and(cri, Restrictions.eq("schoolId", schoolId));
		return getCurrentSession().createCriteria(ExpNoticeMessageLog.class)
				.add(cri)
				.addOrder(Order.desc("id"))
				.list();
	}

	/**
	 * @Description: 标记站内信息为已读
	 * @Author: Chen.zm
	 * @Date: 2018/1/4 0004
	 */
	public void updateNoticeMessageLogeChecked(ExpNoticeMessageLog log) throws UpsertException{
		log.setNoticeChecked(true);
		upsert2(log);
	}

}
