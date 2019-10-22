package com.xxx.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghua on 17/2/10.
 */
public class FileReaderUtils {
    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件（
     * 说明：此方法支持 jar 包中文件的读取，如：readFileByLines("/permitAllRequest")
     */
    public static List<String> readFileByLines(String fileName) {
        InputStream is = FileReaderUtils.class.getResourceAsStream(fileName);
        return readFileByLines(is);
    }

    public static List<String> readFileByLines(InputStream is) {
        List<String> lines = new ArrayList<>();

        if (is == null) return lines;

        BufferedReader reader = null;
        try {
            //System.out.println("以行为单位读取文件内容，一次读一整行：");
            //reader = new BufferedReader(new FileReader(file));
            reader = new BufferedReader(new InputStreamReader(is));

            String tempString = null;
            int line = 1;
            while ((tempString = reader.readLine()) != null) {
                tempString = tempString.trim();
                lines.add(tempString);
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
                try {
                    is.close();
                } catch (IOException e1) {
                }
            }
        }
        return lines;
    }
}
