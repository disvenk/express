package com.xxx.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.xxx.utils.config.SmsConfigs;
import com.xxx.utils.contract.SimpleResult;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import java.io.StringReader;

/**
 * Created by Chen.zm on 17/11/8.
 */
public class SmsUtils {


    /**
     *  请查阅 阿里云短信服务 文档中心页面:"https://help.aliyun.com/document_detail/55284.html?spm=5176.10629532.106.1.335db5a1lgGLMN"
      * @param receiverPhone 短信接收者的手机号
      * @param code        模板id,
      * @param param        模板参数,
      * @return
      */
    public static String send(String receiverPhone, String code, String param) {
        try {
            //初始化ascClient需要的几个参数
            final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
            final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）

            //初始化ascClient,暂时不支持多region（请勿修改）
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", SmsConfigs.getApiId(), SmsConfigs.getApiKey());
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);
            //组装请求对象
            SendSmsRequest request = new SendSmsRequest();
            //使用post提交
            request.setMethod(MethodType.POST);
            //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
            request.setPhoneNumbers(receiverPhone);
            //必填:短信签名-可在短信控制台中找到
            request.setSignName("捷杰送达");
            //必填:短信模板-可在短信控制台中找到
            request.setTemplateCode(code);
            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
            //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
            request.setTemplateParam(param);
            //可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
            //request.setSmsUpExtendCode("90997");
            //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
//            request.setOutId("yourOutId");
            //请求失败这里会抛ClientException异常
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
                //请求成功
                return "SUCCESS";
            } else {
                return "ERROR";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
       }
    }


//    /**
//     * @param receiverPhone 短信接收者的手机号
//     * @param content        内容,请查阅互亿无线文档中心页面:"http://www.ihuyi.com/api/sms.html"
//     * @return
//     */
//    public static String send(String receiverPhone, String content) {
//
//        //******************************注释****************************************************************
//        //*调用发送短信的接口发送短信                                                                      *
//        //*参数顺序说明：                                                                                  *
//        //**************************************************************************************************
//        try {
//            HttpClient client = new HttpClient();
//            String url = "http://106.ihuyi.com/webservice/sms.php?method=Submit" ;
//            String body = "account=" + SmsConfigs.getApiId()
//                    + "&password=" + SmsConfigs.getApiKey()
//                    + "&mobile=" + receiverPhone
//                    + "&content=" + content;
//            SimpleResult result = client.request(HttpClient.Method.POST, url, body, null);
//            SAXReader saxreader = new SAXReader();
//            Document doc = saxreader.read(new StringReader(result.getMessage()));
//            String returnMsg = doc.selectSingleNode("/").getStringValue();
//            String[] returnMsgs = returnMsg.split("\n");
//            if (returnMsgs.length < 2) returnMsgs = new String[2];
//            if ("2".equals(returnMsgs[1])) {
//                return "SUCCESS";
//            } else {
//                //异常返回输出错误码和错误信息
//                System.out.println("错误码=" +returnMsgs[1] + " 错误信息= " + returnMsgs[2]);
//                return "ERROR";
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "ERROR";
//        }
//    }

    public static void main(String[] args) {
         SmsUtils.send("17715580265", "SMS_121085084", "{\"code\":\"123\"}");
    }
}
