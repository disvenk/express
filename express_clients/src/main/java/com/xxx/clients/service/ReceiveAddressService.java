package com.xxx.clients.service;

import com.xxx.clients.form.AddressForm;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.ExpReceiveAddress;
import com.xxx.model.business.ExpSchool;
import com.xxx.user.security.CurrentUser;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ReceiveAddressService extends CommonService {


	/**
	 * @Description: 获取收件地址列表
	 * @Author: Chen.zm
	 * @Date: 2018/1/5 0005
	 */
	public List<ExpReceiveAddress> findList(Integer userId) {
		Criterion cri = Restrictions.and(Restrictions.eq("logicDeleted", false));
		cri = Restrictions.and(cri, Restrictions.eq("userId", userId));
		return getCurrentSession().createCriteria(ExpReceiveAddress.class)
				.add(cri)
				.addOrder(Order.desc("id"))
				.list();
	}

	/**
	 * @Description: 获取用户收件地址
	 * @Author: Chen.zm
	 * @Date: 2018/1/5 0005
	 */
	public ExpReceiveAddress getAddress(Integer id, Integer userId) {
		return get2(ExpReceiveAddress.class, "id", id, "userId", userId);
	}

	/**
	 * @Description: 删除收件地址
	 * @Author: Chen.zm
	 * @Date: 2018/1/5 0005
	 */
	public void removeAddress(Integer id, Integer userId) throws ResponseEntityException, UpsertException{
		ExpReceiveAddress address = getAddress(id, userId);
		if(address == null)
			throw new ResponseEntityException(210, "收件地址不存在");
		address.setLogicDeleted(true);
		upsert2(address);
	}


	/**
	 * @Description: 收件人地址设为默认
	 * @Author: Chen.zm
	 * @Date: 2018/1/5 0005
	 */
	public void saveDefault(Integer id, Integer userId) throws ResponseEntityException, UpsertException{
		ExpReceiveAddress address = getAddress(id, userId);
		if(address == null)
			throw new ResponseEntityException(210, "收件地址不存在");
		//将其他收件地址设为非默认
		String hql = "update ExpReceiveAddress set isDefault = false where userId in :userId";
		getCurrentSession().createQuery(hql).setInteger("userId", userId).executeUpdate();

		address.setDefault(true);
		upsert2(address);
	}

	/**
	 * @Description: 收件人地址保存
	 * @Author: Chen.zm
	 * @Date: 2018/1/5 0005
	 */
	public ExpReceiveAddress save(AddressForm form, Integer userId) throws ResponseEntityException, UpsertException{
		ExpReceiveAddress address;
		if (form.id == null) {
			address = new ExpReceiveAddress();
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
