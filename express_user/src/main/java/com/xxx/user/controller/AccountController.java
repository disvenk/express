package com.xxx.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.ExpUser;
import com.xxx.user.cache.VerifyCache;
import com.xxx.user.form.*;
import com.xxx.user.notice.SmsUtils;
import com.xxx.user.security.CurrentUser;
import com.xxx.user.security.GenericLogin;
import com.xxx.user.service.AccountService;
import com.xxx.user.service.VerifyService;
import com.xxx.user.utils.SessionUtils;
import com.xxx.utils.JavaValidate;
import com.xxx.utils.MD5Utils;
import com.xxx.utils.OSSClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 账户管理
 */
@RestController
@RequestMapping("/account")
public class AccountController {

	@Autowired
	private AccountService accountService;
	@Autowired
	private VerifyService verifyService;

	/**
	 * 登录
	 * @param request
	 * @param loginUser
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/login", method = {RequestMethod.POST})
	public ResponseEntity merchantLogin(HttpServletRequest request, HttpServletResponse response, @RequestBody LoginUserForm loginUser) throws Exception {
		if (StringUtils.isBlank(loginUser.usercode))
			return new ResponseEntity(new RestResponseEntity(110, "用户名为空", null), HttpStatus.OK);
		if (StringUtils.isBlank(loginUser.password))
			return new ResponseEntity(new RestResponseEntity(120, "密码为空", null), HttpStatus.OK);
		if (loginUser.loginType == null)
			return new ResponseEntity(new RestResponseEntity(130, "登录身份不能为空", null), HttpStatus.OK);
		String pass = loginUser.password;
		pass = MD5Utils.md5Hex(pass);
		GenericLogin genericLogin = accountService.processLogin(loginUser.usercode, pass, loginUser.loginType);

		SessionUtils.setSession(response, SessionUtils.USER_ID, 3600 * 10, genericLogin.userId);
		SessionUtils.setSession(response, SessionUtils.LOGIN_TYPE, 3600 * 10, genericLogin.loginType);
		if (loginUser.loginType == 3) {
			SessionUtils.setSession(response, SessionUtils.SCHOOL_ID, 3600 * 10, genericLogin.list);
		}
		return returnAuthToken(genericLogin);
	}

	private ResponseEntity returnAuthToken(GenericLogin genericLogin) {
		if (genericLogin == null) {
			return new ResponseEntity(new RestResponseEntity(130, "用户名或密码不正确", null), HttpStatus.OK);
		} else {
			JSONObject data = new JSONObject();
			data.put("userName", genericLogin.userName);
			data.put("loginType", genericLogin.loginType);
			data.put("authToken", CurrentUser.generateAuthToken(genericLogin));
			data.put("userStatus", genericLogin.userStatus);
			return new ResponseEntity(new RestResponseEntity(100, null, data), HttpStatus.OK);
		}
	}

	/**
	 * 通过手机验证码获取身份信息， 不存在则注册
	 * @param request
	 * @param form
	 * @return
	 * @throws UpsertException
	 * @throws ResponseEntityException
	 */
	@RequestMapping(value = "/verifyLogin", method = {RequestMethod.POST})
	public ResponseEntity verifyLogin(HttpServletRequest request, HttpServletResponse response, @RequestBody RegisterUserForm form) throws UpsertException, ResponseEntityException {
		if (StringUtils.isBlank(form.phoneNumber))
			return new ResponseEntity(new RestResponseEntity(110, "手机号码不能为空", null), HttpStatus.OK);
		if (StringUtils.isBlank(form.verificationCode))
			return new ResponseEntity(new RestResponseEntity(130, "验证码不能为空", null), HttpStatus.OK);
		if (form.loginType == null)
			return new ResponseEntity(new RestResponseEntity(190, "登录身份不能为空", null), HttpStatus.OK);
		if (!JavaValidate.isMobileNO(form.phoneNumber))
			return new ResponseEntity(new RestResponseEntity(120, "手机号格式不正确", null), HttpStatus.OK);
		verifyService.checkVerificationCode(form.phoneNumber, form.verificationCode);
		GenericLogin genericLogin;
		//判断账户是否存在
		ExpUser userLogin = accountService.getExpUser(form.phoneNumber, form.phoneNumber, form.loginType);
		if (userLogin != null) {
			genericLogin = accountService.returnGenericLogin(userLogin);
		} else {
			//注册
			genericLogin = accountService.saveRegisterUser(form.phoneNumber, form.phoneNumber, MD5Utils.md5Hex("888888"), form.loginType, null);
		}
		VerifyCache.deleteVerificationCode(form.phoneNumber);

		SessionUtils.setSession(response, SessionUtils.USER_ID, 3600 * 3, genericLogin.userId);
		SessionUtils.setSession(response, SessionUtils.LOGIN_TYPE, 3600 * 3, genericLogin.loginType);

		return returnAuthToken(genericLogin);
	}

	/**
	 * @Description: 用户绑定网点
	 * @Author: Chen.zm
	 * @Date: 2018/1/4 0004
	 */
	@RequestMapping(value = "/saveSchool", method = {RequestMethod.POST})
	public ResponseEntity saveSchool(@RequestBody IdForm form) throws Exception {
		if (form.id == null)
			return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
		accountService.saveSchool(CurrentUser.get().userId, form.id);
		return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
	}

	/**
	 * @Description: 获取账户信息
	 * @Author: Chen.zm
	 * @Date: 2017/11/4 0004
	 */
	@RequestMapping(value = "/loginInfo", method = {RequestMethod.POST})
	public ResponseEntity loginInfo() throws Exception {
		ExpUser user = accountService.get2(ExpUser.class, CurrentUser.get().userId);
		JSONObject json = new JSONObject();
		json.put("nickName", user.getUserNickname());
		json.put("icon", OSSClientUtil.getObjectUrl(user.getIcon()));
		json.put("mobile", user.getUserMobile());
		json.put("schoolName", user.getExpSchool() == null ? "" : user.getExpSchool().getName());
		json.put("sex", "");
		return new ResponseEntity(new RestResponseEntity(100, null, json), HttpStatus.OK);
	}

	/**
	 * @Description: 修改名称
	 * @Author: Chen.zm
	 * @Date: 2017/11/8 0008
	 */
	@RequestMapping(value = "/updateName", method = {RequestMethod.POST})
	public ResponseEntity updateName(@RequestBody NameForm form) throws Exception {
		if (StringUtils.isBlank(form.name))
			return new ResponseEntity(new RestResponseEntity(110, "名称不能为空", null), HttpStatus.OK);
		accountService.updateName(CurrentUser.get().userId, form.name);
		return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
	}


	/**
	 * @Description: 修改头像
	 * @Author: Chen.zm
	 * @Date: 2017/11/8 0008
	 */
	@RequestMapping(value = "/updateIcon", method = {RequestMethod.POST})
	public ResponseEntity updateIcon(@RequestBody IconForm form) throws Exception {
		if (StringUtils.isBlank(form.icon))
			return new ResponseEntity(new RestResponseEntity(110, "头像不能为空", null), HttpStatus.OK);
		accountService.updateIcon(CurrentUser.get().userId, form.icon);
		return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
	}

	/**
	 * @Description: 验证码修改密码
	 * @Date: 2018/1/22 0022
	 */
	@RequestMapping(value = "/resetPassword", method = {RequestMethod.POST})
	public ResponseEntity resetPassword(@RequestBody RegisterUserForm form) throws Exception {
		if (StringUtils.isBlank(form.phoneNumber))
			return new ResponseEntity(new RestResponseEntity(110, "手机号码不能为空", null), HttpStatus.OK);
		if (StringUtils.isBlank(form.verificationCode))
			return new ResponseEntity(new RestResponseEntity(130, "验证码不能为空", null), HttpStatus.OK);
		if (form.loginType == null)
			return new ResponseEntity(new RestResponseEntity(190, "登录身份不能为空", null), HttpStatus.OK);
		if (!JavaValidate.isMobileNO(form.phoneNumber))
			return new ResponseEntity(new RestResponseEntity(120, "手机号格式不正确", null), HttpStatus.OK);
		if (!form.password.matches("^[0-9a-zA-Z_]*$"))
			return new ResponseEntity(new RestResponseEntity(110, "密码必须由字母、数字、下划线组成", null), HttpStatus.OK);
		if (!form.password.matches("^[0-9a-zA-Z_]{6,16}$"))
			return new ResponseEntity(new RestResponseEntity(120, "密码必须为6-16位字符", null), HttpStatus.OK);
		verifyService.checkVerificationCode(form.phoneNumber, form.verificationCode);
		ExpUser user = accountService.getExpUser(form.phoneNumber, form.phoneNumber, form.loginType);
		if(user == null){
			return new ResponseEntity(new RestResponseEntity(170, "账号不存在", null), HttpStatus.OK);
		}
		accountService.updatePassword(CurrentUser.get().userId, MD5Utils.md5Hex(form.password));
		VerifyCache.deleteVerificationCode(form.phoneNumber);
		return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
	}


	/**
	 * @Description:修改密码
	 * @Author: hanchao
	 * @Date: 2018/1/22 0022
	 */
	@RequestMapping(value = "/updatePassword", method = {RequestMethod.POST})
	public ResponseEntity updatePassword(@RequestBody PasswordForm form) throws Exception {
		if (!form.password.matches("^[0-9a-zA-Z_]*$"))
			return new ResponseEntity(new RestResponseEntity(110, "密码必须由字母、数字、下划线组成", null), HttpStatus.OK);
		if (!form.password.matches("^[0-9a-zA-Z_]{6,16}$"))
			return new ResponseEntity(new RestResponseEntity(120, "密码必须为6-16位字符", null), HttpStatus.OK);
		if (!form.passwordSure.matches("^[0-9a-zA-Z_]*$"))
			return new ResponseEntity(new RestResponseEntity(130, "密码必须由字母、数字、下划线组成", null), HttpStatus.OK);
		if (!form.passwordSure.matches("^[0-9a-zA-Z_]{6,16}$"))
			return new ResponseEntity(new RestResponseEntity(140, "密码必须为6-16位字符", null), HttpStatus.OK);
		if (!form.passwordSure1.matches("^[0-9a-zA-Z_]*$"))
			return new ResponseEntity(new RestResponseEntity(150, "密码必须由字母、数字、下划线组成", null), HttpStatus.OK);
		if (!form.passwordSure1.matches("^[0-9a-zA-Z_]{6,16}$"))
			return new ResponseEntity(new RestResponseEntity(160, "密码必须为6-16位字符", null), HttpStatus.OK);
		ExpUser user = accountService.get2(ExpUser.class,"id",CurrentUser.get().userId,"userPassword",MD5Utils.md5Hex(form.password),"logicDeleted",false);
		if(user == null){
			return new ResponseEntity(new RestResponseEntity(170, "原密码输入错误", null), HttpStatus.OK);
		}
		if(!form.passwordSure.equals(form.passwordSure1)){
			return new ResponseEntity(new RestResponseEntity(180, "新密码输入不一致", null), HttpStatus.OK);
		}
		accountService.updatePassword(CurrentUser.get().userId,MD5Utils.md5Hex(form.passwordSure));
		return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
	}


}
