package com.xxx.pay.service;

import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.service.CommonService;
import com.xxx.pay.Common;
import com.xxx.pay.form.PayForm;
import com.xxx.utils.contract.SimpleResultExpand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

/**
* Created by wangh on 2015/10/27.
 */
@Service
public class CashPayService extends CommonService {
	@Autowired
	private PaymentService paymentService;

	/**
	 * 现金交易
	 *
	 * @param payForm
	 * @throws Exception
	 */
	@CacheEvict(value = {"PackOrder,Deal,RechargeUserRecord,DealCash,DealCashBeautician,SecUserCard,PackOrderArrears,SecUserCardItem,StoreProductOrder,StoreProduct,PackOrderArrearsLog,RechargeOrder,RechargeOrderArrearsLog,TimeCardOrder,TimeCardOrderArrearsLog,TimeCardUser"}, allEntries = true)
	public SimpleResultExpand cashTrade(PayForm payForm, String channel) throws Exception {
		if (payForm.orderId == null)
			throw new ResponseEntityException(110, "订单ID不能为空");
		if (payForm.orderType == null)
			throw new ResponseEntityException(120, "订单类型不能为空");
		if (!Common.validateOrderType(payForm.orderType))
			throw new ResponseEntityException(130, "订单类型不正确");
		if (payForm.totalFee < 0)
			throw new ResponseEntityException(140, "订单总金额不正确");

		SimpleResultExpand extSimpleResult = new SimpleResultExpand(true, "", null);
		paymentService.createPayment(payForm, channel, 1);
		paymentService.FinishPayment(payForm.outTradeNo);
		return extSimpleResult;
	}
}

