package com.xxx.clients.service;

import com.alibaba.fastjson.JSONObject;
import com.xxx.core.config.Configs;
import com.xxx.core.service.CommonService;
import com.xxx.pay.weixin.WeixinUtil;
import com.xxx.utils.HttpRequestSend;
import com.xxx.utils.UUIDGenerator;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Service
public class WeChatService extends CommonService {

	//用于保存 全局信息
	private static Map<String,Object> mapstic = new HashMap<String,Object>();

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
	 * @Description: 通过Code获取微信账号的OpenId
	 * @author: Chen.zm
	 * @date: 2017/5/22
	 */
	public String findOpenId(String code) {
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?" +
				"appid=" + Configs.payAppid +
				"&secret=" + Configs.payAppSecret +
				"&code=" + code +
				"&grant_type=authorization_code";
		String json = HttpRequestSend.sendPost(url,null);
		JSONObject obj = JSONObject.parseObject(json);
		return  obj.get("openid") == null ? "" :  obj.get("openid").toString();
	}

	/**
	 * 通过openid获取用户微信信息
	 * @param openid
	 * @return
	 */
	public Map<String,Object> getUserInfo(String openid) throws UnsupportedEncodingException{
		Map<String,Object> resultmap = new HashMap<String,Object>();
		if(StringUtils.isBlank(openid)){
			resultmap.put("statusCode","500");
			resultmap.put("msg","openid不能为空");
			return resultmap;
		}
		String acc_token = getAccToken();
		String getInfo = "https://api.weixin.qq.com/cgi-bin/user/info?access_token="+acc_token+"&openid="+openid+"&lang=zh_CN";
		JSONObject maptemp = getParam(getInfo);
		resultmap.put("nickname", MapUtils.getString(maptemp,"nickname"));
		resultmap.put("headimgurl",MapUtils.getString(maptemp,"headimgurl"));
		resultmap.put("subscribe",MapUtils.getString(maptemp,"subscribe"));
		return resultmap;
	}

	/**
	 * 发送请求  处理返回数据格式
	 * @param url
	 * @return
	 */
	@Cacheable(value = {"recode"}) //设置缓存，防止过度重复获取二维码  （该缓存名非实体）
	public static JSONObject getParam(String url, String param){
		String json = HttpRequestSend.sendPost(url,param);
		JSONObject obj = JSONObject.parseObject(json);
		return obj;
	}
	public static JSONObject getParam(String url){
		return getParam(url, null);
	}


	/**
	 * 获取微信签名
	 * @return
	 */
	public Map<String,Object> gettick(HttpServletRequest request){
		String tick = MapUtils.getString(mapstic,"ticket_weixin");
		Long ticktime = MapUtils.getLong(mapstic,"ticket_time");
		Date date = new Date();
		Long datelong = date.getTime();
		if (StringUtils.isBlank(tick) || (datelong - ticktime > 7200 * 1000)) {
			String acc_token = getAccToken();
			// 获取票据
			String tickurl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + acc_token + "&type=jsapi";
			tick = MapUtils.getString(getParam(tickurl), "ticket");
			mapstic.put("ticket_weixin", tick);
			mapstic.put("ticket_time", datelong);
		}
		SortedMap<String,Object> signmap = new TreeMap<String,Object>();
		signmap.put("noncestr", UUIDGenerator.generatorUUID32());
		signmap.put("jsapi_ticket",tick);
		signmap.put("timestamp",datelong/1000);
		signmap.put("url",request.getParameter("url"));
		Map<String,Object> resultmap = new HashMap<String,Object>();
		resultmap.put("noncestr",MapUtils.getString(signmap,"noncestr"));
		resultmap.put("timestamp",datelong/1000);
		resultmap.put("signature", WeixinUtil.createPaySign(signmap, null));
		resultmap.put("appid",  Configs.payAppid);
		return resultmap;
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
