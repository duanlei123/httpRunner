package com.fasthttp.internal;

import com.fasthttp.HttpRequest;
import com.fasthttp.MapValue;
import com.fasthttp.common.Constants;
import com.fasthttp.common.ParaType;
import com.fasthttp.print.printHttpHeader;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author: duanlei
 * @Date: 2019/3/8 6:05 PM
 * @Version 1.0
 */
public class ApiHttpRequest implements HttpRequest {

    //是否添加签名
    protected boolean bIsNeedSignature = false;
    //签名算法
    protected ParaType tSigAlgorithm;
    // 签名 Secret
    protected String sAccessKeySecret = "";
    //File类型
    protected String sMultiFilePath = "";


    //保存公共请求(默认为:xml)
    protected String sFormat = Constants.FORMAT_XML;

    // 保存API头部(URL部分)参数
    protected Map<String, MapValue> hMapApiUrlParaList = new LinkedHashMap<String, MapValue>();
    // 保存除URL签名参数
    protected Map<String, MapValue> hMapCustomSigParaList = new LinkedHashMap<String, MapValue>();
    //BODY部分参数
    protected Map<String, MapValue> hMapApiBodyParaList = new LinkedHashMap<String, MapValue>();

    //添加自定义HttpRequest数据
    protected String sCustomRequestData = "";
    protected String sCustomContentType = "";

    //保存http头部
    protected Map<String, String> hMapHttpHeaderList = new LinkedHashMap<String, String>();

    //保存请求方法和url
    protected String sHttpReqMethod= Constants.HTTP_REQ_METHOD_GET;
    protected String sSigHttpMethod ="";
    protected String sUrl = "";



    /**
     * 通用接口：添加请求扩展参数
     * @param sName 参数名称
     * @param sValue 参数内容
     * @param nReqType 参数类型，取值范围如下：\r\n
     *    ParaType.INTEGER  输入内容为整数(值不带双引号)
     *    ParaType.STRING   输入内容为字符串，以KEY:VALUE形式出现
     *    ParaType.FILE     输入内容为文件的绝对路径
     * @param urlOrBody 请求格式，取值如下：
     *    Constants.URL_PARA  参数为URL格式
     *    Constants.BODY_PARA 参数为BODY格式
     * @return
     */
    public boolean addCustomPara(String sName, String sValue, ParaType nReqType, String urlOrBody) {
        //处理字符串和整形格式
        if ((nReqType.equals(ParaType.STRING)) ||
                (nReqType.equals(ParaType.INTEGER)) ||
                (nReqType.equals(ParaType.BOOLEAN))) {
            //记录设置的返回值类型
            if (sName.equalsIgnoreCase(Constants.FORMAT)) {
                sFormat = sValue;
            }
            return addUrlOrBodyPara(sName, new MapValue(nReqType, sValue), urlOrBody);
        } else {
            return _putCustomPara(sName, sValue, nReqType, urlOrBody);
        }
    }

    public boolean addCustomPara(String sName, int nValue, ParaType nReqType, String urlOrBody) {
        return this.addUrlOrBodyPara(sName, new MapValue(ParaType.INTEGER, nValue + ""), urlOrBody);
    }

    /**
     * url 参数 body 参数 用于签名但不在URL中的参数  添加到对应map中
     * @param name   请求参数名称(key)
     * @param value  请求参数(value)
     * @param urlOrBody 请求类型
     * @return 返回是否添加成功
     */
    public boolean addUrlOrBodyPara(String name, MapValue value, String urlOrBody) {
        if (urlOrBody.equalsIgnoreCase(Constants.URL_PARA)) {
            hMapApiUrlParaList.put(name, value);
            return true;
        } else if (urlOrBody.equalsIgnoreCase(Constants.BODY_PARA)) {
            hMapApiBodyParaList.put(name, value);
            return true;
        } else if (urlOrBody.equalsIgnoreCase(Constants.CUSTOM_SIG_PARA)) {
            hMapCustomSigParaList.put(name, value);
            return true;
        } else {
            System.out.println("请正确填写此参数是URL参数还是BODY参数！");
            return false;
        }
    }

    /**
     * 本接口支持多种类型的自动填充，具体如下：
     */
    protected boolean _putCustomPara(String sName, String sValue, ParaType nReqType, String urlOrBody) {

        if (sName.isEmpty()) {
            return false;
        }
        if (nReqType.equals(ParaType.SIG_HMAC_SHA1)) {
            bIsNeedSignature = true;
            tSigAlgorithm = ParaType.SIG_HMAC_SHA1;
            MapValue mapValue = new MapValue(nReqType, ParaType.SIG_HMAC_SHA1.getName());
            addUrlOrBodyPara(sName, mapValue, urlOrBody);
        }
        if (nReqType.equals(ParaType.SIG_HMAC_SHA256)) {
            bIsNeedSignature = true;
            tSigAlgorithm = ParaType.SIG_HMAC_SHA256;
            MapValue mapValue = new MapValue(nReqType, ParaType.SIG_HMAC_SHA256.getName());
            addUrlOrBodyPara(sName, mapValue, urlOrBody);
        }
        // 时间戳类型
        if (nReqType.equals(ParaType.TIMESTAMP_ISO8601)) {
            MapValue mapValue = new MapValue(nReqType, ParaType.buildTimeStamp());
            addUrlOrBodyPara(sName, mapValue, urlOrBody);
        }
        //16位随机数类型
        if (nReqType.equals(ParaType.RANDOM_16)) {
            MapValue mapValue = new MapValue(nReqType, ParaType.buildRandom16());
            addUrlOrBodyPara(sName, mapValue, urlOrBody);
        }
        if ( sValue.isEmpty()) {
            return false;
        }
        //FILE类型
        if (nReqType.equals(ParaType.FILE)) {
            sMultiFilePath = sValue;
        }
        return true;
    }

    /**
     * 添加自定义的请求数据
     * @param sCustomContentType  例如application/json
     * @param sCustomRequestData  自定义的http数据
     */
    public void addCustomRequestData(String sCustomContentType, String sCustomRequestData) {
        this.sCustomRequestData = sCustomRequestData;
        this.sCustomContentType = sCustomContentType;
    }

    /**
     * 添加http头部
     */
    public void addHttpHeader(String sName, String sValue) {
        hMapHttpHeaderList.put(sName, sValue);
    }

    //设置请求方法(Get/Post)
    public void setHttpMethod(String sMethod) {
        sHttpReqMethod = sMethod;
    }

    //获取HTTP头部映射
    public Map<String, String> getHeaderMap() {
        return hMapHttpHeaderList;
    }

    // 获取请求方法(Get/Post)
    public String getHttpMethod() {
        return sHttpReqMethod;
    }
    //操作HTTP请求url
    public String getsUrl() { return sUrl; }
    public void setsUrl(String sUrl) {
        this.sUrl = sUrl;
    }

    //获取返回值格式
    public String getRespFormat() { return sFormat; }

    //获取API请求参数映射
    public Map<String, MapValue> getApiUrlParaMap() { return hMapApiUrlParaList; }
    public Map<String, MapValue> getApiCustomSigParaMap () {
        return hMapCustomSigParaList;
    }
    public Map<String, MapValue> getApiBodyParaMap() {
        return hMapApiBodyParaList;
    }

    //进行参数合法性检查
    public boolean check() { return true; }

    //获取用户自定义request数据
    public String getCustomRequestData() { return sCustomRequestData; }
    //获取用户自定义Content-type
    public String getCustomContentType() { return sCustomContentType; }
    //判断是否需要签名
    public boolean isNeedSignature() { return bIsNeedSignature; }
    //返回签名算法
    public ParaType getSigAlgorithm() {return tSigAlgorithm; }
    public String getSigHttpMethod() {
        if (sSigHttpMethod == null || sSigHttpMethod.length()<= 0 ) {
            return  sHttpReqMethod;
        }
        return sSigHttpMethod;
    }
    public String getAccessKeySecret() {
        return sAccessKeySecret;
    }
    //返回FILE路径
    public String getMultipartFilePath() { return sMultiFilePath; }

    /**
     * @param sRespFormat 支持内容为JSON和XML，如果不是这两种内容赋值为other，并调用ApiResponse.getResponseContent()方法
     *                    获取原始字符串
     * @return 是否设置成功
     */
    public boolean setRespFormat(String sRespFormat) {
        if (sRespFormat.equalsIgnoreCase(Constants.FORMAT_JSON) ||
                sRespFormat.equalsIgnoreCase(Constants.FORMAT_XML) ||
                sRespFormat.equalsIgnoreCase(Constants.FORMAT_FILE) ||
                sRespFormat.equalsIgnoreCase(Constants.FORMAT_OTHER)) {
            this.sFormat = sRespFormat;
            return true;
        }
        return false;
    }

    /**
     * 打印请求内容,待调用Execute后再打印，可获得真实的请求内容
     */
    public void printRequest(){
        System.out.println("------------------------HTTP请求内容------------------------");
        if(isNeedSignature())
            System.out.println("使用的签名算法为：" + getSigAlgorithm());
        else
            System.out.println("未使用签名");

        System.out.println("HTTP请求方法为：" + sHttpReqMethod);

        if(sUrl.equals(""))
            System.out.println("HTTP请求URL为：" + "请先执行ApiRunner的execute函数");
        else
            System.out.println("HTTP请求URL为：" + sUrl);

        System.out.println("HTTP请求头部：");
        System.out.println(printHttpHeader.getAll());

        if(sHttpReqMethod.equals(Constants.HTTP_REQ_METHOD_GET) || sHttpReqMethod.equals(Constants.HTTP_REQ_METHOD_DELETE))
            this.printGetAndDelete();
        else if(sHttpReqMethod.equals(Constants.HTTP_REQ_METHOD_POST) || sHttpReqMethod.equals(Constants.HTTP_REQ_METHOD_PUT))
            this.printPostAndPut();
        else
            System.out.println("请正确设置HTTP请求方法！");

        System.out.println("---------------------------结束---------------------------");
    }

    //输出
    protected void printGetAndDelete(){
        Set<Map.Entry<String, MapValue>> entries = hMapApiUrlParaList.entrySet();
        if(hMapApiUrlParaList != null && !hMapApiUrlParaList.isEmpty()){
            System.out.println("HTTP请求中的URL参数：");
            for (Map.Entry<String, MapValue> entry : entries)
                System.out.println("	" + entry.getKey() + " : " + entry.getValue().sMapValueContent);
        }else
            System.out.println("未添加URL参数");

        if(hMapApiBodyParaList != null && !hMapApiBodyParaList.isEmpty())
            System.out.println(sHttpReqMethod + "方法不支持BODY参数");

        if(!sCustomRequestData.isEmpty() || !sCustomContentType.isEmpty())
            System.out.println(sHttpReqMethod + "方法不支持自定义请求数据");
    }

    //输出
    protected void printPostAndPut(){
        Set<Map.Entry<String, MapValue>> entries1 = hMapApiUrlParaList.entrySet();
        Set<Map.Entry<String, MapValue>> entries2 = hMapApiBodyParaList.entrySet();
        //区分使用参数列表还是自定义请求数据
        if(sCustomRequestData.isEmpty() && sCustomContentType.isEmpty()){
            //System.out.println("	Content-Type : " + "application/json");

            if(hMapApiUrlParaList != null && !hMapApiUrlParaList.isEmpty()){
                System.out.println("HTTP请求中的URL参数：");
                for (Map.Entry<String, MapValue> entry : entries1)
                    System.out.println("	" + entry.getKey() + " : " + entry.getValue().sMapValueContent);
            }else
                System.out.println("未添加URL参数");

            if(hMapApiBodyParaList != null && !hMapApiBodyParaList.isEmpty()){
                System.out.println("HTTP请求中的BODY参数：");
                for (Map.Entry<String, MapValue> entry : entries2)
                    System.out.println("	" + entry.getKey() + " : " + entry.getValue().sMapValueContent);
            }else
                System.out.println("未添加BODY参数");
        }else{
            System.out.println("	Content-Type : " + sCustomContentType);

            if(hMapApiUrlParaList != null && !hMapApiUrlParaList.isEmpty()){
                System.out.println("HTTP请求中的URL参数：");
                for (Map.Entry<String, MapValue> entry : entries1)
                    System.out.println("	" + entry.getKey() + " : " + entry.getValue().sMapValueContent);
            }else
                System.out.println("未添加URL参数");

            if(hMapApiBodyParaList != null && !hMapApiBodyParaList.isEmpty())
                System.out.println("使用自定义请求内容，BODY参数无效");

            System.out.println("自定义请求内容为 : ");
            System.out.println(sCustomRequestData);
        }
    }
}
