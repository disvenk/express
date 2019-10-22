package com.xxx.clients.controller;


import com.xxx.clients.service.WeChatService;
import com.xxx.core.config.Configs;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.ExpSocial;
import com.xxx.model.business.ExpUser;
import com.xxx.user.form.IconForm;
import com.xxx.user.security.CurrentUser;
import com.xxx.user.security.GenericLogin;
import com.xxx.user.service.AccountService;
import com.xxx.user.service.UploadFileService;
import com.xxx.user.utils.SessionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@RequestMapping("homePage")
public class HomePageController {

    @Autowired
    private WeChatService weChatService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private UploadFileService uploadFileService;

    /**
     * 获取微信签名
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getshareinfo", method = {RequestMethod.GET})
    public ResponseEntity getShareInfo(HttpServletRequest request) throws Exception {
        Map map = weChatService.gettick(request);
        return new ResponseEntity(new RestResponseEntity(100, "成功", map), HttpStatus.OK);
    }

    /**
     * 发送客服消息
     */
    @RequestMapping(value = "/sendMessageHtml", method = {RequestMethod.GET})
    public ResponseEntity sendMessage(HttpServletRequest request) throws Exception {
//        Map map = weChatService.sendTextMessage(request.getParameter("openId"), request.getParameter("content"));
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }


    /**
     * 微信进入统一入口
     *     用于获取用户openId
     */
    @RequestMapping(value = "/weixinAuthorizationHtml", method = {RequestMethod.GET})
    public String weixinAuthorization(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String redirectUrl = "http%3a%2f%2f" + Configs.openIdUrl + "%2fhomePage%2fhomePageHtml";
        String id = request.getParameter("id") == null ? "" : request.getParameter("id");
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?" +
                "appid=" + Configs.payAppid +
                "&redirect_uri=" + redirectUrl + //【需用encode编码
                "&response_type=code" +
                "&scope=snsapi_base" + //snsapi_base（默认）   snsapi_userinfo （需授权）
                "&state=" + id +
                "#wechat_redirect";
        return "redirect:" + new String(url.getBytes("UTF-8"));
    }

    /**
     * 学生端首页
     *  处理openId 与 账户绑定， 身份信息不存在时，跳转至登录页面
     */
    @RequestMapping(value = "/homePageHtml", method = {RequestMethod.GET})
    public String homePageHtml(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        System.out.println("-------- 进入homePageHtml ------");
        //校验是否微信入口，获取openId
        String openId = null;
        String code = request.getParameter("code");//微信跳转携带的code  用于获取openId
        System.out.println("-------- code: "+code+" ----------");
        if (StringUtils.isNotBlank(code)) {
            openId = weChatService.findOpenId(code);
        }
        System.out.println("-------- openId: "+openId+" ----------");
        //当openId不存在时，尝试获取Session中的openId信息
        if (StringUtils.isBlank(openId))
            openId = SessionUtils.getSession(request, SessionUtils.OPEN_ID) == null ? null :
                    SessionUtils.getSession(request, SessionUtils.OPEN_ID).toString();
        System.out.println("-------- 服务器openId: "+openId+" ----------");

        //尝试获取userId
        Integer userId = SessionUtils.getSession(request, SessionUtils.USER_ID) == null ? null :
                (Integer) SessionUtils.getSession(request, SessionUtils.USER_ID);
        System.out.println("-------- userId: "+userId+" ----------");
        //当openId,userId 均不存在时， 跳转至登录页面
        if (StringUtils.isBlank(openId) && userId == null)
            return "redirect:" + "loginHtml";

        //当userId为空时， 尝试通过openId获取userId
        ExpSocial social = StringUtils.isBlank(openId) ? null :
                (ExpSocial) weChatService.get2(ExpSocial.class, "location", "WECHAT", "uid", openId);
        if (userId == null) {
            if (social == null || social.getUserId() == null) {
                //当openId与userId绑定信息不存在时
                //缓存openId, 并跳转至登录页面
                SessionUtils.setSession(response, SessionUtils.OPEN_ID, 3600 * 3, openId);
                return "redirect:" + "loginHtml";
            } else {
                userId = social.getUserId();
            }
        }
        System.out.println("-------- 通过openId获取的userId: "+userId+" ----------");

        //获取用户信息，  不存在则跳转至登录
        ExpUser user = weChatService.get2(ExpUser.class, userId);
        if (user == null) {
            //清空userId缓存  (将缓存时间重置为1秒)
            SessionUtils.setSession(response, SessionUtils.USER_ID, 1, userId);
            return "redirect:" + "loginHtml";
        }
        //校验用户是否绑定openId
        if (StringUtils.isNotBlank(openId)) {
            System.out.println("--------- 开始绑定openId ---------");
            if (social == null) {
                social = new ExpSocial();
                social.setLocation("WECHAT");
                social.setUid(openId);
                social.setUserId(userId);
                weChatService.upsert2(social);
                //校验是否有头像
                if (user.getIcon() == null) {
                    System.out.println("--------- 初始化头像和昵称 ---------");
                    //初始化头像和昵称
                    Map<String, Object> m = weChatService.getUserInfo(openId);
                    user.setIcon(uploadFileService.saveOssUploadFileByUrl(m.get("headimgurl").toString()).toString());
                    user.setUserNickname(m.get("nickname").toString());
                    weChatService.upsert2(user);
                }
            } else if (social.getUserId() == null || social.getUserId() != userId.intValue()){
                social.setUserId(userId);
                weChatService.upsert2(social);
            }
            System.out.println("--------- 绑定openId结束 ---------");
        }

        System.out.println("--------- 获取登录信息 ---------");
        //获取登录信息
        GenericLogin genericLogin = accountService.returnGenericLogin(user);
        SessionUtils.setSession(response, SessionUtils.USER_ID, 3600 * 3, genericLogin.userId);
        SessionUtils.setSession(response, SessionUtils.LOGIN_TYPE, 3600 * 3, genericLogin.loginType);
        SessionUtils.setSession(response, SessionUtils.OPEN_ID, 3600 * 3, openId);

        modelMap.put("authToken", CurrentUser.generateAuthToken(genericLogin));
        modelMap.put("userStatus", genericLogin.userStatus);
        modelMap.put("openId", openId);

        System.out.println("--------- 获取登录结束 ---------");

        return "homePage";
    }

    /**
     * 登录页面
     */
    @RequestMapping(value = "/loginHtml", method = {RequestMethod.GET})
    public String loginHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "login";
    }

    /**
     * 绑定网店（学校）
     */
    @RequestMapping(value = "/selectSchoolHtml", method = {RequestMethod.GET})
    public String selectSchoolHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "selectSchool";
    }

    /**
     * 个人中心
     */
    @RequestMapping(value = "/infoHtml", method = {RequestMethod.GET})
    public String infoHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "info";
    }

    /**
     * @Description: 修改头像
     * @Author: Chen.zm
     * @Date: 2017/11/8 0008
     */
    @RequestMapping(value = "/updateIcon", method = {RequestMethod.POST})
    public ResponseEntity updateIcon(@RequestBody IconForm form) throws Exception {
        if (org.apache.commons.lang3.StringUtils.isBlank(form.icon))
            return new ResponseEntity(new RestResponseEntity(110, "头像不能为空", null), HttpStatus.OK);
        String requestUrl = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIA_ID";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", weChatService.getAccToken()).replace("MEDIA_ID", form.icon);
        ExpUser user = weChatService.get2(ExpUser.class, CurrentUser.get().userId);
        if (user == null) {
            throw new ResponseEntityException(200, "账户不存在");
        }
        user.setIcon(uploadFileService.saveOssUploadFileByUrl(requestUrl).toString());
        weChatService.upsert2(user);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    @RequestMapping(value = "/postHtml", method = {RequestMethod.GET})
    public String postHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "post";
    }

    @RequestMapping(value = "/postWay1Html", method = {RequestMethod.GET})
    public String postWay1Html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "postWay1";
    }

    @RequestMapping(value = "/postWay2Html", method = {RequestMethod.GET})
    public String postWay2Html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "postWay2";
    }

    @RequestMapping(value = "/postWay2OrderHtml", method = {RequestMethod.GET})
    public String postWay2OrderHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "postWay2Order";
    }

    @RequestMapping(value = "/receiveHtml", method = {RequestMethod.GET})
    public String receiveHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "receive";
    }

    @RequestMapping(value = "/checkHtml", method = {RequestMethod.GET})
    public String checkHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "check";
    }

    @RequestMapping(value = "/settingHtml", method = {RequestMethod.GET})
    public String settingHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "setting";
    }

    @RequestMapping(value = "/paySuccessHtml", method = {RequestMethod.GET})
    public String paySuccessHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "paySuccess";
    }

    @RequestMapping(value = "/receiveOrderHtml", method = {RequestMethod.GET})
    public String receiveOrderHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "receiveOrder";
    }

    @RequestMapping(value = "/sendOrderHtml", method = {RequestMethod.GET})
    public String sendOrderHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "sendOrder";
    }

    @RequestMapping(value = "/takeOrderHtml", method = {RequestMethod.GET})
    public String takeOrderHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "takeOrder";
    }

    @RequestMapping(value = "/addressListHtml", method = {RequestMethod.GET})
    public String addressListHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "addressList";
    }

    @RequestMapping(value = "/addressAddEditHtml", method = {RequestMethod.GET})
    public String addressAddEditHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "addressAddEdit";
    }

    @RequestMapping(value = "/myGetOrderHtml", method = {RequestMethod.GET})
    public String myGetOrderHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "myGetOrder";
    }

    @RequestMapping(value = "/myTransOrderHtml", method = {RequestMethod.GET})
    public String myTransOrderHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "myTransOrder";
    }

    @RequestMapping(value = "/myTakeOrderHtml", method = {RequestMethod.GET})
    public String myTakeOrderHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "myTakeOrder";
    }

    @RequestMapping(value = "/myPostOrderHtml", method = {RequestMethod.GET})
    public String myPostOrderHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "myPostOrder";
    }
}
