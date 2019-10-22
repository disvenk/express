package com.xxx.clients.service;

import com.xxx.clients.form.AddressForm;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.ExpReceiveAddress;
import com.xxx.model.business.ExpSendAddress;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SendAddressService extends CommonService {


	/**
	 * @Description: 获取寄件地址列表
	 * @Author: Chen.zm
	 * @Date: 2018/1/5 0005
	 */
	public List<ExpSendAddress> findList(Integer userId) {
		Criterion cri = Restrictions.and(Restrictions.eq("logicDeleted", false));
		cri = Restrictions.and(cri, Restrictions.eq("userId", userId));
		return getCurrentSession().createCriteria(ExpSendAddress.class)
				.add(cri)
				.addOrder(Order.desc("id"))
				.list();
	}

	/**
	 * @Description: 获取用户寄件地址
	 * @Author: Chen.zm
	 * @Date: 2018/1/5 0005
	 */
	public ExpSendAddress getAddress(Integer id, Integer userId) {
		return get2(ExpSendAddress.class, "id", id, "userId", userId);
	}

	/**
	 * @Description: 删除寄件地址
	 * @Author: Chen.zm
	 * @Date: 2018/1/5 0005
	 */
	public void removeAddress(Integer id, Integer userId) throws ResponseEntityException, UpsertException{
		ExpSendAddress address = getAddress(id, userId);
		if(address == null)
			throw new ResponseEntityException(210, "收件地址不存在");
		address.setLogicDeleted(true);
		upsert2(address);
	}


	/**
	 * @Description: 收件人寄件设为默认
	 * @Author: Chen.zm
	 * @Date: 2018/1/5 0005
	 */
	public void saveDefault(Integer id, Integer userId) throws ResponseEntityException, UpsertException{
		ExpSendAddress address = getAddress(id, userId);
		if(address == null)
			throw new ResponseEntityException(210, "收件地址不存在");
		//将其他收件地址设为非默认
		String hql = "update ExpSendAddress set isDefault = false where userId in :userId";
		getCurrentSession().createQuery(hql).setInteger("userId", userId).executeUpdate();

		address.setDefault(true);
		upsert2(address);
	}

	/**
	 * @Description: 收件人寄件地址保存
	 * @Author: Chen.zm
	 * @Date: 2018/1/5 0005
	 */
	public ExpSendAddress save(AddressForm form, Integer userId) throws ResponseEntityException, UpsertException{
		ExpSendAddress address;
		if (form.id == null) {
			address = new ExpSendAddress();
			address.setUserId(userId);
		} else {
			address = getAddress(form.id, userId);
			if(address == null)
				throw new ResponseEntityException(210, "收件地址不存在");
		}
		address.setName(form.name);
		address.setTel(form.tel);
		address.setProvince(form.province);
		address.setCity(form.city);
		address.setZone(form.zone);
		address.setAddress(form.address);
		if (form.isDefault != null) {
			address.setDefault(form.isDefault);
		}
		return upsert2(address);
	}


}
