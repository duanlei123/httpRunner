package com.fasthttp;

import com.fasthttp.common.Constants;
import com.fasthttp.common.ParaType;
import com.fasthttp.internal.ApiHttpRequest;
import com.fasthttp.internal.ApiHttpResponse;
import com.fasthttp.internal.ApiHttpRunner;
import com.fasthttp.utils.Base64;
import org.testng.annotations.Test;

/**
 * @Author: duanlei
 * @Date: 2019/3/11 2:49 PM
 * @Version 1.0
 * 测试
 */
public class HttpTest {


    @Test
    public void getAllProject() throws HttpException {
        String sUrl = "http://localhost:9000/api/projects/search";
        ApiHttpRequest req = new ApiHttpRequest();

        req.setHttpMethod(Constants.HTTP_REQ_METHOD_GET);
        req.addCustomPara("ps", 500, ParaType.INTEGER, Constants.URL_PARA);
        req.setRespFormat(Constants.FORMAT_JSON);
        String userName = "admin";
        String userPass = "admin";
        // Basic 鉴权
        req.addHttpHeader("Authorization", "Basic " + Base64.encodeToString((userName + ":" + userPass).getBytes(), false));

        HttpRunner api = new ApiHttpRunner(sUrl);

        HttpResponse resp = new ApiHttpResponse();
        api.execute(req, resp);

        String responseContent = resp.getResponseContent();

        System.out.println(responseContent);

    }

}
