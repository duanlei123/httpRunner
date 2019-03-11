package com.fasthttp;

import com.fasthttp.internal.ApiHttpRequest;
import com.fasthttp.internal.ApiHttpResponse;

/**
 * @Author: duanlei
 * @Date: 2019/3/11 11:22 AM
 * @Version 1.0
 */
public interface HttpRunner {

    /**
     * 用途： 使用request参数，发送HTTP请求
     * @param request  请求参数
     * @param response 响应内容
     *
     * @return 请求是否成功
     *
     * @throws HttpException 异常抛出
     * */
    abstract public boolean execute(HttpRequest request, HttpResponse response) throws HttpException;

    /**
     * 用途：设置连接超时
     *
     * @param connectionTimeout  超时时间 ms
     * */
    public void setConnectTimeout(int connectionTimeout);

    /**
     * 用途： 设置读取数据超时
     *
     * @param readTimeout  超时时间 ms
     * */
    public void setReadTimeout(int readTimeout);

}
