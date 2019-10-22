package com.xxx.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Administrator on 2017/3/25.
 */
public class MathUtils {


    /**
     * Double保留2位小数四舍五入）
     * @param object
     * @return
     */
    public static Double formatObjectReturnDouble(Object object) {

        if(object==null)
            return null;

        DecimalFormat df = new DecimalFormat("######0.00");
        return Double.valueOf(df.format(object));
    }

    /**
     * Double保留1位小数（四舍五入）
     * @param object
     * @return
     */
    public static Double formatObjectReturnDouble1(Object object) {

        if(object==null)
            return null;

        DecimalFormat df = new DecimalFormat("######0.0");
        return Double.valueOf(df.format(object));
    }


}
