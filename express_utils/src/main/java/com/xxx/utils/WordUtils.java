package com.xxx.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @Description: word文档模板导出
 * @Author: Chen.zm
 * @Date: 2017/12/8 0008
 */
public class WordUtils {
    private static Configuration configuration = null;
    //这里注意的是利用WordUtils的类加载器动态获得模板文件的位置
    private static final String templateFolder = WordUtils.class.getClassLoader().getResource("../../").getPath() + "assets/templete/"; //
    static {
        configuration = new Configuration();
        configuration.setDefaultEncoding("utf-8");
        try {
            configuration.setDirectoryForTemplateLoading(new File(templateFolder));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private WordUtils() {
        throw new AssertionError();
    }

    /**
     * 导出单个word文件  默认模板
     * @param request
     * @param response
     * @param fileName
     * @param map
     * @throws IOException
     */
    public static void exportMillCertificateWord(HttpServletRequest request, HttpServletResponse response, String fileName, Map map) throws IOException {
        exportMillCertificateWord(request, response, "supOrder.ftl", fileName, map);
    }

    /**
     * 导出单个word文档
     * @param request
     * @param response
     * @param templateName
     * @param fileName
     * @param map
     * @throws IOException
     */
    public static void exportMillCertificateWord(HttpServletRequest request, HttpServletResponse response, String templateName, String fileName, Map map) throws IOException {
        Template freemarkerTemplate = configuration.getTemplate(templateName);
        File file = null;
        InputStream fin = null;
        ServletOutputStream out = null;
        try {
            // 调用工具类的createDoc方法生成Word文档
            String targetPath =  templateFolder + fileName + RandomUtils.randomFixedLength(6) + ".doc";
            file = createDoc(targetPath, map,freemarkerTemplate);
            fin = new FileInputStream(file);

            response.setCharacterEncoding("utf-8");
            response.setContentType("application/msword");
            // 设置浏览器以下载的方式处理该文件名
            response.setHeader("Content-Disposition", "attachment;filename="
                    .concat(String.valueOf(URLEncoder.encode(fileName + ".doc", "UTF-8"))));

            out = response.getOutputStream();
            byte[] buffer = new byte[512];  // 缓冲区
            int bytesToRead = -1;
            // 通过循环将读入的Word文件的内容输出到浏览器中
            while((bytesToRead = fin.read(buffer)) != -1) {
                out.write(buffer, 0, bytesToRead);
            }
        } finally {
            if(fin != null) fin.close();
            if(out != null) out.close();
            if(file != null) file.delete(); // 删除临时文件
        }
    }

    /**
     * 生产word文件
     * @param targetPath 生成路径
     * @param dataMap
     * @param template
     * @return
     */
    private static File createDoc(String targetPath, Map<?, ?> dataMap, Template template) {
        File f = new File(targetPath);
        Template t = template;
        try {
            // 这个地方不能使用FileWriter因为需要指定编码类型否则生成的Word文档会因为有无法识别的编码而无法打开
            Writer w = new OutputStreamWriter(new FileOutputStream(f), "utf-8");
            t.process(dataMap, w);
            w.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        return f;
    }


    /**
     * 批量导出word — 默认模板
     * @param request
     * @param response
     * @param fileName
     * @param mapList
     * @throws IOException
     */
    public static void exportMillCertificateWordList(HttpServletRequest request, HttpServletResponse response, String fileName, List<Map> mapList) throws Exception {
        exportMillCertificateWordList(request, response, "supOrder.ftl", fileName, mapList);
    }

    /**
     * 批量导出word文档——压缩格式
     * @param request
     * @param response
     * @param templateName
     * @param fileName
     * @param mapList
     * @throws Exception
     */
    public static void exportMillCertificateWordList(HttpServletRequest request, HttpServletResponse response, String templateName, String fileName, List<Map> mapList) throws Exception {
        Template freemarkerTemplate = configuration.getTemplate(templateName);
        File zipFile = null;
        InputStream fin = null;
        ServletOutputStream out = null;
        try {
            String targetName = fileName + RandomUtils.randomFixedLength(6) + ".zip";   //目的压缩文件名
            FileOutputStream outputStream = new FileOutputStream(templateFolder+"\\"+targetName);
            ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(outputStream));
            for (Map map : mapList) {
                File file = null;
                try {
                    // 调用工具类的createDoc方法生成Word文档
                    String docName = (map.get("wordName") == null ? new Date().getTime() : map.get("wordName")) + "";
                    String targetPath = templateFolder + docName + RandomUtils.randomFixedLength(6) + ".doc";
                    file = createDoc(targetPath, map, freemarkerTemplate);
                    CompressedFileUtil.createCompressedFile(zipOut, file, docName + ".doc");
                } finally {
                    if(file != null) file.delete(); // 删除临时文件
                }
            }
            zipOut.close();

            zipFile = new File(templateFolder+"\\"+targetName);
            fin = new FileInputStream(zipFile);
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/msword");
            // 设置浏览器以下载的方式处理该文件名
            response.setHeader("Content-Disposition", "attachment;filename="
                    .concat(String.valueOf(URLEncoder.encode(fileName + ".zip", "UTF-8"))));

            out = response.getOutputStream();
            byte[] buffer = new byte[512];  // 缓冲区
            int bytesToRead = -1;
            // 通过循环将读入的Word文件的内容输出到浏览器中
            while((bytesToRead = fin.read(buffer)) != -1) {
                out.write(buffer, 0, bytesToRead);
            }
        } finally {
            if(fin != null) fin.close();
            if(out != null) out.close();
            if(zipFile != null) zipFile.delete(); // 删除临时文件
        }
    }
}
