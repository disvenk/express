package com.xxx.admin.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("home")
public class HomeController {



    @RequestMapping(value = "/loginHtml", method = {RequestMethod.GET})
    public String loginHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "login";
    }

    @RequestMapping(value = "/welcomeHtml", method = {RequestMethod.GET})
    public String welcomeHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "welcome";
    }

    @RequestMapping(value = "/indexHtml", method = {RequestMethod.GET})
    public String indexHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "index";
    }
    /*
    *
    * 客户管理
    *
    * */
    @RequestMapping(value = "/clientManageHtml", method = {RequestMethod.GET})
    public String clientManageHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/clientManage/clientManage";
    }

    @RequestMapping(value = "/clientManageEditHtml", method = {RequestMethod.GET})
    public String clientManageEditHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/clientManage/clientManageEdit";
    }

    @RequestMapping(value = "/clientManageAddressHtml", method = {RequestMethod.GET})
    public String clientManageAddressHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/clientManage/clientManageAddress";
    }

    @RequestMapping(value = "/clientManageReceiveAddressHtml", method = {RequestMethod.GET})
    public String clientManageReceiveAddressHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/clientManage/clientManageReceiveAddress";
    }
    /*
    *
    * 平台人员管理
    *
    * */
    @RequestMapping(value = "/platManageHtml", method = {RequestMethod.GET})
    public String platManageHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/platManage/platManage";
    }

    @RequestMapping(value = "/platManageEditHtml", method = {RequestMethod.GET})
    public String platManageEditHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/platManage/platManageEdit";
    }

    @RequestMapping(value = "/platManageAddHtml", method = {RequestMethod.GET})
    public String platManageAddHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/platManage/platManageAdd";
    }
    /*
    *
    * 收件管理
    *
    * */
    @RequestMapping(value = "/getExpressManageHtml", method = {RequestMethod.GET})
    public String getExpressManageHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/getManage/getExpressManage";
    }

    @RequestMapping(value = "/getOrderManageHtml", method = {RequestMethod.GET})
    public String getOrderManageHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/getManage/getOrderManage";
    }

    @RequestMapping(value = "/getOrderManageEditHtml", method = {RequestMethod.GET})
    public String getOrderManageEditHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/getManage/getOrderManageEdit";
    }

    @RequestMapping(value = "/pushHistoryHtml", method = {RequestMethod.GET})
    public String pushHistoryHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/getManage/pushHistory";
    }

    @RequestMapping(value = "/courierManageHtml", method = {RequestMethod.GET})
    public String courierManageHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "getManage/courierManage";
    }

    @RequestMapping(value = "/courierManageEditHtml", method = {RequestMethod.GET})
    public String courierManageEditHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "getManage/courierManageEdit";
    }
    @RequestMapping(value = "/courierManageAddHtml", method = {RequestMethod.GET})
    public String courierManageAddHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "getManage/courierManageAdd";
    }
    /*
    *
    * 寄件管理
    *
    * */
    @RequestMapping(value = "/postOrderManageHtml", method = {RequestMethod.GET})
    public String postOrderManageHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "postManage/postOrderManage";
    }
    @RequestMapping(value = "/postOrderManageEditHtml", method = {RequestMethod.GET})
    public String postOrderManageEditHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "postManage/postOrderManageEdit";
    }
    @RequestMapping(value = "/expressOrderManageHtml", method = {RequestMethod.GET})
    public String expressOrderManageHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "postManage/expressOrderManage";
    }
    @RequestMapping(value = "/expressOrderAddHtml", method = {RequestMethod.GET})
    public String expressOrderAddHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "postManage/expressOrderAdd";
    }
    @RequestMapping(value = "/expressCostManageHtml", method = {RequestMethod.GET})
    public String expressCostManageHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "postManage/expressCostManage";
    }
    @RequestMapping(value = "/expressCostEditHtml", method = {RequestMethod.GET})
    public String expressCostEditHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "postManage/expressCostEdit";
    }
    @RequestMapping(value = "/expressCostAddHtml", method = {RequestMethod.GET})
    public String expressCostAddHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "postManage/expressCostAdd";
    }
    @RequestMapping(value = "/courierCostManageHtml", method = {RequestMethod.GET})
    public String courierCostManageHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "postManage/courierCostManage";
    }
    @RequestMapping(value = "/courierCostEditHtml", method = {RequestMethod.GET})
    public String courierCostEditHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "postManage/courierCostEdit";
    }
    @RequestMapping(value = "/courierCostAddHtml", method = {RequestMethod.GET})
    public String courierCostAddHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "postManage/courierCostAdd";
    }
    @RequestMapping(value = "/companyManageHtml", method = {RequestMethod.GET})
    public String companyManageHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "postManage/companyManage";
    }
    @RequestMapping(value = "/companyManageEditHtml", method = {RequestMethod.GET})
    public String companyManageEditHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "postManage/companyManageEdit";
    }
    @RequestMapping(value = "/companyManageAddHtml", method = {RequestMethod.GET})
    public String companyManageAddHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "postManage/companyManageAdd";
    }
    @RequestMapping(value = "/branchHtml", method = {RequestMethod.GET})
    public String branchHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "branchManage/branchManage";
    }
    @RequestMapping(value = "/branchEditHtml", method = {RequestMethod.GET})
    public String branchEditHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "branchManage/branchManageEdit";
    }
    @RequestMapping(value = "/branchAddHtml", method = {RequestMethod.GET})
    public String branchAddHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "branchManage/branchManageAdd";
    }
    /*
    *
    * 短信历史
    *
    * */
    @RequestMapping(value = "/messageHistoryHtml", method = {RequestMethod.GET})
    public String messageHistoryHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "messageHistory/messageHistory";
    }
    /*
    *
    * 对账管理
    *
    * */
    @RequestMapping(value = "/accountManageHtml", method = {RequestMethod.GET})
    public String accountManageHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "accountManage/accountManage";
    }

    /*
    * 系统设置
    * */
    @RequestMapping(value = "/menuManageHtml", method = {RequestMethod.GET})
    public String menuManageHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "permissionManage/menuManage";
    }

    @RequestMapping(value = "/roleManageHtml", method = {RequestMethod.GET})
    public String roleManageHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "permissionManage/roleManage";
    }

    @RequestMapping(value = "/userManageHtml", method = {RequestMethod.GET})
    public String userManageHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "permissionManage/userManage";
    }
}
