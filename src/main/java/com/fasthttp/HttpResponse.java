package com.fasthttp;

import com.fasthttp.common.Response;
import com.jayway.jsonpath.Predicate;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * @Author: duanlei
 * @Date: 2019/3/11 10:34 AM
 * @Version 1.0
 */
public interface HttpResponse {

    /**
     * 执行失败时，可调用本接口获取错误码
     * @return 标准HTTP响应码，如200
     * */
    int getHttpCode();

    /**
     * 调用是否成功
     * @return 是否成功
     * */
    boolean isSuccess();

    /**
     * 获取完整响应内容
     * @return 响应内容字符串
     * */
    String getResponseContent();

    /**
     * 获取返回值中指定名称的结果
     * @param sName  要查找的项名称.
     * <p>持完整名称匹配和部分匹配<br>
     * 完整名称用:号分割,如a:b<br>
     *
     * @return 查找结果内容列表
     * */
   Vector<String> findElement(String sName);

    /**
     * 获取返回值中指定名称的结果
     * @param sName  要查找的项名称.
     *<p>支持完整名称匹配和部分匹配<br>
     * 完整名称用:号分割,如a:b<br>
     * @param isFullName  是否是完整名称.
     *<p>( 如 根节点id 和statuses:id 的差别，有时根节点id需要详细指明为全名称)
     * @return 查找结果内容列表
     * */
    Vector<String> findElement(String sName, boolean isFullName);

    /**
     * 判断指定元素是否存在
     * @param sName - 项名称
     * @return 是否查找成功
     * */
    boolean isElementExist(String sName);

    /**
     * 判断指定元素 -值 是否存在
     * @param sName  - 键名称
     * <p>支持完整名称匹配和部分匹配<br>
     * 完整名称用:号分割,如a:b<br>
     * @param sValue - 键值
     * @return 是否查找成功
     * */
    boolean matchElement(String sName,String sValue);

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
    boolean matchElement(String sName,  String sValue, boolean isFullName);

    /**
     *  使用JsonPath 解析Json, 并返回内容对象.
     *
     *  <p>&nbsp; " $.store.book[*].author"  &nbsp;&nbsp;              List&lt;String&gt;  <br>
     *         &nbsp; " $.store.book[1].author"   &nbsp;&nbsp;             String           <br>
     *         &nbsp; " $.store.book[?(@.category == 'reference') "  &nbsp; &nbsp;     List&lt;Object&gt;     <br>
     *         &nbsp; " $.store.book[?(@.price > 10)"   &nbsp;&nbsp;    List&lt;Object&gt; <br>
     *         &nbsp; "$.store.book[?(@.isbn)"            &nbsp;&nbsp;    List&lt;Object&gt; <br>
     *         &nbsp; " $..price                                        &nbsp;&nbsp;  List&lt;Double&gt; <br>
     * @param jsonPath  JsonPath
     * @param var3 JsonPath Filter  过滤条件  变长参数 可不赋值
     * @param <T> .
     * @return   返回内容对象(可能转换失败).
     *                   <p>对象 HashMap<br>
     *                   列表 List<br>
     *                   基本数据 String; Boolean ; Integer ;  Long; BigInteger; Float, Double<br>
     */
    <T> T  jsonPath(String jsonPath, Predicate... var3);

    /**
     * 使用JsonPath 解析Json, 按照指定类型转换，并返回内容对象.
     * @param jsonPath JsonPath
     * @param type  指定返回类型
     * @param var3  JsonPath Filter  过滤条件  变长参数 可不赋值
     * @param <T>  转换类型(bean).
     * @return  内容对象(可能转换失败)
     */
    <T> T  jsonPath(String jsonPath, Class<T> type, Predicate... var3);
    /**
     * 使用JsonPath 解析Json, 按照指定类型转换，并返回内容对象列表
     * @param jsonPath JsonPath
     * @param type  指定返回类型
     * @param var3  JsonPath Filter  过滤条件  变长参数 可不赋值
     * @param <T>  转换类型(bean)..
     * @return  内容对象列表(可能转换失败)
     */
    <T> List<T> jsonPathList(String jsonPath, Class<T> type, Predicate... var3);


    /**
     *  调试函数
     *  打印 解析JSON(含XML)后，所有元素路径和值
     */
    void printAllElement();

    /**
     * 将反馈的Json字串打印为友好格式
     * @return Json字符串
     */
    String toPrettyJson();

    /**
     * 获取下载文件.
     *    当Response中 Content-Type为  octets/stream  application/octet-stream 等.
     *    Http返回内容为文件数据，Api将数据保存到本地文件中.
     * @return 下载文件
     * */
    File getDownLoadFile();

    /**保存响应内容
     * @param resp - 设置响应内容
     * */
    void setResponse(Response resp);

    /**解析响应内容*/
    void parser();

    /**设置响应格式
     *
     * @param sReqFormat - 格式取值为json或xml或file或other
     * */
    void setFormat(String sReqFormat);


    /**
     * 专用接口：获取request id
     * @return request id
     * */
    abstract public String getRequestId();

    /**
     * 专用接口：获取host id
     * @return host id
     * */
    abstract public String getHostId();

    /**
     * 专用接口：获取api错误代码
     * @return 阿里云定义的错误码
     * */
    abstract public String getErrCode();

    /**
     * 专用接口：获取api错误信息
     * @return 阿里云定义的错误信息
     * */
    abstract public String getErrMsg();

    /**
     * 专用接口：获取http头部信息
     * @param HeaderName  头部名.
     *                    <p>为空表示获取全部头部信息
     * @return Http头部列表
     */
    abstract public List<String> getHttpHeader(String HeaderName);

    /**
     * 专用接口：获取http头部列表
     * @return Http头部列表
     */
    abstract public Map<String, List<String>> getHttpHeaderList();

    /**
     *  调试用函数.
     *  打印回复 header 部分
     */
    abstract public void printHeader();

}
