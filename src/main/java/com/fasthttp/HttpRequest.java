package com.fasthttp;

import com.fasthttp.common.ParaType;

import java.util.Map;

/**
 * @Author: duanlei
 * @Date: 2019/3/8 5:21 PM
 * @Version 1.0
 * http 请求接口定义
 */
public interface HttpRequest {

    /**
     * 通用接口：添加请求扩展参数
     * @param sKey  参数名称
     * @param sValue 参数内容
     * @param nReqType 参数类型，取值范围如下：\r\n
     *    ParaType.INTEGER  输入内容为整数(值不带双引号)
     *    ParaType.STRING   输入内容为字符串，以KEY:VALUE形式出现
     *    ParaType.FILE     输入内容为文件的绝对路径
     * @param urlOrBody 请求格式，取值如下：
     *    Constants.URL_PARA  参数为URL格式
     *    Constants.BODY_PARA 参数为BODY格式
     * @return 是否添加成功
     */
   boolean addCustomPara(String sKey, String sValue, ParaType nReqType, String urlOrBody);


    /**
     * 通用接口：添加自定义的请求数据 本函数只支持POST/PUT方法
     * 本方法只需调用一次，多次调用会自动覆盖；
     * @param sCustomContentType,取值如下：
     *   application/json
     *   application/xml
     * @param sCustomRequestData,自定义的数据
     */
    void addCustomRequestData(String sCustomContentType, String sCustomRequestData);

    /**
     * 通用接口：添加HTTP扩展头部
     * @param sName  参数名称
     * @param sValue 参数内容
     */
    void addHttpHeader(String sName, String sValue);

    /**
     * 通用接口：设置HTTP请求方法(支持：GET/POST/PUT/DELETE)
     * @param sMethod - 方法名称
     */
    void setHttpMethod(String sMethod);

    /**
     * 获取HTTP头部映射
     */
    Map<String, String> getHeaderMap();

    /**
     * 通用接口：设置返回值格式(支持json和xml)
     * @param sFormat - 格式名称
     * @return 是否设置成功
     */
    boolean setRespFormat(String sFormat);

    /**
     * 通用接口：获取HTTP请求方法
     * @return 返回请求方法
     * */
    String getHttpMethod();

    /**
     * 操作HTTP请求url
     */
    String getsUrl();
    void setsUrl(String sUrl) ;

    /**
     * 获取返回值格式
     */
    String getRespFormat();

    //获取API请求参数映射
    Map<String, MapValue> getApiUrlParaMap();
    Map<String, MapValue> getApiBodyParaMap();
    Map<String, MapValue> getApiCustomSigParaMap();

    //检查请求参数是否合法
    boolean check();

    //获取用户自定义request数据
    String getCustomRequestData();

    //获取用户自定义Content-type
    String getCustomContentType();

    //通用接口：打印请求内容,待调用Execute后再打印，可获得真实的请求内容
    void printRequest();

    //返回FILE路径
    String getMultipartFilePath();

    /**
     * 判断是否需要签名
     * @return 是否需要签名
     */
    boolean isNeedSignature();

    /**
     *  返回签名算法
     * @return 签名算法
     */
    ParaType getSigAlgorithm();


    String getSigHttpMethod() ;

  /**
   * 专用接口：获取access key secret
   * @return key secret
   */
   String getAccessKeySecret();
}
