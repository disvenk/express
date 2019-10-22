package com.xxx.clients.service;

import com.alibaba.fastjson.JSONObject;
import com.xxx.clients.dao.UserHomeDao;
import com.xxx.core.config.Configs;
import com.xxx.core.service.CommonService;
import com.xxx.utils.HttpRequestSend;
import com.xxx.utils.UUIDGenerator;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


@Service
public class UserHomeService extends CommonService {

	@Autowired
	private UserHomeDao userHomeDao;

	/**
	 * @Description: 获取首页信息
	 * @Author: Chen.zm
	 * @Date: 2018/1/4 0004
	 */
	public JSONObject findHomeInfo(Integer userId) {
		return userHomeDao.findHomeInfo(userId);
	}


}
