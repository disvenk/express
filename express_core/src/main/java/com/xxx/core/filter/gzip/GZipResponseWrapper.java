package com.xxx.core.filter.gzip;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class GZipResponseWrapper extends HttpServletResponseWrapper {
    private HttpServletResponse response;
    private GZipOutputStream gzipOutputStream;
    private PrintWriter writer;

    public GZipResponseWrapper(HttpServletResponse response) throws IOException {
        super(response);
        this.response = response;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (gzipOutputStream == null)
            gzipOutputStream = new GZipOutputStream(response);
        return gzipOutputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer == null)
            writer = new PrintWriter(new OutputStreamWriter(new GZipOutputStream(response), "UTF-8"));
        return writer;
    }

    //压缩后数据长度会发生变化，因此将该方法内容置空
    @Override
    public void setContentLength(int contentLength) {
    }

    @Override
    public void flushBuffer() throws IOException {
        gzipOutputStream.flush();
    }

    public void finishResponse() throws IOException {
        if (gzipOutputStream != null)
            gzipOutputStream.close(); //调用这个方法菜最终输出被压缩的数据到浏览器
        if (writer != null)
            writer.close();
    }
}
