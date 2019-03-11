package com.fasthttp.common;

/**
 * @Author: duanlei
 * @Date: 2019/3/8 5:29 PM
 * @Version 1.0
 */
public class Constants {

    /** TOP默认时间格式 **/
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /** TOP Date默认时区 **/
    public static final String DATE_TIMEZONE = "GMT+8";

    /** SDK版本号 */
    public static final String SDK_VERSION = "top-sdk-java-20150515";

    /** ISO-8859-1字符集 **/
    public static final String CHARSET_ISO8859_1= "ISO-8859-1";

    /**
     * JSON 响应格式
     */
    public static final String FORMAT_JSON = "json";
    /**
     * TOP XML 响应格式
     */
    public static final String FORMAT_XML = "xml";
    /**
     * TOP 文件响应格式
     */
    public static final String FORMAT_FILE="file";

    public static final String FORMAT_OTHER = "other";
    /**
     * 时间戳格式化-格式
     */
    public static final String DATE_FORMAT_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static final String SIGNATURE_METHOD_HMAC_SHA1 = "HMAC-SHA1";

    public static final String SIGNATURE_VERSION_1_0 = "1.0";


    public static final String ACTION = "Action";

    public static final String VERSION = "Version";

    public static final String ACCESS_KEY_ID = "AccessKeyId";

    public static final String TIME_STAMP = "TimeStamp";

    public static final String SIGNATURE_METHOD = "SignatureMethod";

    public static final String SIGNATURE_VERSION = "SignatureVersion";

    public static final String SIGNATURE_NONCE = "SignatureNonce";

    public static final String FORMAT = "Format";

    public static final String SIGNATURE = "Signature";

    /**
     * UTF-8字符集
     **/
    public static final String CHARSET_UTF8 = "UTF-8";

    /**
     * 返回的错误码
     */
    public static final String ERROR_RESPONSE = "error_response";

    public static final int HTTP_RESP_800 = 800;


    //请求方法
    public static final String HTTP_REQ_METHOD_GET = "GET";
    public static final String HTTP_REQ_METHOD_POST = "POST";
    public static final String HTTP_REQ_METHOD_PUT = "PUT";
    public static final String HTTP_REQ_METHOD_DELETE = "DELETE";

    //区分URL参数和BODY参数
    public static final String URL_PARA = "URL";
    public static final String BODY_PARA = "BODY";
    // 用于签名但不在URL中的参数
    public static final String CUSTOM_SIG_PARA= "CUSTOM_SIG";

    //HTTP请求头部
    public static final String HTTP_HEADER_ACCEPT = "text/html,application/xhtml+xml,application/xml,application/json;q=0.9,image/webp,*/*;q=0.8";
    public static final String HTTP_HEADER_USER_AGENT = "ApiRunner testFreamWork by duanlei";
}
