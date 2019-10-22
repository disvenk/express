package com.xxx.utils.date;

/**
 * @Description:
 * @Author: disvenk.dai
 * @Date: 下午 2:22 2018/1/15 0015
 */
public class SwitchUtils {
    public static String orderStatus(Integer status){
        String statusStr = null;
        switch (status){
            case 1 :
                statusStr="未到达";
                break;
            case  2 :
                statusStr="已到达";
                break;
            case 3 :
                statusStr="待接单";
                break;
            case 4 :
                statusStr="已接单";
                break;
            case 5 :
                statusStr="待签收";
                break;
            case 6 :
                statusStr="已签收";
                break;
            case 7 :
                statusStr="已退回";
            default:
                statusStr="未到达";
                break;
        }
        return statusStr;
    }
}
