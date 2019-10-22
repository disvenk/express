package com.xxx.utils.date;

import org.apache.commons.lang3.time.DateUtils;

import java.io.File;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by wangh on 2016/8/28.
 */
public class DateTimeUtil {

    /**
     * 获取两个时间段的毫秒数 如果要转成秒，请将结果除以1000
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return
     */
    public static long getTimeInterval(Date startDate, Date endDate) {
        return (endDate.getTime() - startDate.getTime());
    }

    /**
     * 将日期格式转换为字符串形式，格式如：2004-12-16 17:24:27
     *
     * @param date
     * @return
     */
    public static String toDateString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    /**
     *  获取两个日期相差的月数
     * @param d1    较大的日期
     * @param d2    较小的日期
     * @return  如果d1>d2返回 月数差 否则返回0
     */
    public static int getMonthDiff(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        if (c1.getTimeInMillis() < c2.getTimeInMillis()) return 0;
        int year1 = c1.get(Calendar.YEAR);
        int year2 = c2.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH);
        int month2 = c2.get(Calendar.MONTH);
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        int day2 = c2.get(Calendar.DAY_OF_MONTH);
        // 获取年的差值 假设 d1 = 2015-8-16  d2 = 2011-9-30
        int yearInterval = year1 - year2;
        // 如果 d1的 月-日 小于 d2的 月-日 那么 yearInterval-- 这样就得到了相差的年数
        if (month1 < month2 || month1 == month2 && day1 < day2) yearInterval--;
        // 获取月数差值
        int monthInterval = (month1 + 12) - month2;
        if (day1 < day2) monthInterval--;
        monthInterval %= 12;
        return yearInterval * 12 + monthInterval;
    }

    /**
     * 将时间戳转换为字符串形式，格式如：2004-12-16 17:24:27
     *
     * @param l
     * @return
     */
    public static String toDateString(Long l) {
        if (l == null) return null;
        if (String.valueOf(l).length() == 10)
            l = l * 1000;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return simpleDateFormat.format(new Date(l));
    }

    /**
     * 将字符串日期格式转换为日期格式，字符串如：2004-12-16 17:24:27
     *
     * @param date
     * @return
     * @throws Exception
     */
    public static Date toDate(String date) throws Exception {
        String fmt = null;
        if (date.length() == 19)
            fmt = "yyyy-MM-dd HH:mm:ss";
        else if (date.length() == 10)
            fmt = "yyyy-MM-dd";
        else if (date.length() == 16)
            fmt = "yyyy-MM-dd HH:mm";
        else
            throw new Exception(MessageFormat.format("{0} 不是日期格式字符串，支持的日期格式长度有：10、16、19", date));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fmt);
        return simpleDateFormat.parse(date);  //throws ParseException
    }


    /**
     * 将有毫秒的日期类型转换为无毫秒的日期类型（参数可以是：java.sql.Date、java.sql.Timestamp）
     *
     * @param date
     * @return
     */
    public static Date convertToJavaUtilDate(Date date) {
        return new Date(date.getTime());
    }

    public static String toyyyyMMdd(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(date);
    }

    public static String toyyyyMMddHHmmss(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(date);
    }

    public static String toyyyyMMddHHmmssSSS(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return sdf.format(date);
    }
    public static Long getTimeMillisbyDate(String date, String pattern) {
        if (org.apache.commons.lang3.StringUtils.isNotBlank(date)) {
            SimpleDateFormat sf = new SimpleDateFormat(pattern);
            try {
                Date newdate = sf.parse(date);
                return (Long) (newdate.getTime() / 1000);
            } catch (ParseException e) {
//                throw new SystemException(500, "时间格式转换出错！");
            }
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(DateTimeUtil.toyyyyMMdd(new Date()));
        System.out.println(DateTimeUtil.toyyyyMMddHHmmss(new Date()));
        System.out.println(DateTimeUtil.toyyyyMMddHHmmssSSS(new Date()));

        Date date1 = new Date();
        Date date2 = DateUtils.addMonths(date1, 18);
        System.out.println(DateTimeUtil.toyyyyMMdd(date1));
        System.out.println(DateTimeUtil.toyyyyMMdd(date2));
        System.out.println(DateTimeUtil.getMonthDiff(date2,date1));

        System.out.println(System.currentTimeMillis());
        System.out.println(System.currentTimeMillis()/1000);
        System.out.println(System.currentTimeMillis()/1000*1000);
        System.out.println(toDateString(System.currentTimeMillis()/1000*1000));

        File file = new File("http://wx.qlogo.cn/mmopen/AAic76OPuWjc8eldXSD1JeiayuCa4t9D2lbCDUxYnRe2PKIVY63I54IcvvHYDv35XyCmnibpWLAFNibe8GT81pP5KsBSqOYciaVOT/0");
//        file.get
    }

}
