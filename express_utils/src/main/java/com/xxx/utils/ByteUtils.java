package com.xxx.utils;

import java.io.*;

/**
 * Created by wanghua on 17/2/10.
 */
public class ByteUtils {

    /**
     * 对象转字节流
     */
    public static byte[] toByteArray(Object obj) throws IOException {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        try {
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
        } finally {
            try {
                oos.close();
            } finally {
                bos.close();
            }
        }

        return bytes;
    }

    /**
     * 字节流转对象
     */
    public static Object toObject(byte[] bytes) throws Exception {
        Object obj = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        try {
            obj = ois.readObject();
        } finally {
            try {
                ois.close();
            } finally {
                bis.close();
            }
        }
        return obj;
    }
}
