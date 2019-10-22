package com.xxx.user.service;

import com.alibaba.fastjson.JSONObject;
import com.xxx.core.config.Configs;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.ExpNoticeMessageLog;
import com.xxx.model.business.ExpSocial;
import com.xxx.user.form.SendMessageForm;
import com.xxx.utils.HttpRequestSend;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service("weChantService2")
public class WeChatService extends CommonService {

	/**
	 * @Description: 根据用户userId， 推线微信公众号消息
	 * @Author: Chen.zm
	 * @Date: 2018/1/25
	 */
	public boolean sendTextMessage(int userId, String content) {
		//根据userId获取openId
		PageQuery pageQuery = new PageQuery(0, 1);
		Criterion cri = Restrictions.eq("userId", userId);
		cri = Restrictions.and(cri, Restrictions.eq("location", "WECHAT"));
		pageQuery.hibernateCriterion = cri;
		pageQuery.order = "desc";
		pageQuery.sort = "id";
		List<ExpSocial> socials =  hibernateReadonlyRepository.getList(ExpSocial.class, pageQuery);
		if (socials == null || socials.size() == 0 || StringUtils.isBlank(socials.get(0).getUid()))
			return false;
		Map map = sendTextMessage(socials.get(0).getUid(), content);
		if (!"ok".equals(map.get("errmsg"))) {
			return false;
		}
		return true;
	}

	/**
	 * 获取微信token
	 * @return
	 */
	public String getAccToken() {
		String getToken = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential" +
				"&appid="+ Configs.payAppid+
				"&secret="+ Configs.payAppSecret;
		Map<String,Object> mapparam = getParam(getToken);
		String acc_token = MapUtils.getString(mapparam,"access_token");
		return acc_token;
	}


	/**
	 * 发送请求  处理返回数据格式
	 * @param url
	 * @return
	 */
	@Cacheable(value = {"recode"}) //设置缓存，防止过度重复获取二维码  （该缓存名非实体）
	public static Map<String,Object> getParam(String url, String param){
		String json = HttpRequestSend.sendPost(url,param);
		JSONObject obj = JSONObject.parseObject(json);
		return obj;
	}
	public static Map<String,Object> getParam(String url){
		return getParam(url, null);
	}



	/**
	 * @Description: 微信（客服）向用户发送消息  （发送文本消息， 其他类型消息请参照官方文档）
	 * @Referenced: https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140547
	 */
	public Map<String,Object> sendTextMessage(String openId, String content) {
		String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + getAccToken();
		String jsonStr =
				"{\n" +
						"    \"touser\":\"" + openId + "\",\n" +
						"    \"msgtype\":\"text\",\n" +
						"    \"text\":\n" +
						"    {\n" +
						"         \"content\":\"" + content + "\"\n" +
						"    }\n" +
						"}";
		return getParam(url, jsonStr);
	}


	/**
	 * @Description: 客服消息- 发送图文消息（点击跳转到图文消息页面） 图文消息条数限制在8条以内，注意，如果图文数超过8，则将会无响应。
	 * @param:
	 */
	public Map<String,Object> sendMpnewsMessage(String openId, String media_id) {
		String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + getAccToken();
		String jsonStr =
				"{\n" +
						"    \"touser\":\"" + openId + "\",\n" +
						"    \"msgtype\":\"mpnews\",\n" +
						"    \"mpnews\":\n" +
						"    {\n" +
						"         \"media_id\":\"" + media_id + "\"\n" +
						"    }\n" +
						"}";
		return getParam(url, jsonStr);
	}






}
