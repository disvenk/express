package com.xxx.core.filter.gzip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;

public class GZipOutputStream extends ServletOutputStream {
    private HttpServletResponse response;                   //原Response
    private GZIPOutputStream gzipOutputStream;             //JDK 自带的 GZIP 压缩类
    private ByteArrayOutputStream byteArrayOutputStream;  //将压缩后的数据存放到 ByteArrayOutputStream 对象中

    public GZipOutputStream(HttpServletResponse response) throws IOException {
        this.response = response;
        this.byteArrayOutputStream = new ByteArrayOutputStream();
        this.gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
    }

    //输出到 JDK 的 GZIP 输出类中
    @Override
    public void write(int b) throws IOException {
        gzipOutputStream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        gzipOutputStream.write(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
        gzipOutputStream.write(b);
    }

    //执行压缩，并将数据输出到浏览器
    @Override
    public void close() throws IOException {
        gzipOutputStream.finish();  //执行压缩，一定要调用此方法。
        byte[] content = byteArrayOutputStream.toByteArray();

        //设定压缩方式为 GZIP，客户端浏览器会自动将数据解压。
        response.addHeader("Content-Encoding", "gzip");
        response.addHeader("Content-Length", Integer.toString(content.length));
        ServletOutputStream out = response.getOutputStream();
        out.write(content);
        out.close();
    }

    @Override
    public void flush() throws IOException {
        gzipOutputStream.flush();
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
    }
}