package com.xxx.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证
 */
public class JavaValidate {
    /**
     * 是否手机号码
     * <p>
     * 先要整清楚现在已经开放了多少个号码段，国家号码段分配如下：
     * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
     * 联通：130、131、132、152、155、156、185、186
     * 电信：133、153、180、189、（1349卫通）
     *
     * @param mobile
     * @return
     */
   /* public static boolean isMobileNO(String mobile) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobile);
        return m.matches();
    }*/

    public static boolean isMobileNO(String mobile) {
        Pattern p = Pattern.compile("^1[34578]\\d{9}$");
        Matcher m = p.matcher(mobile);
        return m.matches();
    }

    public static void main(String[] args) {
        Double a =0.10;
        Double b =0.12;
        Double c =0.0;
        Double d =0.02;
        Double e = (a+c)+d;

        System.out.println(a+c+d+","+b);
    }

}
