package com.xxx.user.security;

import com.alibaba.fastjson.JSONObject;
import com.xxx.core.exceptions.ResponseEntityException;

import java.io.Serializable;
import java.util.List;

/**
 * 说明:登录信息
 */
public class GenericLogin implements Serializable  {
	public int userId;
	public int loginType;                 //0：超管   1： 学生   2：快递人员  3：平台用户
	public String userName;
	public int userStatus;               //账户状态   0：正常    1：账户未绑定网点
	public boolean isExperience = false;   //是否体验账号
	public Integer schoolId;					//网点id
	public List<Integer> list;

	public GenericLogin() {
	}

	public GenericLogin(int userId, int loginType, String userName, int userStatus, boolean isExperience,Integer schoolId,List<Integer> list) {
		this.userId = userId;
		this.loginType = loginType;
		this.userName = userName;
		this.userStatus = userStatus;
		this.schoolId = schoolId;
		this.isExperience = isExperience;
		this.list=list;
	}

	@Override
	public String toString() {
		JSONObject json = new JSONObject();
		json.put("userId", userId);
		json.put("schoolId",schoolId);
		json.put("loginType", loginType);
		json.put("userName", userName);
		json.put("userStatus", userStatus);
		json.put("isExperience", isExperience);
		json.put("schoolIds",list);
		return json.toJSONString();
	}

	public Integer getLoginType() throws ResponseEntityException {
		return loginType;
	}

}