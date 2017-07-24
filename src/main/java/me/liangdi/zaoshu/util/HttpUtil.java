package me.liangdi.zaoshu.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import lombok.extern.slf4j.Slf4j;
import me.liangdi.zaoshu.ApiException;
import me.liangdi.zaoshu.Constant;
import me.liangdi.zaoshu.api.KeyPair;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static me.liangdi.zaoshu.Constant.AUTH_HEADER;
import static me.liangdi.zaoshu.Constant.AUTH_PRE;

/**
 * 使用 http 协议调用 API 接口的工具包 暂时用 httplicent 实现 后期再优化 Created by liangdi on
 * 6/27/17.
 */
@Slf4j
public class HttpUtil {

    /**
     * 无参数 GET 请求
     *
     * @param keyPair
     * @param url
     * @return
     */
    public static String get(KeyPair keyPair, String url) throws ApiException {
        return get(keyPair, url, null);
    }

    /**
     * 处理 GET 请求
     *
     * @param url
     * @param query
     * @return
     */
    public static String get(KeyPair keyPair, String url, Map<String, String> query) throws ApiException {
        URI uri = buildUri(url, query);
        if (uri == null) {
            return "";
        }
        url = uri.toString();
        log.info("get url:{}", url);

        Request request = Request.Get(url);

        if (query != null && !query.isEmpty() && query.containsKey("contentType") && !query.get("contentType").equals("json") && !query.get("contentType").equals("xml")) {
            return requestFile(keyPair, request, Constant.METHOD_GET, query, "");
        } else {
            return request(keyPair, request, Constant.METHOD_GET, query, "");
        }
    }

    /**
     * Patch 请求
     *
     * @param keyPair
     * @param query
     * @return
     */
    public static String patch(KeyPair keyPair, String url, Map<String, String> query, String body) throws ApiException {
        URI uri = buildUri(url, query);
        if (uri == null) {
            return "";
        }
        url = uri.toString();
        log.info("patch url:{}", url);

        Request request = Request.Patch(url);
        request.body(new StringEntity(body, "UTF-8"));

        return request(keyPair, request, Constant.METHOD_PATCH, query, body);
    }

    public static String post(KeyPair keyPair, String url, Map<String, String> query, String body) throws ApiException {
        URI uri = buildUri(url, query);
        if (uri == null) {
            return "";
        }
        url = uri.toString();
        log.info("post url:{}", url);

        Request request = Request.Post(url);
        return request(keyPair, request, Constant.METHOD_POST, query, body);
    }

    public static Map<String, String> headers(KeyPair keyPair, String url) {
        Map<String, String> headerMap = new HashMap<>();
        URI uri = buildUri(url, null);
        if (uri == null) {
            return headerMap;
        }
        url = uri.toString();
        log.info("get url:{}", url);

        Request request = Request.Get(url);

        configRequest(request, Constant.METHOD_GET, keyPair, null, "");

        try {
            HttpResponse httpResponse = request.execute()
                    .returnResponse();
            Header[] allHeaders = httpResponse.getAllHeaders();

            for (Header header
                    : allHeaders) {
                headerMap.put(header.getName(), header.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return headerMap;
    }

    /**
     * 发送请求处理
     *
     * @param keyPair
     * @param request
     * @param query
     * @param body
     * @return
     */
    private static String request(KeyPair keyPair, Request request, String method, Map<String, String> query, String body) throws ApiException {
        String result = "";
        try {
            configRequest(request, method, keyPair, query, body);
            HttpResponse resp = request
                    .execute()
                    .returnResponse();
            int code = resp.getStatusLine().getStatusCode();

            if (code == Constant.EXCEPTION_CODE_401) {
                System.out.println(EntityUtils.toString(resp.getEntity()));
                throw new ApiException(Constant.EXCEPTION_MESSAGE_401, Constant.EXCEPTION_CODE_401);
            }

            result = EntityUtils.toString(resp.getEntity(), "UTF-8");
        } catch (IOException e) {
            log.error("http exception:{}", e.getMessage());
            e.printStackTrace();

            result = "{}";
        }
        return result;
    }

    private static String requestFile(KeyPair keyPair, Request request, String method, Map<String, String> query, String body) throws ApiException {
        configRequest(request, method, keyPair, query, body);
        HttpResponse resp;
        InputStream is;
        try {
            resp = request
                    .execute()
                    .returnResponse();
            is = resp.getEntity().getContent();
        } catch (IOException ex) {
            throw new ApiException("请求io出错", 0);
        }
        if ("application/octet-stream".equals(resp.getEntity().getContentType().getValue())) {

        }
        Header[] headers = resp.getHeaders("Content-Disposition");
        String fileName = null;
        for (int i = 0; i < headers.length; i++) {
            Header header = headers[i];
            System.out.println(header.getName());
            System.out.println(header.getValue());
            if (header.getValue().contains("filename")) {
                String[] strs = header.getValue().split(";");
                if (strs.length == 2) {
                    String[] strs2 = strs[1].split("'");
                    fileName = strs2[strs2.length - 1];
                }
            }
        }
        if (fileName == null) {
            throw new ApiException("解析文件名出错", 0);
        }
        File file = new File(fileName);
        System.out.println(file.getAbsolutePath());
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException ex) {
            throw new ApiException("文件保存路径出错", 0);
        }
        int inByte;
        try {
            while ((inByte = is.read()) != -1) {
                fos.write(inByte);
            }
        } catch (IOException ex) {
            throw new ApiException(ex.getMessage(), 0);
        }
        return file.getAbsolutePath();
    }

    /**
     * 设置请求头和body,包含生成sign步骤
     *
     * @param request
     * @param keyPair
     * @param query
     * @param body
     */
    private static void configRequest(Request request, String method, KeyPair keyPair, Map<String, String> query, String body) {
        Date now = new Date();
        String gmtDate = Authorize.getDate(now);

        String sign = Authorize.signJsonRequest(keyPair.getSecret(), method, gmtDate, query, body);
        Header authorHeader = authorHeader(keyPair.getApiKey(), sign);
        log.debug("authorHeader:{}", authorHeader);
        request
                .addHeader("Content-Type", Constant.CONTENT_TYPE)
                .addHeader("Date", gmtDate)
                .addHeader(authorHeader);
        if (body != null && !body.trim().equals("")) {
            request.bodyString(body, ContentType.parse(Constant.CONTENT_TYPE));
        }
    }

    /**
     * 构造 URI
     *
     * @param url
     * @param query
     * @return
     */
    private static URI buildUri(String url, Map<String, String> query) {
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(url);
            if (query != null && !query.isEmpty()) {
                for (Map.Entry<String, String> entry : query.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    uriBuilder.addParameter(key, value);
                }
            }

            return uriBuilder.build();
        } catch (URISyntaxException e) {
            log.error("Build URI Error:{}", e.getMessage());
            return null;

        }
    }

    /**
     * 构建 API 认证相关 header
     *
     * @param apiKey
     * @param sign
     * @return
     */
    private static Header authorHeader(String apiKey, String sign) {
        return new BasicHeader(AUTH_HEADER, AUTH_PRE + apiKey + ":" + sign);
    }

}
