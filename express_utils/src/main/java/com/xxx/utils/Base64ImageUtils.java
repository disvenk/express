package com.xxx.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Created by wanghua on 17/3/9.
 */
public class Base64ImageUtils {
    /**
     * 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
     *
     * @param imgFilePath
     * @return 返回Base64编码过的字节数组字符串
     */
    public static String getImageBase64Str(String imgFilePath) {
        byte[] data = null;
        try {
            InputStream in = new FileInputStream(imgFilePath);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }


    /**
     * 将图片Base64字符串转换为字节流
     * @param imageBase64Str 图片Base64字符串
     * @return
     */
    public static byte[] imageBase64StrToBytes(String imageBase64Str) {
        if (StringUtils.isBlank(imageBase64Str)) return null;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            return decoder.decodeBuffer(imageBase64Str);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 对字节数组字符串进行Base64解码并生成图片
     *
     * @param imageBase64Str 图片Base64字符串
     * @param imgFilePath    图片输出地址
     * @return
     */
    public static boolean generateImage(String imageBase64Str, String imgFilePath) {
        if (StringUtils.isBlank(imageBase64Str)) return false;
        OutputStream out = null;
        try {
            byte[] bytes = imageBase64StrToBytes(imageBase64Str);
            for (int i = 0; i < bytes.length; ++i)
                if (bytes[i] < 0)
                    bytes[i] += 256;
            out = new FileOutputStream(imgFilePath);
            out.write(bytes);
            out.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (out != null) out.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


//    /**
//     * @Title: GetImageStrFromUrl      【注: 会出现base64不完整的问题
//     * @Description: (将一张网络图片转化成Base64字符串)
//     * @param imgURL 网络资源位置
//     * @return Base64字符串
//     */
//    public static String getImageStrFromUrl(String imgURL) {
//        byte[] data = null;
//        try {
//            // 创建URL
//            URL url = new URL(imgURL);
//            // 创建链接
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.setConnectTimeout(5 * 1000);
//            InputStream inStream = conn.getInputStream();
//            data = new byte[inStream.available()];
//            inStream.read(data);
//            inStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        // 对字节数组Base64编码
//        com.cloopen.rest.sdk.utils.encoder.BASE64Encoder encoder = new com.cloopen.rest.sdk.utils.encoder.BASE64Encoder();
//        // 返回Base64编码过的字节数组字符串
//        return encoder.encode(data);
//    }


    /**
     * 链接url下载图片至本地
     * @param urlList 图片地址
     * @param dir   下载位置
     */
    private static void downloadPicture(String urlList, String dir) {
        URL url = null;
        try {
            url = new URL(urlList);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(new File(dir));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取在线url图片base64
     *    因直接转换base64会导致图片不完整    所以采用下载至本地 转码为base64 再将本地图片删除的策略
     * @param imgURL 图片路径
     * @return
     */
    public static String getImageBase64FromUrl(String imgURL) {
        String base64 = "";
        try {
            String dir = Base64ImageUtils.class.getClassLoader().getResource("../../").getPath()+ "assets/downloadImgs/";
            //判断文件夹是否存在  不存在则创建，   该文件夹仅仅用于存放临时图片
            File file =new File(dir);
            if  (!file.exists() && !file .isDirectory()) {
                file .mkdir();
            }
            //采用时间戳标识图片名
            dir += System.currentTimeMillis() + ".jpg";
            downloadPicture(imgURL, dir);
            base64 = getImageBase64Str(dir);
            boolean success = (new File(dir)).delete();
            if (!success) System.out.println("删除图片失败");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return base64;
    }


    public static void main(String[] args) {
//        // 测试从Base64编码转换为图片文件
//        String base64Str = "xxxxx";
//        generateImage(base64Str, "C:\\a1.jpg");
//
//        // 测试从图片文件转换为Base64编码
//        System.out.println(getImageBase64Str("C:\\a2.jpg"));
//        System.out.println(getImageStrFromUrl("http://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1512741524514&di=06602b15a5c7d7f2f0a8bb627ec98a0a&imgtype=0&src=http%3A%2F%2Fimgs.technews.cn%2Fwp-content%2Fuploads%2F2014%2F10%2FBaidu.jpg"));
//        System.out.println(getImageStrFromUrl("http://kaa.oss-cn-hangzhou.aliyuncs.com/264?Expires=1828086915&OSSAccessKeyId=LTAI37QkCpGXzcKJ&Signature=nhSgUgHV9ENHb5%2BMsK8wBa2U%2BSk%3D"));


    }
}
