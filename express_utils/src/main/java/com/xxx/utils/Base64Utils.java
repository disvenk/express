package com.xxx.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.*;

/**
 * 注意：
 * 根据RFC822规定，BASE64Encoder编码每76个字符，还需要加上一个回车换行
 * 部分Base64编码的Java库还按照这个标准实行，如 BASE64Encoder，就因为这些换行弄得出了问题，解决办法是替换所有换行和回车。
 * 最好的办法是换用Apache的 commons-codec.jar， Base64.encodeBase64String(byte[]）得到的编码字符串是不带换行符的。
 *
 * Created by wangh on 2016/7/13.
 */
public class Base64Utils {
    private static sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
    private static sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();

    /**
     * base64加密字符串.
     *
     * @param oldStr
     * @return
     */
    public static String encode(String oldStr) {
        return Base64.encodeBase64String(oldStr.getBytes());
    }

    /**
     * base64解密字符串.
     * @param base64String
     * @return
     */
    public static String decode(String base64String) {
        byte[] bytes = Base64.decodeBase64(base64String);
        String result = null;
        try {
            result = new String(bytes, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * base64编码输入流.
     *
     * @param inputStream  输入流
     * @param outputStream 输出流
     * @throws IOException
     */
    public static void encode(InputStream inputStream, OutputStream outputStream)
            throws IOException {
        encoder.encode(inputStream, outputStream);
        inputStream.close();
        outputStream.close();
    }


    /**
     * base64解密输入流.
     *
     * @param inputStream
     * @param outputStream
     * @throws IOException
     */
    public static void decode(InputStream inputStream, OutputStream outputStream)
            throws IOException {
        decoder.decodeBuffer(inputStream, outputStream);
        inputStream.close();
        outputStream.close();
    }

    /**
     * base64加密文件.
     *
     * @param inFileName  源文件
     * @param outFileName 新的文件
     * @throws IOException
     */
    public static void encode(String inFileName, String outFileName) throws IOException {
        File oldFile = new File(inFileName);
        File newFile = new File(outFileName);
        InputStream input = new BufferedInputStream(new FileInputStream(oldFile));
        OutputStream out = new BufferedOutputStream(new FileOutputStream(newFile));
        Base64Utils.encode(input, out);
    }

    /**
     * base64解密文件.
     *
     * @param inFileName  源文件
     * @param outFileName 新的文件
     * @throws IOException
     */
    public static void decode(String inFileName, String outFileName) throws IOException {
        File oldFile = new File(inFileName);
        File newFile = new File(outFileName);
        InputStream input = new BufferedInputStream(
                new FileInputStream(oldFile));
        OutputStream out = new BufferedOutputStream(
                new FileOutputStream(newFile));
        Base64Utils.decode(input, out);
    }

    public static void main(String[] a) throws IOException {
        //Base64.encode("D:\\wang.h\\项目\\游戏营销\\yjl\\src\\main\\webapp\\static\\src\\games\\pingtu\\gameMusic.mp3", "d:\\base64.txt");
        //Base64.decode("d:\\hah.base64", "d:\\ddd.mp3");
        System.out.println("ok");
    }
}
