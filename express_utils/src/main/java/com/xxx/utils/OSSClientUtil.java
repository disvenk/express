package com.xxx.utils;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.BucketInfo;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;

import java.io.*;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Date;

/**
 * Created by wanghua on 17/3/8.
 */
public class OSSClientUtil {
    private static String endpoint;
    private static String accessKeyId;
    private static String accessKeySecret;
    private static String bucketName1;      //存储空间
    private static String filedir;          //文件存储目录
    private static OSSClient ossClient;     //线程安全的
    private static BucketInfo bucketInfo1;  //存储空间

    static {
        PropertiesTools pt = new PropertiesTools("/aliyunOss.properties");
        endpoint = pt.getPropValue("oss.endpoint");
        accessKeyId = pt.getPropValue("oss.accessKeyId");
        accessKeySecret = pt.getPropValue("oss.accessKeySecret");
        bucketName1 = pt.getPropValue("oss.bucketName1");
        filedir = pt.getPropValue("oss.filedir");

        ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        bucketInfo1 = ossClient.getBucketInfo(bucketName1);
    }

    /**
     * Base64图片上传，如果同名文件会覆盖服务器上的
     *
     * @param key        请使用UploadFile实体ID
     * @param base64Str
     * @param fileSuffix
     * @return
     */
    public static PutObjectResult putBase64Img(String key, String base64Str, String fileSuffix) {
        int i = base64Str.indexOf(",");
        if (i != -1) base64Str = base64Str.substring(i + 1);
        byte[] bytes = Base64ImageUtils.imageBase64StrToBytes(base64Str);
        return putObject(key, bytes, fileSuffix);
    }

    /**
     * 对象上传，如果同名文件会覆盖服务器上的
     *
     * @param key        请使用UploadFile实体ID
     * @param bytes
     * @param fileSuffix
     * @return
     */
    public static PutObjectResult putObject(String key, byte[] bytes, String fileSuffix) {
        //如果不是用base64上传，byte[] bytes参数改为一个Inputstream
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(bytes.length);//使用Inputstream后填写Inputstream.available()
        objectMetadata.setCacheControl("no-cache");
        objectMetadata.setHeader("Pragma", "no-cache");
        objectMetadata.setContentType(getContentType(fileSuffix));
        //objectMetadata.setContentDisposition("inline;filename=" + fileName);
        return ossClient.putObject(bucketName1, key, new ByteArrayInputStream(bytes), objectMetadata);
    }

    public static PutObjectResult putObjectUrl(String key, String url, String fileSuffix) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
//        objectMetadata.setContentLength(bytes.length);
        objectMetadata.setCacheControl("no-cache");
        objectMetadata.setHeader("Pragma", "no-cache");
        objectMetadata.setContentType(getContentType(fileSuffix));

        InputStream inputStream = new URL(url).openStream();
        return ossClient.putObject(bucketName1, key , inputStream,objectMetadata);
    }

    /**
     * Object是否存在
     *
     * @return
     */
    public static boolean doesObjectExist(String key) {
        return ossClient.doesObjectExist(bucketName1, key);
    }

    /**
     * 删除单个Object
     *
     * @return
     */
    public static void deleteObject(String key) {
        ossClient.deleteObject(bucketName1, key);
    }

    /**
     * 获取文件的相对路径
     *
     * @param fileName 文件名称，格式如：abc.png
     * @return 返回格式如：
     */
    public static String getObjectRelativePath(String fileName) {
        Bucket bucket = bucketInfo1.getBucket();
        String fileFullPath = MessageFormat.format("{0}.{1}/{2}", bucket.getName(), bucket.getExtranetEndpoint());
        return fileFullPath;
    }

    /**
     * 获得对象url链接（此方法不会向OSS服务器发送请求，使用频率高也无妨）
     *
     * @param key
     * @return
     */
    public static String getObjectUrl(String key) {
        if (org.apache.commons.lang3.StringUtils.isBlank(key)) return "";
        // 设置URL过期时间为10年 3600l* 1000*24*365*10
        Date expiration = new Date(new Date().getTime() + 3600l * 1000 * 24 * 365 * 10);
        // 生成URL
        URL url = ossClient.generatePresignedUrl(bucketName1, key, expiration);

        if (url != null)
            return url.toString();
        return null;
    }

    /**
     * Description: 判断OSS服务文件上传时文件的contentType
     *
     * @param FilenameExtension 文件后缀
     * @return String
     */
    public static String getContentType(String FilenameExtension) {
        if (FilenameExtension.equalsIgnoreCase("bmp")) {
            return "image/bmp";
        } else if (FilenameExtension.equalsIgnoreCase("gif")) {
            return "image/gif";
        } else if (FilenameExtension.equalsIgnoreCase("png")) {
            return "image/png";
        } else if (FilenameExtension.equalsIgnoreCase("jpe") ||
                FilenameExtension.equalsIgnoreCase("jpeg") ||
                FilenameExtension.equalsIgnoreCase("jpg") ||
                FilenameExtension.equalsIgnoreCase("jfif")) {
            return "image/jpeg";
        } else if (FilenameExtension.equalsIgnoreCase("html")) {
            return "text/html";
        } else if (FilenameExtension.equalsIgnoreCase("txt")) {
            return "text/plain";
        } else if (FilenameExtension.equalsIgnoreCase("vsd")) {
            return "application/vnd.visio";
        } else if (FilenameExtension.equalsIgnoreCase("pptx") ||
                FilenameExtension.equalsIgnoreCase("ppt")) {
            return "application/vnd.ms-powerpoint";
        } else if (FilenameExtension.equalsIgnoreCase("docx") ||
                FilenameExtension.equalsIgnoreCase("doc")) {
            return "application/msword";
        } else if (FilenameExtension.equalsIgnoreCase("xml")) {
            return "text/xml";
        } else if (FilenameExtension.equalsIgnoreCase("mp4")) {
            return "video/mp4";
        }
        return "image/jpeg";
    }

    /**
     * 销毁
     */
    public void destory() {
        if (ossClient != null)
            ossClient.shutdown();
    }

    public static void main(String[] args) throws IOException {
        String key = "354617";

        //上传图片
//        String jpgStr = "data:image/jpeg;base64,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";
//        File file = new File("http://wx.qlogo.cn/mmopen/AAic76OPuWjc8eldXSD1JeiayuCa4t9D2lbCDUxYnRe2PKIVY63I54IcvvHYDv35XyCmnibpWLAFNibe8GT81pP5KsBSqOYciaVOT/0");
        File file = new File("blob:http://localhost:8080/7a8f3629-2f8c-488a-aa79-2928752f5277");
        FileInputStream fis = new FileInputStream(file);

//        OSSClientUtil.putObject(key, StreamUtil.readBytesFromInputStream(fis), "jpg");
        OSSClientUtil.putObject("363801", StreamUtil.readBytesFromInputStream(fis), "mp4");
        fis.close();
//·
//        System.out.println(OSSClientUtil.doesObjectExist(key));

//        System.out.println(OSSClientUtil.getObjectUrl(key));

//        OSSClientUtil.deleteObject(key);
    }
}
