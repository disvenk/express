package com.xxx.pay.weixin;

import com.xxx.utils.MD5;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.*;


/**
 * Created by wangh on 2016/7/1.
 */
public class WeixinUtil {

    public static boolean isResponseError(String responseText) {
        return (responseText.contains("errcode") && responseText.contains("errmsg"));
    }

    //访问此链接必须在微信客户端中，否则会报错！
    public static String getOauth2AuthorizeUrl(String appid, String redirectUri, Scope scope, String state) {
        return MessageFormat.format("https://open.weixin.qq.com/connect/oauth2/authorize?appid={0}&redirect_uri={1}&response_type=code&scope={2}&state={3}#wechat_redirect"
                , appid
                , redirectUri
                , scope.toString()
                , state);
    }

    /**
     * 微信支付签名算法（通用）
     *
     * @param parameters
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String createPaySign(SortedMap<String, Object> parameters, String key) {
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();//所有参与传参的参数按照accsii排序（升序）
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
//        sb.append("key=" + key);
//        String sign = MD5.MD5Encode(sb.toString(), "UTF-8").toUpperCase();
        String sign = null;
        if(!StringUtils.isBlank(key)){
            sb.append("key=" + key);
            sign = MD5.MD5Encode(sb.toString(),"UTF-8").toUpperCase();
        }else {
            sb = sb.deleteCharAt(sb.length()-1);
            try
            {
                MessageDigest crypt = MessageDigest.getInstance("SHA-1");
                crypt.reset();
                crypt.update(sb.toString().getBytes("UTF-8"));
                sign = byteToHex(crypt.digest());
            }
            catch (NoSuchAlgorithmException e)
            {
                e.printStackTrace();
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
        }
        return sign;
    }


    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

}
