package com.xxx.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * Created by wangh on 2015/10/26.
 */
public  class UUIDUtils {
    //生成一个不携带'-'的32位UUID
    public static String generatorUUID32() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static void main(String[] args) throws Exception {
        double ddd = (new Random()).nextDouble();
        System.out.println(generatorUUID32());


        String extFiltersStr = "[{\"value\":\"aaaa\",\"field\":\"Case_workaddress\",\"type\":\"string\",\"comparison\":\"like\"}," +
                "           {\"value\":\"bbbb\",\"field\":\"Case_name\",\"type\":\"string\",\"comparison\":\"like\",\"operator\":\"or\"}," +
                "           [{\"field\":\"Staff_type1\",\"type\":\"numeric\",\"value\":\"107230101\",\"comparison\":\"eq\",\"operator\":\"or\"},{\"field\":\"Staff_type2\",\"type\":\"numeric\",\"value\":\"107230102\",\"comparison\":\"eq\",\"operator\":\"or\"}]" +
                "           {\"value\":\"gggg\",\"field\":\"Case_workaddress\",\"type\":\"string\",\"comparison\":\"like\"},\n" +
                "           [{\"field\":\"Staff_type3\",\"type\":\"numeric\",\"value\":\"107230103\",\"comparison\":\"eq\"},{\"field\":\"Staff_type4\",\"type\":\"numeric\",\"value\":\"107230104\",\"comparison\":\"eq\"}]" +
                "           [{\"field\":\"Staff_type5\",\"type\":\"numeric\",\"value\":\"107230105\",\"comparison\":\"eq\"}]]";

        extFiltersStr = "[{\"field\":\"Staff_type1\",\"type\":\"Numeric\",\"value\":\"107230101\",\"comparison\":\"eq\"},{\"operator\":\"or\"},{\"field\":\"Staff_type1\",\"type\":\"Numeric\",\"value\":\"107230101\",\"comparison\":\"eq\"},{\"operator\":\"and\"},[{\"field\":\"Staff_type1\",\"type\":\"Numeric\",\"value\":\"107230101\",\"comparison\":\"eq\"},{\"operator\":\"or\"},{\"field\":\"Staff_type1\",\"type\":\"Numeric\",\"value\":\"107230101\",\"comparison\":\"eq\"}]]";
        //ExtFilter.parse(extFiltersStr);

        //日期类型转换
        Calendar calendar = Calendar.getInstance();
        System.out.println(calendar.get(Calendar.YEAR));
        System.out.println(calendar.get(Calendar.MONTH));
        System.out.println(calendar.get(Calendar.DAY_OF_MONTH));

        //日期类型转换
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println(sdf.format(new Date()));

        System.out.println(sdf.parse("2015-12-11").getTime());
        System.out.println(sdf.parse("2015-12-12").getTime());

        //java.util.Date 可精确到毫秒
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
        Date d = new Date(new Date().getTime());
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(d));

    }
}
