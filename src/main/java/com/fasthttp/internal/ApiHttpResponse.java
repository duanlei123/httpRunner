package com.fasthttp.internal;

import com.fasthttp.HttpResponse;
import com.fasthttp.common.Constants;
import com.fasthttp.common.ParseService;
import com.fasthttp.common.Response;
import com.jayway.jsonpath.Predicate;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * @Author: duanlei
 * @Date: 2019/3/11 10:35 AM
 * @Version 1.0
 */
public class ApiHttpResponse implements HttpResponse {


    protected Response resp = new Response();
    //保存解析后的响应列表
    ParseService parseService;
    //请求格式：xml json File  or Other
    protected String sReqFormat;

    /**
     * 获取状态码
     * @return 返回状态码 例如：200
     */
    @Override
    public int getHttpCode() {
        return resp.getHttpCode();
    }

    /**
     * 判断调用是否成功
     * @return  true 成功 false 失败
     */
    @Override
    public boolean isSuccess() {
        return (resp.getHttpCode() >= HttpURLConnection.HTTP_OK &&  resp.getHttpCode() <300 );
    }

    /**
     * 获取完整到响应内容
     * @return 返回响应内容
     */
    @Override
    public String getResponseContent() {
        return resp.getContent();
    }


    /**
     * 获取返回值中指定名称的结果
     * @param sName  要查找的项名称.
     * <p>持完整名称匹配和部分匹配<br>
     * 完整名称用:号分割,如a:b<br>
     *
     * @return 查找结果内容列表
     * */
    @Override
    public Vector<String> findElement(String sName) {
        return findElement(sName, false);
    }

    /**
     * 获取返回值中指定名称的结果
     * @param sName  要查找的项名称.
     *<p>支持完整名称匹配和部分匹配<br>
     * 完整名称用:号分割,如a:b<br>
     * @param isFullName  是否是完整名称.
     *<p>( 如 根节点id 和statuses:id 的差别，有时根节点id需要详细指明为全名称)
     * @return 查找结果内容列表
     * */
    @Override
    public Vector<String> findElement(String sName, boolean isFullName) {
        if (this.parseService == null) {
            return new Vector<String>(0);
        }
        return this.parseService.findElement(sName,isFullName);
    }

    /**
     * 判断指定元素是否存在
     * @param sName - 项名称
     * @return 是否查找成功
     * */
    @Override
    public boolean isElementExist(String sName) {
        if (this.parseService == null) {
            return false;
        }
        return this.parseService.findElement(sName,false,true).size() > 0;
    }

    /**
     * 判断指定元素 -值 是否存在
     * @param sName  - 键名称
     * <p>支持完整名称匹配和部分匹配<br>
     * 完整名称用:号分割,如a:b<br>
     * @param sValue - 键值
     * @return 是否查找成功
     * */
    @Override
    public boolean matchElement(String sName, String sValue) {
        return matchElement(sName,sValue,false);
    }

    /**
     * 判断指定元素 -值是否存在
     * @param sName  键名称.
     * <p>支持完整名称匹配和部分匹配<br>
     * 完整名称用:号分割,如a:b<br>
     * @param sValue  键值.
     * @param isFullName  是否是完整名称.
     *<p> 如 根节点id 和statuses:id 的差别 <br>
     *        有时根节点id需要详细指明为全名称
     * @return 是否查找成功
     * */
    @Override
    public boolean matchElement(String sName, String sValue, boolean isFullName) {
        Vector<String> vecValueList = findElement(sName, isFullName);

        for (int i = 0; i < vecValueList.size(); i++) {
            if (vecValueList.get(i).equalsIgnoreCase(sValue)) {
                return true;
            }
        }

        return false;
    }


    @Override
    public <T> T jsonPath(String jsonPath, Predicate... var3) {
        if (this.parseService == null) {
            return null;
        }
        return this.parseService.jsonPath(jsonPath, var3);
    }

    @Override
    public <T> T jsonPath(String jsonPath, Class<T> type, Predicate... var3) {
        if (this.parseService == null) {
            return null;
        }
        return this.parseService.jsonPath(jsonPath, type, var3);
    }

    @Override
    public <T> List<T> jsonPathList(String jsonPath, Class<T> type, Predicate... var3) {
        if (this.parseService == null) {
            return null;
        }
        return this.parseService.jsonPathList(jsonPath, type, var3);
    }

    @Override
    public void printAllElement() {
        if (this.parseService == null) {
            return;
        }
        this.parseService.printAllElement();
    }

    @Override
    public String toPrettyJson() {
        if (this.parseService == null) {
            return "";
        }
        return this.parseService.toPrettyJson();
    }

    @Override
    public File getDownLoadFile() {
        return resp.getDownLoadFile();
    }

    @Override
    public void setResponse(Response resp) {
        this.resp = resp;
    }

    @Override
    public void setFormat(String sReqFormat) {
        this.sReqFormat = sReqFormat;
    }

    @Override
    public String getRequestId() {
        return findElement("RequestId").get(0);
    }

    @Override
    public String getHostId() {
        return "";
    }

    @Override
    public String getErrCode() {
        return "";
    }

    @Override
    public String getErrMsg() {
        return "";
    }

    @Override
    public List<String> getHttpHeader(String HeaderName) {
        List<String> lValue = new ArrayList<String>();

        List<String> lResult = resp.getHeader().get(HeaderName);

        if (lResult == null) {
            return null;
        }
        lValue.addAll(lResult);

        return lValue;
    }

    @Override
    public Map<String, List<String>> getHttpHeaderList() {
        return resp.getHeader();
    }

    /**
     * 打印头部
     */
    @Override
    public void printHeader() {
        Map<String,List<String>> map = resp.getHeader();
        System.out.println("HTTP 相应中的头部参数:");
        for (String key: map.keySet()) {
            List<String> lResult = map.get(key);
            System.out.println("\t"+key+ " : ");
            System.out.print("\t\t");
            for (String value : lResult) {
                System.out.print(value + "\t");
            }
            System.out.println("");
        }
    }

    @Override
    public void parser() {
        // 下载文件类型判断
        if(resp.getDownLoadFile() !=null) {
            // 非期望文件类型
            if (!Constants.FORMAT_FILE.equalsIgnoreCase(sReqFormat) ) {
                System.out.println("Response content type is "+Constants.FORMAT_FILE+ ", not expected " + sReqFormat);
            }
            parseService = null;
            return;
        }
        /**构造一些打桩数据*/
        String sTmpContent = resp.getContent();
        if (sTmpContent == null || sTmpContent.equals("{}") || sTmpContent.equals("")) {
            System.out.println("Response content is empty");
            parseService = null;
            return;
        }

        sTmpContent = sTmpContent.trim();

        // 判断内容为文件类型
        try {
            if (Constants.FORMAT_XML.equalsIgnoreCase(sReqFormat)) {
                this.parseService = parserXml(sTmpContent);
            } else if (Constants.FORMAT_JSON.equalsIgnoreCase(sReqFormat)) {
                this.parseService = parserJson(sTmpContent);
            } else {
                this.parseService = null;
            }
        } catch (Exception e) {
            System.out.println("Response content  expected type is "+sReqFormat+ " but parse failed: " + e.getMessage());
            //e.printStackTrace();
        }
    }

    /**
     * 将XML格式的字符串，转换成vector输出
     */
    private ParseService parserXml(String sXmlData) throws Exception {

        ParseService parser = new ParseService();

        try {
            parser.ParseXml(sXmlData);
        } catch (Exception e) {
            throw e;
        }
        return parser;
    }

    /**
     * 将Json格式的字符串，转换成vector输出
     */
    private ParseService parserJson(String sXmlData)  throws Exception {
        ParseService parser = new ParseService();
        try {
            parser.ParseJson(sXmlData);
        } catch (Exception e) {
            System.out.println("CaseResult failed");
            throw e;
        }
        return parser;
    }
}
