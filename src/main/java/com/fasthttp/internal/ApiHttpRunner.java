package com.fasthttp.internal;

import com.fasthttp.*;
import com.fasthttp.common.Constants;
import com.fasthttp.common.Response;
import com.fasthttp.common.Signature;
import com.fasthttp.utils.WebUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author: duanlei
 * @Date: 2019/3/11 11:25 AM
 * @Version 1.0
 */
public class ApiHttpRunner implements HttpRunner {

    //保存待请求的url
    private String sReqUrl = "";
    private int connectTimeout = 3000;//3秒
    private int readTimeout = 80000;//80秒

    public ApiHttpRunner(String _sUrl) {
        sReqUrl = _sUrl;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    /**
     * 执行http请求
     */
    @Override
    public boolean execute(HttpRequest request, HttpResponse response) throws HttpException {
        if (!request.check()) {
            Response resp = new Response();
            resp.setHttpCode(Constants.HTTP_RESP_800);
            resp.setContent("请求参数有误，请求报文终止发送");
            response.setResponse(resp);
            return false;
        }
        //设置请求格式
        response.setFormat(request.getRespFormat());

        return _execute(request, response);
    }

    private boolean _execute(HttpRequest request, HttpResponse response) throws HttpException {

        // 添加签名字符串
        Map<String, MapValue> protocalUrlParams = request.getApiUrlParaMap();
        Map<String, MapValue> protocalBodyParams = request.getApiBodyParaMap();
        String sCustomContentType = request.getCustomContentType();
        String sCustomRequestData = request.getCustomRequestData();

        try {
            // 计算签名，并将签名结果加入请求参数中
            // 自定义http数据方式暂不支持签名 需要自己处理Body中的数据
            if (request.isNeedSignature() && request.getSigAlgorithm() !=null) {
                Map<String, MapValue> protocalCustomSigParams = request.getApiCustomSigParaMap();

                protocalUrlParams.put(Constants.SIGNATURE,
                        Signature.computeSignature(protocalUrlParams, protocalCustomSigParams, request.getAccessKeySecret(), request.getSigHttpMethod(), request.getSigAlgorithm()));

            }
        } catch (Exception e) {
            throw new HttpException(e);
        }

        String serverUrl = sReqUrl;
        String url = null;
        Response resp = null;

        try {

            url = serverUrl;

            String query = WebUtils.buildQuery(protocalUrlParams, Constants.CHARSET_UTF8);
            request.setsUrl(WebUtils.buildGetUrl(url, query).toString());

            //GET请求
            if (request.getHttpMethod().equalsIgnoreCase(Constants.HTTP_REQ_METHOD_GET)) {
                resp = WebUtils.doGet(url, protocalUrlParams, request.getHeaderMap(), Constants.CHARSET_UTF8, connectTimeout, readTimeout);
            } else if (request.getHttpMethod().equalsIgnoreCase(Constants.HTTP_REQ_METHOD_POST)) {
                //POST请求
                //POST支持json和自定义数据两种方式
                if (sCustomContentType.isEmpty() && sCustomRequestData.isEmpty()) {

                    String sMutilipartFilePath = request.getMultipartFilePath();

                    if (sMutilipartFilePath.isEmpty()) {
                        resp = WebUtils.doPost(url, protocalUrlParams, protocalBodyParams, Constants.CHARSET_UTF8, connectTimeout, readTimeout, request.getHeaderMap());
                    } else {
                        //处理MultiPart文件
                        Map<String, FileItem> urlParams = new LinkedHashMap<String, FileItem>();

                        FileItem item = new FileItem(sMutilipartFilePath);
                        urlParams.put("file", item);

                        resp = WebUtils.doPost(url, protocalUrlParams, protocalBodyParams, urlParams,  Constants.CHARSET_UTF8, connectTimeout, readTimeout,request.getHeaderMap());
                    }

                } else {
                    resp = WebUtils.doPostCustom(url, protocalUrlParams, sCustomContentType, sCustomRequestData, Constants.CHARSET_UTF8, connectTimeout, readTimeout, request.getHeaderMap());
                }
            } else if (request.getHttpMethod().equalsIgnoreCase(Constants.HTTP_REQ_METHOD_PUT)) {
                //PUT请求
                //POST支持json和自定义数据两种方式
                if (sCustomContentType.isEmpty() && sCustomRequestData.isEmpty()) {
                    resp = WebUtils.doPut(url, protocalUrlParams, protocalBodyParams, Constants.CHARSET_UTF8, connectTimeout, readTimeout, request.getHeaderMap());
                } else {
                    resp = WebUtils.doPutCustom(url, protocalUrlParams, sCustomContentType, sCustomRequestData, Constants.CHARSET_UTF8, connectTimeout, readTimeout, request.getHeaderMap());

                }
            } else if (request.getHttpMethod().equalsIgnoreCase(Constants.HTTP_REQ_METHOD_DELETE)) {
                //DELETE请求
                resp = WebUtils.doDelete(url, protocalUrlParams, request.getHeaderMap(), Constants.CHARSET_UTF8, connectTimeout, readTimeout);
            }


        } catch (IOException e) {
            System.out.println("发送请求时发生异常");
            throw new HttpException(e);
        }
        //保存响应内容
        response.setResponse(resp);
        try {
            // if (response.isSuccess()) {
            //解析响应内容
            response.parser();
            // }
        } catch (Exception e) {
            System.out.println("解析响应时发生异常");
            throw new HttpException(e);
        }
        return true;
    }

    /**
     * 构造请求URL字符串
     */
    private String paramsToQueryString(Map<String, String> params) throws UnsupportedEncodingException {
        if (params == null || params.size() == 0) {
            return null;
        }

        StringBuilder paramString = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> p : params.entrySet()) {
            String key = p.getKey();
            String val = p.getValue();

            if (!first) {
                paramString.append("&");
            }

            paramString.append(URLEncoder.encode(key, Constants.CHARSET_UTF8));

            if (val != null) {
                paramString.append("=").append(
                        URLEncoder.encode(val, Constants.CHARSET_UTF8));
            }

            first = false;
        }
        return paramString.toString();
    }
}
