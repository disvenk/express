package com.xxx.utils;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class KuaiDi100 {

    /**
     * 快递查询接口方法
     * @param com ：快递公司代码，在http://www.kuaidi100.com/openapi  网上的技术文档里可以查询到
     * @param nu：快递单号，请勿带特殊符号，不支持中文（大小写不敏感）
     * @return 快递100返回的url，然后放入页面iframe标签的src即可
     * @see
     */
    public static String searchkuaiDiInfo(String com, String nu) {
        String content = "";
        //商家用户key值，在http://www.kuaidi100.com/openapi 申请的
        String key = "1949fc1aa47e87f1";
        try {
            URL url = new URL("http://www.kuaidi100.com/applyurl?key=" + key
                    + "&com=" + com
                            + "&nu=" + nu);
            URLConnection con = url.openConnection();
            con.setAllowUserInteraction(false);
            InputStream urlStream = url.openStream();
            byte b[] = new byte[10000];
            int numRead = urlStream.read(b);
            content = new String(b, 0, numRead);
            while (numRead != -1) {
                numRead = urlStream.read(b);
                if (numRead != -1) {
                    // String newContent = new String(b, 0, numRead);
                    String newContent = new String(b, 0, numRead, "UTF-8");
                    content += newContent;
                }
            }
            urlStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    public static void main(String[] agrs) {
        KuaiDi100 kuaidi = new KuaiDi100();
        String content = kuaidi.searchkuaiDiInfo("zhongtong", "454432565149");
    }

}
