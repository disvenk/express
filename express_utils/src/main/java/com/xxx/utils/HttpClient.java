package com.xxx.utils;

import com.alibaba.fastjson.JSONObject;
import com.xxx.utils.contract.SimpleResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
//import webapp.mvc.beans.SimpleResult;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

public class HttpClient {
    public enum Method {GET, POST}

    private HttpClientContext CONTEXT;

    private RequestConfig requestConfig;

    public HttpClient() {
        this.CONTEXT = HttpClientContext.create();
        //设置超时
        this.requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(5000)   //单位毫秒。
                .setConnectTimeout(5000)
                .setSocketTimeout(5000)
                .build();
    }

    /**
     * 创建 HttpClient
     *
     * @param keyStoreType 如：PKCS12。若无，则填 null
     * @param certPassword 如：1372888702。keyStoreType 为null时，此参数也为 null
     * @param certPath     keyStoreType 为null时，此参数也为 null
     * @return
     */
    private CloseableHttpClient createCloseableHttpClient(String keyStoreType, String certPassword, String certPath) throws Exception {
        if (!StringUtils.isBlank(keyStoreType)) {
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            FileInputStream instream = new FileInputStream(new File(certPath));
            try {
                keyStore.load(instream, certPassword.toCharArray());
            } finally {
                instream.close();
            }

            // Trust own CA and all self-signed certs
            SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, certPassword.toCharArray()).build();
            // Allow TLSv1 protocol only
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslcontext,
                    new String[]{"TLSv1"},
                    null,
                    SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            CloseableHttpClient httpclient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .build();

            return httpclient;
        } else {
            //相信所有自签名的证书
            SSLContext sslcontext = new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
            //创建httpclient
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext);
            CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            return httpclient;
        }
    }


    public SimpleResult request(Method method, String url, String body, String cookie) throws Exception {
        SimpleResult simpleResult = new SimpleResult();

        CloseableHttpClient httpclient = createCloseableHttpClient(null, null, null);

        HttpRequestBase httpRequestBase = null;
        if (method.equals(Method.GET))
            httpRequestBase = new HttpGet(url);
        else if (method.equals(Method.POST))
            httpRequestBase = new HttpPost(url);
        else {
            throw new Exception("不支持的 method");
        }
        httpRequestBase.setConfig(requestConfig);

        //httpRequestBase.setHeader("X-IsAJAXForm", "1");
        //httpRequestBase.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httpRequestBase.setHeader("X-Requested-With", "XMLHttpRequest");
        httpRequestBase.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.101 Safari/537.36");
        httpRequestBase.setHeader("Accept", "*/*");
        httpRequestBase.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        if (!StringUtils.isBlank(cookie))
            httpRequestBase.setHeader("Cookie", cookie);

        // 设置FormEntity
        if (!StringUtils.isBlank(body)) {
            if (!method.equals(Method.POST))
                throw new Exception("body 参数必须在 POST 请求下使用");
            List<BasicNameValuePair> formparams = new ArrayList();
            String[] kvs = body.split("&");
            for (int i = 0; i < kvs.length; i++) {
                String[] keyValue = kvs[i].split("=");
                formparams.add(new BasicNameValuePair(keyValue[0], keyValue.length == 2 ? keyValue[1] : null));
            }
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(formparams, "utf-8");  //UrlEncodedFormEntity
            ((HttpPost) httpRequestBase).setEntity(urlEncodedFormEntity);
        }

        //执行请求
        CloseableHttpResponse response = httpclient.execute(httpRequestBase, CONTEXT);
        //处理返回结果
        try {
            //获取entity
            HttpEntity entity = response.getEntity();
            simpleResult.setMessage(EntityUtils.toString(entity, "utf-8"));
            EntityUtils.consume(entity);  //销毁 entity
            simpleResult.setSuccess(response.getStatusLine().getStatusCode() == 200);
            return simpleResult;
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                throw new Exception("关闭CloseableHttpResponse时发生异常（connection）：" + e.getMessage());
            }

            try {
                httpclient.close();
            } catch (IOException e) {
                throw new Exception("关闭HttpClient时发生异常（connection）：" + e.getMessage());
            }
        }
    }

    public SimpleResult requestJson(Method method, String url, JSONObject body, String cookie) throws Exception {
        SimpleResult simpleResult = new SimpleResult();

        CloseableHttpClient httpclient = createCloseableHttpClient(null, null, null);

        HttpRequestBase httpRequestBase = null;
        if (method.equals(Method.GET))
            httpRequestBase = new HttpGet(url);
        else if (method.equals(Method.POST))
            httpRequestBase = new HttpPost(url);
        else {
            throw new Exception("不支持的 method");
        }
        httpRequestBase.setConfig(requestConfig);

        //httpRequestBase.setHeader("X-IsAJAXForm", "1");
        //httpRequestBase.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httpRequestBase.setHeader("X-Requested-With", "XMLHttpRequest");
        httpRequestBase.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.101 Safari/537.36");
        httpRequestBase.setHeader("Accept", "*/*");
        httpRequestBase.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        if (!StringUtils.isBlank(cookie))
            httpRequestBase.setHeader("Cookie", cookie);

        // 设置entity
        if (!StringUtils.isBlank(body.toString())) {
            if (!method.equals(Method.POST))
                throw new Exception("body 参数必须在 POST 请求下使用");

            StringEntity entity = new StringEntity(body.toString(), "utf-8");//解决中文乱码问题
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            ((HttpPost) httpRequestBase).setEntity(entity);  //StringEntity
        }

        //执行请求
        CloseableHttpResponse response = httpclient.execute(httpRequestBase, CONTEXT);
        //处理返回结果
        try {
            //获取entity
            HttpEntity entity = response.getEntity();
            simpleResult.setMessage(EntityUtils.toString(entity, "utf-8"));
            EntityUtils.consume(entity);  //销毁 entity
            simpleResult.setSuccess(response.getStatusLine().getStatusCode() == 200);
            return simpleResult;
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                throw new Exception("关闭CloseableHttpResponse时发生异常（connection）：" + e.getMessage());
            }

            try {
                httpclient.close();
            } catch (IOException e) {
                throw new Exception("关闭HttpClient时发生异常（connection）：" + e.getMessage());
            }
        }
    }

    public SimpleResult requestXml(Method method, String url, String xml, String cookie, String keyStoreType, String certPassword, String certPath) throws Exception {
        SimpleResult simpleResult = new SimpleResult();

        CloseableHttpClient httpclient = createCloseableHttpClient(keyStoreType, certPassword, certPath);

        HttpRequestBase httpRequestBase = null;
        if (method.equals(Method.GET))
            httpRequestBase = new HttpGet(url);
        else if (method.equals(Method.POST))
            httpRequestBase = new HttpPost(url);
        else {
            throw new Exception("不支持的 method");
        }
        httpRequestBase.setConfig(requestConfig);

        //httpRequestBase.setHeader("X-IsAJAXForm", "1");
        //httpRequestBase.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httpRequestBase.setHeader("X-Requested-With", "XMLHttpRequest");
        httpRequestBase.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.101 Safari/537.36");
        httpRequestBase.setHeader("Accept", "*/*");
        httpRequestBase.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        if (!StringUtils.isBlank(cookie))
            httpRequestBase.setHeader("Cookie", cookie);

        // 设置entity
        if (!StringUtils.isBlank(xml)) {
            if (!method.equals(Method.POST))
                throw new Exception("body 参数必须在 POST 请求下使用");

            StringEntity entity = new StringEntity(xml, "utf-8");//解决中文乱码问题
            entity.setContentEncoding("UTF-8");
            entity.setContentType("text/xml");
            ((HttpPost) httpRequestBase).setEntity(entity);  //StringEntity
        }

        //执行请求
        CloseableHttpResponse response = httpclient.execute(httpRequestBase, CONTEXT);
        //处理返回结果
        try {
            //获取entity
            HttpEntity entity = response.getEntity();
            simpleResult.setMessage(EntityUtils.toString(entity, "utf-8"));
            EntityUtils.consume(entity);  //销毁 entity
            simpleResult.setSuccess(response.getStatusLine().getStatusCode() == 200);
            return simpleResult;
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                throw new Exception("关闭CloseableHttpResponse时发生异常（connection）：" + e.getMessage());
            }

            try {
                httpclient.close();
            } catch (IOException e) {
                throw new Exception("关闭HttpClient时发生异常（connection）：" + e.getMessage());
            }
        }
    }

}
