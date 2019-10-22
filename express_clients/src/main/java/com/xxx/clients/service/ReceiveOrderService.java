package com.xxx.clients.service;

import com.alibaba.fastjson.JSONObject;
import com.xxx.clients.form.MyReceiveOrderForm;
import com.xxx.core.config.Configs;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.*;
import com.xxx.user.security.CurrentUser;
import com.xxx.user.utils.GenerateNumberUtil;
import com.xxx.utils.HttpRequestSend;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class ReceiveOrderService extends CommonService {

	/**
	 * @Description: 学生端：我要收件时，保存至我的订单
	 * @Author: disvenk.dai
	 * @Date: 2018/1/9
	 */
	@CacheEvict(value = "ExpReceiveOrder",allEntries = true)
	public Boolean saveToMyOrder(MyReceiveOrderForm form, Integer userId ,Integer schoolId) throws ResponseEntityException, UpsertException {
		ExpReceiveOrder expReceiveOrder = get2(ExpReceiveOrder.class, "deliveryNo", form.deliveryNo, "userId", userId, "logicDeleted", false);
		if (expReceiveOrder != null) {
			/*expReceiveOrder.setDeliveryId(form.deliverCompanyId);
			expReceiveOrder.setGoodsTypeId(form.goodsType);
			expReceiveOrder.setGoodsDescription(form.goodsName);
			expReceiveOrder.setDeliveryNo(form.deliveryNo);
			upsert2(expReceiveOrder);*/
			return false;
		} else {
			ExpReceiveOrder order = new ExpReceiveOrder();
			order.setSchoolId(schoolId);
			order.setUserId(userId);
			order.setOrderNo(GenerateNumberUtil.generateSendExpressNumber());
			order.setOrderStatus(1);
			order.setType(0);
			order.setDeliveryId(form.deliverCompanyId);
			order.setDeliveryNo(form.deliveryNo);
			order.setGoodsTypeId(form.goodsType);
			ExpGoodsType expGoodsType =	get2(ExpGoodsType.class,form.goodsType);
			order.setGoodsTypeName(expGoodsType.getName());
			order.setGoodsDescription(form.desc);
			order.setCreatedDate(new Date());
			upsert2(order);
			return true;
		}
	}

	/**
	 * @Description: 收件订单列表(自行取件和派件上门)
	 * @Author: disvenk.dai
	 * @Date: 2018/1/9
	 */
	@Cacheable(value = "ExpReceiveOrder")
	public List<ExpReceiveOrder> findReceiveOrderList(Integer userId, Integer schoolId, Integer orderType) {
		Criterion cri = Restrictions.and(Restrictions.eq("logicDeleted", false));
		cri = Restrictions.and(cri, Restrictions.eq("userId", userId));
		cri = Restrictions.and(cri, Restrictions.eq("schoolId", schoolId));
		cri = Restrictions.and(cri, Restrictions.eq("type", orderType));
		return getCurrentSession().createCriteria(ExpReceiveOrder.class)
				.add(cri)
				.addOrder(Order.desc("createdDate"))
				.list();
	}

	/**
	 * @Description: 取消订单
	 * @Author: disvenk.dai
	 * @Date: 2018/1/9
	 */
	public void deleteReceiveOrder(Integer userId, Integer orderId) throws ResponseEntityException, UpsertException {
		ExpReceiveOrder order = get2(ExpReceiveOrder.class, "id", orderId, "userId", userId);
		if (order != null && order.getOrderStatus()==1) {
			order.setLogicDeleted(true);
			upsert2(order);
		}
	}

	/**
	 * @Description: 转派件上门
	 * @Author: disvenk.dai
	 * @Date: 2018/1/15
	 */
	@CacheEvict(value = "ExpReceiveOrder",allEntries = true)
	public ExpReceiveOrder transDoor(Integer id,String doorTime,Integer fee,Integer addressId,String remarks) throws UpsertException, ResponseEntityException {
		ExpReceiveOrder expReceiveOrder = get2(ExpReceiveOrder.class,id);
		ExpServiceFee expServiceFee = get2(ExpServiceFee.class,"schoolId", CurrentUser.get().schoolId,"type",1);
		if(expReceiveOrder==null){
			throw new ResponseEntityException(220,"该订单不存在");
		}
		if(expReceiveOrder.getOrderStatus()!=2){
		throw new ResponseEntityException(220,"不能执行转派件上门");
		}
		ExpReceiveAddress expReceiveAddress = get2(ExpReceiveAddress.class,"id",addressId);
		expReceiveOrder.setReceiverName(expReceiveAddress.getName());
		expReceiveOrder.setReceiverTel(expReceiveAddress.getTel());
		expReceiveOrder.setProvince(expReceiveAddress.getProvince());
		expReceiveOrder.setCity(expReceiveAddress.getCity()==null?"":expReceiveAddress.getCity());
		expReceiveOrder.setZone(expReceiveAddress.getZone()==null?"":expReceiveOrder.getZone());
		expReceiveOrder.setReceiverAddress(expReceiveAddress.getAddress());
		expReceiveOrder.setBeginEndTime(doorTime);
		expReceiveOrder.setTodoorFee(expServiceFee.getFee());
		expReceiveOrder.setTodoorTipFee(fee.doubleValue());
		Double countFee = expServiceFee.getFee();
		if(fee!=null){
		countFee = countFee+fee.intValue();
		}
		expReceiveOrder.setOrderPrice(countFee);
		expReceiveOrder.setRemarks(remarks);
		return upsert2(expReceiveOrder);
	}

	/**
	 * @Description: 拒收订单
	 * @Author: disvenk.dai
	 * @Date: 2018/1/9
	 */
	public void rejectOrder(Integer userId, Integer orderId) throws ResponseEntityException, UpsertException {
		ExpReceiveOrder order = get2(ExpReceiveOrder.class, "id", orderId, "userId", userId);
		if (order != null) {
			order.setOrderStatus(7);
			order.setRefuseDate(new Date());
			upsert2(order);
		}
	}

	/**
	 * @Description: 收件订单详情(自行取件和派件上门)
	 * @Author: disvenk.dai
	 * @Date: 2018/1/9
	 */
	public ExpReceiveOrder getReceiveOrder(Integer orderId) throws ResponseEntityException {
		ExpReceiveOrder expReceiveOrder = get2(ExpReceiveOrder.class, "id", orderId);
		if(expReceiveOrder==null){
			throw new ResponseEntityException(220,"该订单不存在");
		}
		return expReceiveOrder;
	}
	/**
	 * @Description: 查询账单表
	 * @Author: disvenk.dai
	 * @Date: 2018/1/9
	 */
	public Payment getPayment(Integer orderId){
		Payment payment = get2(Payment.class,"orderId",orderId,"status",20,"orderType",1);
		return payment;
	}

	/**
	 * @Description: 确认送达
	 * @Author: disvenk.dai
	 * @Date: 2018/1/9
	 */
	@CacheEvict(value = "ExpReceiveOrder",allEntries = true)
	public void receivedOrder(Integer userId, Integer orderId) throws ResponseEntityException, UpsertException {
		ExpReceiveOrder order = get2(ExpReceiveOrder.class, "id", orderId, "userId", userId);
		if (order != null) {
			order.setOrderStatus(5);
			order.setReachDate(new Date());
			upsert2(order);
		}
	}

}
