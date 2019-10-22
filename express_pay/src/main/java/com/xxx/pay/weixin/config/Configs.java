package com.xxx.pay.weixin.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Created by liuyangkly on 15/6/27.
 */
public class Configs {
    private static Configuration configs;
    public static String payAppid;
    public static String payAppSecret;
    public static String mchId;   //商户号
    //key设置路径：微信商户平台(pay.weixin.qq.com)-->账户设置-->API安全-->密钥设置
    //这里特别注意，公众平台的密钥和商户号的密钥是不一样的！！！微信支付审核成功之后会收到一封邮件，邮件中有appid 商户号，商户后台登录上号和密码，登录到商户后台：账户设置-安全设置-切换到API安全，下载证书，下面有一个api密匙，进去填写一个字符串 ，保存，后续两次签名都是用的这个手动设置的key！
    public static String key;  //商户Key
    public static String notifyUrl;  //异步接收微信支付结果通知的回调地址
    static {
        init("weixin.properties");
    }

    // 根据文件名读取配置文件，文件后缀名必须为.properties
    private static void init(String filePath) {
        try {
            configs = new PropertiesConfiguration(filePath);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }

        if (configs == null) {
            throw new IllegalStateException("can`t find file by path:" + filePath);
        }

        payAppid = configs.getString("payAppid");
        payAppSecret = configs.getString("payAppSecret");
        mchId = configs.getString("payMerchantNumber");
        key = configs.getString("payMerchantKey");
        notifyUrl = configs.getString("notifyUrl");
    }
}
