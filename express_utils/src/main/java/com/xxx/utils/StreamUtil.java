package com.xxx.utils;

import java.io.*;

/**
 * Created by wangh on 2016/7/23.
 */
public class StreamUtil {
    public static StringBuffer readFromInputStream(InputStream is) {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String buffer;
            while ((buffer = br.readLine()) != null) {
                sb.append(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (Exception e) {
            }
            try {
                is.close();
            } catch (Exception e) {
            }
        }
        return sb;
    }

    public static byte[] readBytesFromInputStream(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[8092];
        while (inputStream.available() > 0) {
            int size = inputStream.read(bytes);
            byteArrayOutputStream.write(bytes, 0, size);
        }
        bytes = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        return bytes;
    }
}
