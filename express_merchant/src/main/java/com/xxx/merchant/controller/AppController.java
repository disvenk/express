package com.xxx.merchant.controller;


import com.xxx.utils.RandomUtils;
import com.xxx.utils.WordUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;


@Controller
@RequestMapping("/app")
public class AppController {

    @RequestMapping(value = "/downloadApp")
    public void downloadApp(HttpServletRequest request, HttpServletResponse response) throws  Exception {
        File file = null;
        InputStream fin = null;
        ServletOutputStream out = null;
        try {
            String appUrl = AppController.class.getClassLoader().getResource("../../").getPath() + "assets/expbiz.apk";
            file = new File(appUrl);
            fin = new FileInputStream(file);

            response.setCharacterEncoding("utf-8");
            response.setContentType("application/octet-stream");
            // 设置浏览器以下载的方式处理该文件名
            response.setHeader("Content-Disposition", "attachment;filename="
                    .concat(String.valueOf(URLEncoder.encode("express.apk", "UTF-8"))));

            out = response.getOutputStream();
            byte[] buffer = new byte[512];  // 缓冲区
            int bytesToRead = -1;
            while((bytesToRead = fin.read(buffer)) != -1) {
                out.write(buffer, 0, bytesToRead);
            }
        } finally {
            if(fin != null) fin.close();
            if(out != null) out.close();
        }
    }


}
