package com.fasthttp.utils;

import com.fasthttp.FileItem;
import com.fasthttp.MapValue;
import com.fasthttp.common.Constants;
import com.fasthttp.common.ParaType;
import com.fasthttp.common.Response;
import com.fasthttp.print.printHttpHeader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: duanlei
 * @Date: 2019/3/11 2:21 PM
 * @Version 1.0
 * 网络工具类
 */
public class WebUtils {

    private static final String DEFAULT_CHARSET = Constants.CHARSET_UTF8;
    private static final String METHOD_POST = "POST";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_DELETE = "DELETE";


    private static class DefaultTrustManager implements X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
    }

    private WebUtils() {
    }

    /**
     * 执行HTTP POST请求。
     *
     * @param url    请求地址
     * @param urlParams 请求参数
     * @return 响应字符串
     * @throws IOException
     */
    public static Response doPost(String url, Map<String, MapValue> urlParams, Map<String, MapValue> bodyParams) throws IOException {
        return doPost(url, urlParams, bodyParams, DEFAULT_CHARSET, -1, -1);
    }

    /**
     * 执行HTTP POST请求。
     *
     * @param url    请求地址
     * @param urlParams 请求参数
     * @param bodyParams 请求参数
     * @param connectTimeout 超时
     * @param readTimeout  超时
     * @return 响应字符串
     * @throws IOException
     */
    public static Response doPost(String url, Map<String, MapValue> urlParams, Map<String, MapValue> bodyParams,
                                  int connectTimeout, int readTimeout) throws IOException {
        return doPost(url, urlParams, bodyParams, DEFAULT_CHARSET, connectTimeout, readTimeout);
    }

    /**
     * 执行HTTP POST请求。
     *
     * @param url     请求地址
     * @param urlParams  请求参数
     * @param charset 字符集，如UTF-8, GBK, GB2312
     * @return 响应字符串
     * @throws IOException
     */
    public static Response doPost(String url, Map<String, MapValue> urlParams, Map<String, MapValue> bodyParams,
                                  String charset) throws IOException {
        return doPost(url, urlParams, bodyParams, charset, -1, -1, null);
    }
    /**
     * 执行HTTP POST请求。
     *
     * @param url     请求地址
     * @param urlParams  请求参数
     * @param charset 字符集，如UTF-8, GBK, GB2312
     * @param connectTimeout 超时
     * @param readTimeout  超时
     * @return 响应字符串
     * @throws IOException
     */
    public static Response doPost(String url, Map<String, MapValue> urlParams, Map<String, MapValue> bodyParams,
                                  String charset, int connectTimeout, int readTimeout) throws IOException {
        return doPost(url, urlParams, bodyParams, charset, connectTimeout, readTimeout, null);
    }

    /**
     * 支持post自定义数据发送
     *
     * @param url  请求地址
     * @param sCustomContentType,例如application/json
     * @param sCustomRequestData，自定义字符串内容
     * @param headerMap  http请求头
     * @return 响应字符串
     * @throws IOException
     */
    public static Response doPostCustom(String url, Map<String, MapValue> urlParams, String sCustomContentType, String sCustomRequestData, Map<String, String> headerMap) throws IOException {
        return doPostCustom(url,urlParams, sCustomContentType, sCustomRequestData,DEFAULT_CHARSET, -1, -1, headerMap);
    }

    /**
     * 支持post自定义数据发送
     *
     * @param url  请求地址
     * @param sCustomContentType,例如application/json
     * @param sCustomRequestData，自定义字符串内容
     * @param connectTimeout 超时
     * @param readTimeout  超时
     * @param headerMap  http请求头
     * @return 响应字符串
     * @throws IOException
     */
    public static Response doPostCustom(String url, Map<String, MapValue> urlParams, String sCustomContentType, String sCustomRequestData,
                                        int connectTimeout, int readTimeout , Map<String, String> headerMap) throws IOException {
        return doPostCustom(url,urlParams, sCustomContentType, sCustomRequestData,DEFAULT_CHARSET, connectTimeout, readTimeout, headerMap);
    }
    /**
     * 支持post自定义数据发送
     *
     * @param url  请求地址
     * @param sCustomContentType,例如application/json
     * @param sCustomRequestData，自定义字符串内容
     * @param charset 字符集，如UTF-8, GBK, GB2312
     * @param headerMap  http请求头
     * @return 响应字符串
     * @throws IOException
     */
    public static Response doPostCustom(String url, Map<String, MapValue> urlParams, String sCustomContentType, String sCustomRequestData,
                                        String charset, Map<String, String> headerMap) throws IOException {
        return doPostCustom(url,urlParams, sCustomContentType, sCustomRequestData,charset,-1,-1,headerMap);
    }
    /**
     * 支持post自定义数据发送
     *
     * @param url  请求地址
     * @param sCustomContentType,例如application/json
     * @param sCustomRequestData，自定义字符串内容
     * @param charset 字符集，如UTF-8, GBK, GB2312
     * @param connectTimeout 超时
     * @param readTimeout  超时
     * @param headerMap  http请求头
     * @return 响应字符串
     * @throws IOException
     */
    public static Response doPostCustom(String url, Map<String, MapValue> urlParams, String sCustomContentType, String sCustomRequestData,
                                        String charset, int connectTimeout, int readTimeout, Map<String, String> headerMap) throws IOException {
        //String ctype = "application/x-www-form-urlencoded;charset=" + charset;

        //POST请求直接插入自定义字符串
        byte[] content = {};
        if (sCustomRequestData != null) {
            content = sCustomRequestData.getBytes(charset);
        }

        String urlQuery = buildQuery(urlParams, charset);

        String ctype = sCustomContentType;

        return _doPost(url, urlQuery, ctype, content, connectTimeout, readTimeout, headerMap);
    }
    /**
     * 执行HTTP POST请求。
     *
     * @param url     请求地址
     * @param urlParams  请求参数
     * @param headerMap  http请求头
     * @return 响应字符串
     * @throws IOException
     */
    public static Response doPost(String url, Map<String, MapValue> urlParams, Map<String, MapValue> bodyParams,
                                  Map<String, String> headerMap) throws IOException {
        return doPost(url, urlParams, bodyParams,DEFAULT_CHARSET,-1,-1,headerMap);
    }
    /**
     * 执行HTTP POST请求。
     *
     * @param url     请求地址
     * @param urlParams  请求参数
     * @param headerMap  http请求头
     * @param connectTimeout 超时
     * @param readTimeout  超时
     * @return 响应字符串
     * @throws IOException
     */
    public static Response doPost(String url, Map<String, MapValue> urlParams, Map<String, MapValue> bodyParams,
                                  int connectTimeout, int readTimeout,Map<String, String> headerMap) throws IOException {
        return doPost(url, urlParams, bodyParams,DEFAULT_CHARSET,connectTimeout,readTimeout,headerMap);
    }
    /**
     * 执行HTTP POST请求。
     *
     * @param url     请求地址
     * @param urlParams  请求参数
     * @param charset 字符集，如UTF-8, GBK, GB2312
     * @param headerMap  http请求头
     * @return 响应字符串
     * @throws IOException
     */
    public static Response doPost(String url, Map<String, MapValue> urlParams, Map<String, MapValue> bodyParams,
                                  String charset, Map<String, String> headerMap) throws IOException {
        return doPost(url, urlParams, bodyParams,charset,-1,-1,headerMap);
    }
    /**
     * 执行HTTP POST请求。
     *
     * @param url     请求地址
     * @param urlParams  请求参数
     * @param charset 字符集，如UTF-8, GBK, GB2312
     * @param connectTimeout 超时
     * @param readTimeout  超时
     * @param headerMap  http请求头
     * @return 响应字符串
     * @throws IOException
     */
    public static Response doPost(String url, Map<String, MapValue> urlParams, Map<String, MapValue> bodyParams,
                                  String charset, int connectTimeout, int readTimeout, Map<String, String> headerMap) throws IOException {
        //String ctype = "application/x-www-form-urlencoded;charset=" + charset;

        //POST请求默认改成json格式发送
        String query = buildJson(bodyParams);

        byte[] content = {};
        if (query != null) {
            content = query.getBytes(charset);
        }

        String urlQuery = buildQuery(urlParams, charset);

        String ctype = "application/json";

        return _doPost(url, urlQuery, ctype, content, connectTimeout, readTimeout, headerMap);
    }

    /**
     * 支持put自定义数据发送
     *
     * @param url  请求地址
     * @param sCustomContentType,例如application/json
     * @param sCustomRequestData，自定义字符串内容
     * @param headerMap  http请求头
     * @return 响应字符串
     * @throws IOException
     */
    public static Response doPutCustom(String url, Map<String, MapValue> urlParams, String sCustomContentType, String sCustomRequestData, Map<String, String> headerMap) throws IOException {
        return doPutCustom(url,urlParams, sCustomContentType, sCustomRequestData,DEFAULT_CHARSET, -1, -1, headerMap);
    }

    /**
     * 支持put自定义数据发送
     *
     * @param url  请求地址
     * @param sCustomContentType,例如application/json
     * @param sCustomRequestData，自定义字符串内容
     * @param connectTimeout 超时
     * @param readTimeout  超时
     * @param headerMap  http请求头
     * @return 响应字符串
     * @throws IOException
     */
    public static Response doPutCustom(String url, Map<String, MapValue> urlParams, String sCustomContentType, String sCustomRequestData,
                                       int connectTimeout, int readTimeout , Map<String, String> headerMap) throws IOException {
        return doPutCustom(url,urlParams, sCustomContentType, sCustomRequestData,DEFAULT_CHARSET, connectTimeout, readTimeout, headerMap);
    }
    /**
     * 支持put自定义数据发送
     *
     * @param url  请求地址
     * @param sCustomContentType,例如application/json
     * @param sCustomRequestData，自定义字符串内容
     * @param charset 字符集，如UTF-8, GBK, GB2312
     * @param headerMap  http请求头
     * @return 响应字符串
     * @throws IOException
     */
    public static Response doPutCustom(String url, Map<String, MapValue> urlParams, String sCustomContentType, String sCustomRequestData,
                                       String charset, Map<String, String> headerMap) throws IOException {
        return doPutCustom(url,urlParams, sCustomContentType, sCustomRequestData,charset,-1,-1,headerMap);
    }
    /**
     * 支持put自定义数据发送
     *
     * @param url  请求地址
     * @param sCustomContentType,例如application/json
     * @param sCustomRequestData，自定义字符串内容
     * @param charset 字符集，如UTF-8, GBK, GB2312
     * @param connectTimeout 超时
     * @param readTimeout  超时
     * @param headerMap  http请求头
     * @return 响应字符串
     * @throws IOException
     */
    public static Response doPutCustom(String url, Map<String, MapValue> urlParams, String sCustomContentType, String sCustomRequestData,
                                       String charset, int connectTimeout, int readTimeout, Map<String, String> headerMap) throws IOException {
        //PUT请求支持自定义字符串发送
        byte[] content = {};
        if (sCustomRequestData != null) {
            content = sCustomRequestData.getBytes(charset);
        }

        String urlQuery = buildQuery(urlParams, charset);

        String ctype = sCustomContentType;

        return _doPut(url, urlQuery, ctype, content, connectTimeout, readTimeout, headerMap);
    }
    /**
     * 执行HTTP Put请求。
     *
     * @param url     请求地址
     * @param urlParams  请求参数
     * @param headerMap  http请求头
     * @return 响应字符串
     * @throws IOException
     */
    public static Response doPut(String url, Map<String, MapValue> urlParams, Map<String, MapValue> bodyParams,
                                 Map<String, String> headerMap) throws IOException {
        return doPut(url,urlParams,bodyParams,DEFAULT_CHARSET,-1,-1,headerMap);
    }
    /**
     * 执行HTTP Put请求。
     *
     * @param url     请求地址
     * @param urlParams  请求参数
     * @param connectTimeout 超时
     * @param readTimeout  超时
     * @param headerMap  http请求头
     * @return 响应字符串
     * @throws IOException
     */
    public static Response doPut(String url, Map<String, MapValue> urlParams, Map<String, MapValue> bodyParams,
                                 int connectTimeout, int readTimeout, Map<String, String> headerMap) throws IOException {
        return doPut(url,urlParams,bodyParams,DEFAULT_CHARSET,connectTimeout,readTimeout,headerMap);
    }
    /**
     * 执行HTTP Put请求。
     *
     * @param url     请求地址
     * @param urlParams  请求参数
     * @param charset 字符集，如UTF-8, GBK, GB2312
     * @param headerMap  http请求头
     * @return 响应字符串
     * @throws IOException
     */
    public static Response doPut(String url, Map<String, MapValue> urlParams, Map<String, MapValue> bodyParams, String charset,
                                 Map<String, String> headerMap) throws IOException {
        return doPut(url,urlParams,bodyParams,charset,-1,-1,headerMap);

    }
    /**
     * 执行HTTP Put请求。
     *
     * @param url     请求地址
     * @param urlParams  请求参数
     * @param charset 字符集，如UTF-8, GBK, GB2312
     * @param headerMap  http请求头
     * @return 响应字符串
     * @throws IOException
     */
    public static Response doPut(String url, Map<String, MapValue> urlParams, Map<String, MapValue> bodyParams, String charset,
                                 int connectTimeout, int readTimeout, Map<String, String> headerMap) throws IOException {
        //POST请求默认改成json格式发送
        String query = buildJson(bodyParams);

        byte[] content = {};
        if (query != null) {
            content = query.getBytes(charset);
        }

        String urlQuery = buildQuery(urlParams, charset);

        String ctype = "application/json";

        return _doPut(url, urlQuery, ctype, content, connectTimeout, readTimeout, headerMap);
    }

    /**
     * 执行HTTP POST请求。
     *
     * @param url     请求地址
     * @param ctype   请求类型
     * @param content 请求字节数组
     * @return 响应字符串
     * @throws IOException
     */
    private static Response _doPost(String url, String query, String ctype, byte[] content, int connectTimeout, int readTimeout,
                                    Map<String, String> headerMap) throws IOException {
        HttpURLConnection conn = null;
        OutputStream out = null;
        String respData = null;
        Response resp = null;
        try {
            try {
                conn = getConnection(buildGetUrl(url, query), Constants.HTTP_REQ_METHOD_POST, ctype, headerMap, connectTimeout, readTimeout);
            } catch (IOException e) {
                Map<String, String> map = getParamsFromUrl(url);
                ClintLogger.logCommError(e, url, map.get("app_key"), map.get("method"), content);
                throw e;
            }
            try {
                //建tcp连接
                out = conn.getOutputStream();
                out.write(content);
                //respData = getResponseAsString(conn);
                //resp.setContent(respData);
                resp = new Response();
                getResponse(conn,resp);

                resp.setHttpCode(conn.getResponseCode());
                resp.setHeader(conn.getHeaderFields());
            } catch (IOException e) {
                //Map<String, String> map = getParamsFromUrl(url);
                //TaobaoLogger.logCommError(e, conn, map.get("app_key"), map.get("method"), content);
                throw e;
            }

        } finally {
            if (out != null) {
                out.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

        return resp;
    }

    /**
     * 执行HTTP PUT请求。
     *
     * @param url     请求地址
     * @param ctype   请求类型
     * @param content 请求字节数组
     * @return 响应字符串
     * @throws IOException
     */
    private static Response _doPut(String url, String query, String ctype, byte[] content, int connectTimeout, int readTimeout,
                                   Map<String, String> headerMap) throws IOException {
        HttpURLConnection conn = null;
        OutputStream out = null;
        String respData = null;
        Response resp = null;
        try {
            try {
                conn = getConnection(buildGetUrl(url, query), Constants.HTTP_REQ_METHOD_PUT, ctype, headerMap, connectTimeout, readTimeout);
            } catch (IOException e) {
                Map<String, String> map = getParamsFromUrl(url);
                ClintLogger.logCommError(e, url, map.get("app_key"), map.get("method"), content);
                throw e;
            }
            try {
                //建tcp连接
                out = conn.getOutputStream();
                out.write(content);
                //respData = getResponseAsString(conn);
                resp = new Response();
                //resp.setContent(respData);
                getResponse(conn,resp);

                resp.setHttpCode(conn.getResponseCode());
                resp.setHeader(conn.getHeaderFields());
            } catch (IOException e) {
                //Map<String, String> map = getParamsFromUrl(url);
                //TaobaoLogger.logCommError(e, conn, map.get("app_key"), map.get("method"), content);
                throw e;
            }

        } finally {
            if (out != null) {
                out.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

        return resp;
    }

    /**
     * 执行带文件上传的HTTP POST请求。
     *
     * @param url        请求地址
     * @param urlParams  url参数
     * @param bodyParams body参数
     * @param fileParams 文件请求参数
     * @return 响应字符串
     * @throws IOException
     */
    public static Response doPost(String url, Map<String, MapValue> urlParams, Map<String, MapValue> bodyParams,
                                  Map<String, FileItem> fileParams, int connectTimeout, int readTimeout) throws IOException {
        if (fileParams == null || fileParams.isEmpty()) {
            return doPost(url, urlParams, bodyParams, DEFAULT_CHARSET, connectTimeout, readTimeout);
        } else {
            return doPost(url, urlParams, bodyParams, fileParams, DEFAULT_CHARSET, connectTimeout, readTimeout);
        }
    }

    public static Response doPost(String url, Map<String, MapValue> urlParams, Map<String, MapValue> bodyParams,
                                  Map<String, FileItem> fileParams, String charset, int connectTimeout, int readTimeout) throws IOException {
        return doPost(url, urlParams, bodyParams, fileParams, charset, connectTimeout, readTimeout, null);
    }

    /**
     * 执行带文件上传的HTTP POST请求。
     *
     * @param url        请求地址
     * @param urlParams     url参数
     * @param bodyParams   body参数
     * @param fileParams 文件请求参数
     * @param charset    字符集，如UTF-8, GBK, GB2312
     * @param headerMap  需要传递的header头，可以为空
     * @return 响应字符串
     * @throws IOException
     */
    public static Response doPost(String url, Map<String, MapValue> urlParams, Map<String, MapValue> bodyParams,
                                  Map<String, FileItem> fileParams, String charset, int connectTimeout, int readTimeout, Map<String, String> headerMap)
            throws IOException {
        if (fileParams == null || fileParams.isEmpty()) {
            return doPost(url, urlParams, bodyParams, charset, connectTimeout, readTimeout, headerMap);
        } else {
            String urlQuery = buildQuery(urlParams, charset);
            return _doPostWithFile(url, urlQuery, urlParams, bodyParams, fileParams,
                    charset, connectTimeout, readTimeout, headerMap);
        }

    }

    private static Response _doPostWithFile(String url, String query, Map<String, MapValue> urlParams, Map<String, MapValue> bodyParams,
                                            Map<String, FileItem> fileParams, String charset, int connectTimeout, int readTimeout, Map<String, String> headerMap)
            throws IOException {
        Map<String, MapValue> allParams = new LinkedHashMap<String, MapValue>();
        allParams.putAll(urlParams);
        allParams.putAll(bodyParams);

        String boundary = System.currentTimeMillis() + ""; // 随机分隔线
        HttpURLConnection conn = null;
        OutputStream out = null;
        String rsp = null;

        Response resp = null;
        try {
            try {
                String ctype = "multipart/form-data;charset=" + charset + ";boundary=" + boundary;
                conn = getConnection(buildGetUrl(url, query), METHOD_POST, ctype, headerMap, connectTimeout, readTimeout);
            } catch (IOException e) {
                Map<String, String> map = getParamsFromUrl(url);

                ClintLogger.logCommError(e, url, map.get("app_key"), map.get("method"), allParams);
                throw e;
            }

            try {
                out = conn.getOutputStream();

                byte[] entryBoundaryBytes = ("\r\n--" + boundary + "\r\n").getBytes(charset);

                // 组装文本请求参数
                Set<Map.Entry<String, MapValue>> textEntrySet = bodyParams.entrySet();
                for (Map.Entry<String, MapValue> textEntry : textEntrySet) {
                    byte[] textBytes = getTextEntry(textEntry.getKey(), textEntry.getValue().sMapValueContent, charset);
                    out.write(entryBoundaryBytes);
                    out.write(textBytes);
                }

                // 组装文件请求参数
                Set<Map.Entry<String, FileItem>> fileEntrySet = fileParams.entrySet();
                for (Map.Entry<String, FileItem> fileEntry : fileEntrySet) {
                    FileItem fileItem = fileEntry.getValue();
                    if (fileItem.getContent() == null) {
                        continue;
                    }
                    byte[] fileBytes = getFileEntry(fileEntry.getKey(), fileItem.getFileName(), fileItem.getMimeType(), charset);
                    out.write(entryBoundaryBytes);
                    out.write(fileBytes);
                    out.write(fileItem.getContent());
                }

                // 添加请求结束标志
                byte[] endBoundaryBytes = ("\r\n--" + boundary + "--\r\n").getBytes(charset);
                out.write(endBoundaryBytes);
                // rsp = getResponseAsString(conn);
            } catch (IOException e) {
                Map<String, String> map = getParamsFromUrl(url);
                ClintLogger.logCommError(e, conn, map.get("app_key"), map.get("method"), allParams);
                throw e;
            }
        } finally {

            resp = new Response();
            getResponse(conn,resp);
            //resp.setContent(rsp);
            resp.setHttpCode(conn.getResponseCode());
            resp.setHeader(conn.getHeaderFields());

            if (out != null) {
                out.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

        return resp;
    }

    private static byte[] getTextEntry(String fieldName, String fieldValue, String charset) throws IOException {
        StringBuilder entry = new StringBuilder();
        entry.append("Content-Disposition:form-data;name=\"");
        entry.append(fieldName);
        entry.append("\"\r\nContent-Type:text/plain\r\n\r\n");
        entry.append(fieldValue);
        return entry.toString().getBytes(charset);
    }

    private static byte[] getFileEntry(String fieldName, String fileName, String mimeType, String charset)
            throws IOException {
        StringBuilder entry = new StringBuilder();
        entry.append("Content-Disposition:form-data;name=\"");
        entry.append(fieldName);
        entry.append("\";filename=\"");
        entry.append(fileName);
        entry.append("\"\r\nContent-Type:");
        entry.append(mimeType);
        entry.append("\r\n\r\n");
        return entry.toString().getBytes(charset);
    }
    /**
     * 执行HTTP GET请求。
     *
     * @param url         请求地址
     * @param paramsQuery 请求参数
     * @return 响应字符串
     * @throws IOException
     */
    public static Response doGet(String url, Map<String, MapValue> paramsQuery, Map<String, String> paramsHeader) throws IOException {
        return doGet(url, paramsQuery, paramsHeader, DEFAULT_CHARSET,  -1, -1);
    }
    /**
     * 执行HTTP GET请求。
     *
     * @param url         请求地址
     * @param paramsQuery 请求参数
     * @param connectTimeout 超时
     * @param readTimeout     超时
     * @return 响应字符串
     * @throws IOException
     */
    public static Response doGet(String url, Map<String, MapValue> paramsQuery, Map<String, String> paramsHeader, int connectTimeout, int readTimeout) throws IOException {
        return doGet(url, paramsQuery, paramsHeader, DEFAULT_CHARSET, connectTimeout, readTimeout);
    }
    /**
     * 执行HTTP GET请求。
     *
     * @param url         请求地址
     * @param paramsQuery 请求参数
     * @param charset     字符集，如UTF-8, GBK, GB2312
     * @return 响应字符串
     * @throws IOException
     */
    public static Response doGet(String url, Map<String, MapValue> paramsQuery, Map<String, String> paramsHeader, String charset) throws IOException {
        return doGet(url, paramsQuery, paramsHeader,charset, -1, -1);
    }
    /**
     * 执行HTTP GET请求。
     *
     * @param url         请求地址
     * @param paramsQuery 请求参数
     * @param charset     字符集，如UTF-8, GBK, GB2312
     * @param connectTimeout 超时
     * @param readTimeout     超时
     * @return 响应字符串
     * @throws IOException
     */
    public static Response doGet(String url, Map<String, MapValue> paramsQuery, Map<String, String> paramsHeader, String charset, int connectTimeout, int readTimeout) throws IOException {
        HttpURLConnection conn = null;
        Response resp = new Response();

        try {
            String ctype = "application/x-www-form-urlencoded;charset=" + charset;
            String query = buildQuery(paramsQuery, charset);
            try {
                conn = getConnection(buildGetUrl(url, query), METHOD_GET, ctype, paramsHeader, connectTimeout, readTimeout);
            } catch (IOException e) {
                Map<String, String> map = getParamsFromUrl(url);
                ClintLogger.logCommError(e, url, map.get("app_key"), map.get("method"), paramsQuery);
                throw e;
            }

            try {
                //resp.setContent(getResponseAsString(conn));
                getResponse(conn,resp);
                resp.setHttpCode(conn.getResponseCode());
                resp.setHeader(conn.getHeaderFields());
            } catch (IOException e) {
                Map<String, String> map = getParamsFromUrl(url);
                ClintLogger.logCommError(e, conn, map.get("app_key"), map.get("method"), paramsQuery);
                throw e;
            }

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return resp;
    }
    /**
     * 执行HTTP DELETE请求。
     *
     * @param url         请求地址
     * @param paramsQuery 请求参数
     * @return 响应字符串
     * @throws IOException
     */
    public static Response doDelete(String url, Map<String, MapValue> paramsQuery, Map<String, String> paramsHeader) throws IOException {
        return doDelete(url, paramsQuery, paramsHeader, DEFAULT_CHARSET, -1, -1);
    }
    /**
     * 执行HTTP DELETE请求。
     *
     * @param url         请求地址
     * @param paramsQuery 请求参数
     * @param connectTimeout 超时
     * @param readTimeout     超时
     * @return 响应字符串
     * @throws IOException
     */
    public static Response doDelete(String url, Map<String, MapValue> paramsQuery, Map<String, String> paramsHeader, int connectTimeout, int readTimeout) throws IOException {
        return doDelete(url, paramsQuery, paramsHeader, DEFAULT_CHARSET, connectTimeout, readTimeout);
    }

    /**
     * 执行HTTP DELETE请求。
     *
     * @param url         请求地址
     * @param paramsQuery 请求参数
     * @param charset     字符集，如UTF-8, GBK, GB2312
     * @return 响应字符串
     * @throws IOException
     */
    public static Response doDelete(String url, Map<String, MapValue> paramsQuery, Map<String, String> paramsHeader, String charset) throws IOException {
        return  doDelete(url, paramsQuery, paramsHeader, charset, -1, -1);
    }

    /**
     * 执行HTTP DELETE请求。
     *
     * @param url         请求地址
     * @param paramsQuery 请求参数
     * @param charset     字符集，如UTF-8, GBK, GB2312
     * @param connectTimeout 超时
     * @param readTimeout     超时
     * @return 响应字符串
     * @throws IOException
     */
    public static Response doDelete(String url, Map<String, MapValue> paramsQuery, Map<String, String> paramsHeader, String charset, int connectTimeout, int readTimeout) throws IOException {
        HttpURLConnection conn = null;
        Response resp = new Response();

        try {
            String ctype = "application/x-www-form-urlencoded;charset=" + charset;
            String query = buildQuery(paramsQuery, charset);
            try {
                conn = getConnection(buildGetUrl(url, query), METHOD_DELETE, ctype, paramsHeader, connectTimeout, readTimeout);
            } catch (IOException e) {
                Map<String, String> map = getParamsFromUrl(url);
                ClintLogger.logCommError(e, url, map.get("app_key"), map.get("method"), paramsQuery);
                throw e;
            }

            try {
                //resp.setContent(getResponseAsString(conn));
                getResponse(conn,resp);
                resp.setHttpCode(conn.getResponseCode());
            } catch (IOException e) {
                Map<String, String> map = getParamsFromUrl(url);
                ClintLogger.logCommError(e, conn, map.get("app_key"), map.get("method"), paramsQuery);
                throw e;
            }

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return resp;
    }

    private static HttpURLConnection getConnection(URL url, String method, String ctype, Map<String, String> headerMap, int connectTimeout, int readTimeout) throws IOException {
        HttpURLConnection conn = null;
        if ("https".equals(url.getProtocol())) {
            SSLContext ctx = null;
            try {
                ctx = SSLContext.getInstance("TLS");
                ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
            } catch (Exception e) {
                throw new IOException(e);
            }
            HttpsURLConnection connHttps = (HttpsURLConnection) url.openConnection();
            connHttps.setSSLSocketFactory(ctx.getSocketFactory());
            connHttps.setHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;// 默认都认证通过
                }
            });
            conn = connHttps;
        } else {
            conn = (HttpURLConnection) url.openConnection();
        }

        conn.setRequestMethod(method);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        //关闭http自动重定向
        conn.setInstanceFollowRedirects(false);
        //conn.setRequestProperty("Accept", "text/html,text/javascript,text/html");

        if (connectTimeout >= 0) {
            conn.setConnectTimeout(connectTimeout);
        }
        if (readTimeout >=0) {
            conn.setReadTimeout(readTimeout);
        }

        printHttpHeader.clear();

        //添加用户指定的HTTP头部
        if (headerMap != null) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                _addHttpHeader(conn, entry.getKey(), entry.getValue());
            }
        }

        //添加固定头部
        _addHttpHeader(conn, "Accept", Constants.HTTP_HEADER_ACCEPT);
        _addHttpHeader(conn, "User-Agent", Constants.HTTP_HEADER_USER_AGENT);
        _addHttpHeader(conn, "Content-Type", ctype);
        return conn;
    }

    /**
     * 实际添加HTTP头部，key相同则直接忽略
     * */
    private static void _addHttpHeader(HttpURLConnection conn,String sKey,String sValue) {
        String sTmpValue = conn.getRequestProperty(sKey);
        if (sTmpValue == null || sTmpValue.isEmpty()) {
            conn.setRequestProperty(sKey, sValue);
            printHttpHeader.add(sKey,sValue);
        }
    }

    public static URL buildGetUrl(String strUrl, String query) throws IOException {
        URL url = new URL(strUrl);

        //请求参数可能为空
        if (query == null || StringUtils.isEmpty(query)) {
            return url;
        }

        if (StringUtils.isEmpty(url.getQuery())) {
            if (strUrl.endsWith("?")) {
                strUrl = strUrl + query;
            } else {
                strUrl = strUrl + "?" + query;
            }
        } else {
            if (strUrl.endsWith("&")) {
                strUrl = strUrl + query;
            } else {
                strUrl = strUrl + "&" + query;
            }
        }

        return new URL(strUrl);
    }

    public static String buildQuery(Map<String, MapValue> params, String charset) throws IOException {
        if (params == null || params.isEmpty()) {
            return null;
        }

        StringBuilder query = new StringBuilder();
        Set<Map.Entry<String, MapValue>> entries = params.entrySet();
        boolean hasParam = false;

        for (Map.Entry<String, MapValue> entry : entries) {
            String name = entry.getKey();
            String value = entry.getValue().sMapValueContent;
            //if (StringUtils.areNotEmpty(name, value)) {
            if (hasParam) {
                query.append("&");
            } else {
                hasParam = true;
            }

            query.append(name).append("=").append(URLEncoder.encode(value, charset));
            //}
        }

        return query.toString();
    }

    public static String buildJson(Map<String, MapValue> params) throws IOException {

        if (params == null || params.isEmpty()) {
            return null;
        }

        StringBuilder query = new StringBuilder();
        Set<Map.Entry<String, MapValue>> entries = params.entrySet();

        query.append("{");

        for (Map.Entry<String, MapValue> entry : entries) {
            String name = entry.getKey();
            String value = entry.getValue().sMapValueContent;
            // 忽略参数名或参数值为空的参数
            // 不应当忽略
            //if (StringUtils.areNotEmpty(name, value)) {
            if (StringUtils.areNotEmpty(name)) {
                //name
                query.append("\"").append(name).append("\"").append(":");
                if (value == null) {
                    query.append("null").append(",");
                } else {
                    try{//识别是否为JSON对象
                        new JSONObject(value);
                        query.append(value).append(",");
                    }catch(JSONException je1){
                        try{//识别是否为JSON数组
                            new JSONArray(value);
                            query.append(value).append(",");
                        }catch(JSONException je2){
                            //支持整型值，两端不加双引号 2015-8-12
                            if (entry.getValue().sMapValueType.equals(ParaType.INTEGER)) {
                                query.append(value).append(",");
                            } else if (entry.getValue().sMapValueType.equals(ParaType.BOOLEAN)) {
                                if (value.equalsIgnoreCase("true")) {
                                    query.append("true").append(",");
                                } else if (value.equalsIgnoreCase("false") ) {
                                    query.append("false").append(",");
                                } else {
                                    query.append(JSONObject.quote(value)).append(",");
                                }
                            } else {
                                //value 字符串类型  自动处理处理字符串quote
                                //query.append("\"").append(value).append("\"").append(",");
                                query.append(JSONObject.quote(value)).append(",");
                            }
                        }
                    }
                }
            }
        }

        //去掉最后一位逗号
        query.deleteCharAt(query.length() - 1);

        query.append("}");

        return query.toString();
    }

    /** 遇到下载文件用 */
    private static final String[] CONTENT_TYPES_FILE = {"octet-stream","octets/stream" ,"java-archive"};

    protected static void getResponse(HttpURLConnection conn, Response resp )  throws IOException {

        String ctype = conn.getContentType();

        boolean bFileType = false;
        if (!StringUtils.isEmpty(ctype)) {
            // 检查是否返回下载文件类型
            for (String fileType : CONTENT_TYPES_FILE) {
                if (ctype.indexOf(fileType) >=0) {
                    bFileType = true;
                    break;
                }
            }
        }

        if (bFileType) {
            // 文件类型
            File file = getResponseAsFile(conn);
            resp.setDownLoadFile(file);
        } else {
            // 文本类型
            String rspData = getResponseAsString(conn);
            resp.setContent(rspData);
        }

    }

    protected static File getResponseAsFile(HttpURLConnection conn)  throws IOException {

        String fileName = getDownLoadFileName(conn);
        File file = new File(fileName);
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(file);
            getStream(conn.getInputStream(), output);
        } catch (IOException e) {
            throw e;
        } finally {
            if (output != null ) {
                try {
                    output.close();
                } catch (IOException e) {
                }
            }
        }
        return file;
    }
    //TODO：这个正则表达式正确么
    private final static Pattern REGEX_FILE_NAME = Pattern.compile("attachment;filename=\"?([\\w\\-\\.\\\\:/+\\-&^$#@!%~`'=\\[\\]\\{\\}\\u4e00-\\u9fa5\\(\\)]+)\"?");

    private static String getDownLoadFileName(HttpURLConnection conn) {

        // private static final Pattern REGEX_FILE_NAME = Pattern.compile("attachment;filename=\"([\\w\\-]+)\"");
        String sContentDis = conn.getHeaderField("Content-Disposition").trim();
        String sFileName = null;
        String sExt ="";
        if (!StringUtils.isEmpty(sContentDis)) {
            Matcher matcher = REGEX_FILE_NAME.matcher(sContentDis);
            if (matcher.find()) {
                sFileName= matcher.group(1);
            } else {
                final String[] charSets= { /*Constants.CHARSET_UTF8,Constants.CHARSET_GBK,*/ Constants.CHARSET_ISO8859_1};
                for (String charSet: charSets) {
                    String matchString;
                    try {
                        matchString = new String( sContentDis.getBytes(charSet));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        continue;
                    }
                    matcher = REGEX_FILE_NAME.matcher(matchString);
                    if (matcher.find()) {
                        sFileName= matcher.group(1);
                        break;
                    }
                }
            }
            if (sFileName !=null) {
                File file = new File(matcher.group(1));
                return file.getName();
            }
            int iPos = sContentDis.lastIndexOf(".");
            if (iPos>= 0) {
                sExt = sContentDis.substring(iPos);
                if ( "\"".equals(sExt.charAt(sExt.length() - 1))) {
                    sExt = sExt.substring(0,sExt.length()-1);
                }
            }
        }

        return  "Tmp" + getTime()+ sExt;
    }
    private static String getTime() {
        StringBuffer sTime = new StringBuffer("");
        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
        sTime.append(c.get(Calendar.YEAR));
        sTime.append(c.get(Calendar.MONTH) + 1); //从0开始计数
        sTime.append(c.get(Calendar.DATE));
        sTime.append(c.get(Calendar.HOUR_OF_DAY));
        sTime.append(c.get(Calendar.MINUTE));
        sTime.append(c.get(Calendar.SECOND));
        return sTime.toString();
    }
    protected static String getResponseAsString(HttpURLConnection conn) throws IOException {

        //发送http报文
        String sTmpContent = conn.getContentType();
        String charset = getResponseCharset(sTmpContent);
        InputStream es = conn.getErrorStream();
        if (es == null) {
            return getStreamAsString(conn.getInputStream(), charset);
        } else {
            String msg = getStreamAsString(es, charset);
            if (StringUtils.isEmpty(msg)) {
                throw new IOException(conn.getResponseCode() + ":" + conn.getResponseMessage());
            } else {
                return msg;
            }
        }
    }
    private static long getStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
    private static String getStreamAsString(InputStream stream, String charset) throws IOException {
        try {
            Reader reader = new InputStreamReader(stream, charset);
            StringBuilder response = new StringBuilder();

            final char[] buff = new char[1024];
            int read = 0;
            while ((read = reader.read(buff)) > 0) {
                response.append(buff, 0, read);
            }

            return response.toString();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    private static String getResponseCharset(String ctype) {
        String charset = DEFAULT_CHARSET;

        if (!StringUtils.isEmpty(ctype)) {
            String[] params = ctype.split(";");
            for (String param : params) {
                param = param.trim();
                if (param.startsWith("charset")) {
                    String[] pair = param.split("=", 2);
                    if (pair.length == 2) {
                        if (!StringUtils.isEmpty(pair[1])) {
                            charset = pair[1].trim();
                        }
                    }
                    break;
                }
            }
        }

        return charset;
    }

    /**
     * 使用默认的UTF-8字符集反编码请求参数值。
     *
     * @param value 参数值
     * @return 反编码后的参数值
     */
    public static String decode(String value) {
        return decode(value, DEFAULT_CHARSET);
    }

    /**
     * 使用默认的UTF-8字符集编码请求参数值。
     *
     * @param value 参数值
     * @return 编码后的参数值
     */
    public static String encode(String value) {
        return encode(value, DEFAULT_CHARSET);
    }

    /**
     * 使用指定的字符集反编码请求参数值。
     *
     * @param value   参数值
     * @param charset 字符集
     * @return 反编码后的参数值
     */
    public static String decode(String value, String charset) {
        String result = null;
        if (!StringUtils.isEmpty(value)) {
            try {
                result = URLDecoder.decode(value, charset);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    /**
     * 使用指定的字符集编码请求参数值。
     *
     * @param value   参数值
     * @param charset 字符集
     * @return 编码后的参数值
     */
    public static String encode(String value, String charset) {
        String result = null;
        if (!StringUtils.isEmpty(value)) {
            try {
                result = URLEncoder.encode(value, charset);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    private static Map<String, String> getParamsFromUrl(String url) {
        Map<String, String> map = null;
        if (url != null && url.indexOf('?') != -1) {
            map = splitUrlQuery(url.substring(url.indexOf('?') + 1));
        }
        if (map == null) {
            map = new HashMap<String, String>();
        }
        return map;
    }

    /**
     * 从URL中提取所有的参数。
     *
     * @param query URL地址
     * @return 参数映射
     */
    public static Map<String, String> splitUrlQuery(String query) {
        Map<String, String> result = new HashMap<String, String>();

        String[] pairs = query.split("&");
        if (pairs != null && pairs.length > 0) {
            for (String pair : pairs) {
                String[] param = pair.split("=", 2);
                if (param != null && param.length == 2) {
                    result.put(param[0], param[1]);
                }
            }
        }

        return result;
    }

    /**
     * 获取文件的真实媒体类型。目前只支持JPG, GIF, PNG, BMP四种图片文件。
     *
     * @param bytes 文件字节流
     * @return 媒体类型(MEME-TYPE)
     */
    public static String getMimeType(byte[] bytes) {
        String suffix = getFileSuffix(bytes);
        String mimeType;

        if ("JPG".equals(suffix)) {
            mimeType = "image/jpeg";
        } else if ("GIF".equals(suffix)) {
            mimeType = "image/gif";
        } else if ("PNG".equals(suffix)) {
            mimeType = "image/png";
        } else if ("BMP".equals(suffix)) {
            mimeType = "image/bmp";
        }else {
            mimeType = "application/octet-stream";
        }

        return mimeType;
    }

    /**
     * 获取文件的真实后缀名。目前只支持JPG, GIF, PNG, BMP四种图片文件。
     *
     * @param bytes 文件字节流
     * @return JPG, GIF, PNG or null
     */
    public static String getFileSuffix(byte[] bytes) {
        if (bytes == null || bytes.length < 10) {
            return null;
        }

        if (bytes[0] == 'G' && bytes[1] == 'I' && bytes[2] == 'F') {
            return "GIF";
        } else if (bytes[1] == 'P' && bytes[2] == 'N' && bytes[3] == 'G') {
            return "PNG";
        } else if (bytes[6] == 'J' && bytes[7] == 'F' && bytes[8] == 'I' && bytes[9] == 'F') {
            return "JPG";
        } else if (bytes[0] == 'B' && bytes[1] == 'M') {
            return "BMP";
        } else {
            return null;
        }
    }

}
