package com.xxx.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by wanghua on 17/2/17.
 */
public class UrlUtils {
    /**
     * URLEncode
     * 注意：base64编码时，会出现+的，不要将其中的+也给变成空格了。
     *
     * @param s
     * @param enc
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String UrlEncode(String s, String enc) {
        try {
            return URLEncoder.encode(s, enc);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * UrlDecode
     * @param s
     * @param enc
     * @return
     */
    public static String UrlDecode(String s, String enc) {
        try {
            return URLDecoder.decode(s, enc);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
