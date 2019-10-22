package com.xxx.utils;

import java.io.*;

public class CloneUtils {
    /**
     * 深度克隆
     *
     * @param obj 要克隆的对象
     * @return
     * @throws CloneNotSupportedException
     */
    public static Object depthClone(Object obj) throws CloneNotSupportedException {
        try {
            //字节数组输出流，暂存到内存中
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            //序列化
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            //反序列化
            return ois.readObject();
        } catch (Exception e) {
            throw new CloneNotSupportedException(e.getMessage());
        }
    }

}
