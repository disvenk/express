package com.xxx.user;

public class Commo {

    /**
     * 获取默认图片路径
     * @return
     */
    public static String findDefaultImg() {
        return "";
    }

    /**
     * @Description: 获取支付类型对应的名称
     * @Author: Chen.zm
     * @Date: 2017/10/31 0031
     */
    public static String parsePaymentChannelName(String payType) {
        if (payType == null) return "";
        //支付渠道, ALIPAY:支付宝; WECHAT:微信; UPMP:银联 CASH:现金
        switch (payType) {
            case "ALIPAY": return "支付宝";
            case "WECHAT": return "微信";
            case "UPMP": return "微信";
            case "CASH": return "微信";
            default: return "其他";
        }
    }


    /**
     * @Description: 解析站内信通知类别
     * @Author: Chen.zm
     * @Date: 2018/1/4 0004
     */
    public static String parseNoticeType(Integer noticeType) {
        if (noticeType == null) return "";
        //通知类型   1 到件通知,  2 取件通知
        switch (noticeType) {
            case 1: return "到件通知";
            case 2: return "取件通知";
            default: return "其他";
        }
    }


    /**
     * @Description:结款状态
     * @Author: hanchao
     * @Date: 2017/12/18 0018
     */
    public static String getPayStatus(Integer type){
        if (type == null) return "";
        //0：全部  1：已结算 2:未结算
        switch (type) {
            case 0:
                return "全部";
            case 1:
                return "已结款";
            case 2:
                return "未结款";
        }
        return "";
    }

    /**
     * @Description:网点面单类型
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    public static String getIspaper(Integer type){
        if (type == null) return "";
        //面单类型    0 电子面单， 1 纸质面单
        switch (type) {
            case 0:
                return "电子面单";
            case 1:
                return "纸质面单";
        }
        return "";
    }

    /**
     * @Description:收费状态
     * @Author: hanchao
     * @Date: 2018/1/5 0005
     */
    public static String parsePayStatus(Integer type){
        if (type == null) return "";
        //收费状态  0：未支付，    1：已支付
        switch (type) {
            case 0:
                return "未支付";
            case 1:
                return "已支付";
        }
        return "";
    }

    /**
     * @Description:上门类型
     * @Author: hanchao
     * @Date: 2018/1/10 0010
     */
    public static String parseTodoorype(Integer type){
        if (type == null) return "";
        //上门类型 1派件上门,  2上门取件
        switch (type) {
            case 1:
                return "派件上门";
            case 2:
                return "上门取件";
        }
        return "";
    }

    /**
     * @Description:取件方式
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    public static String parseReceiveType(Integer type){
        if (type == null) return "";
        //取件方式    0 自行取件，   1 派件上门
        switch (type) {
            case 0:
                return "自行取件";
            case 1:
                return "派件上门";
        }
        return "";
    }

    /**
     * @Description:寄件方式
     * @Author: hanchao
     * @Date: 2018/1/10 0010
     */
    public static String parseSendType(Integer type){
        if (type == null) return "";
       //取件方式    0 自行寄件，   1 上门取件
        switch (type) {
            case 0:
                return "自行寄件";
            case 1:
                return "上门取件";
        }
        return "";
    }

    /**
     * @Description:寄件订单状态
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    /*public static String parseOrderStatus(Integer type){
        if (type == null) return "";
        //订单状态    1 待接单，  2 待取件，  3 已取件，  4 待支付，  5 已支付，  6 已取消
        switch (type) {
            case 1:
                return "待接单";
            case 2:
                return "待取件";
            case 3:
                return "已取件";
            case 4:
                return "待支付";
            case 5:
                return "已支付";
            case 6:
                return "已取消";
        }
        return "";
    }*/
    public static String parseOrderStatus(Integer type){
        if (type == null) return "";
        //订单状态    1 待取件，  2 已取件，  3 已支付，  4 已取消
        switch (type) {
            case 1:
                return "待取件";
            case 2:
                return "已取件";
            case 3:
                return "已支付";
            case 4:
                return "已取消";
        }
        return "";
    }

    /**
     * @Description: 收件订单状态
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    public static String parseReceiveOrderStatus(Integer type){
        if (type == null) return "";
        //订单状态     订单状态    1 未到达,  2 已到达,  3 待接单,  4 已接单,  5 待签收,  6 已签收,  7 已拒收 8 已退回
        switch (type) {
            case 1:
                return "未到达";
            case 2:
                return "已到达";
            case 3:
                return "待接单";
            case 4:
                return "已接单";
            case 5:
                //return "待签收";
                return "已接单";
            case 6:
                return "已签收";
            case 7:
                return "已拒收";
            case 8:
                return "已退回";
        }
        return "";
    }
}
