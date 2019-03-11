package com.fasthttp.common;

import java.util.Date;
import java.util.UUID;

/**
 * @Author: duanlei
 * @Date: 2019/3/8 5:23 PM
 * @Version 1.0
 *
 * 本类用于申明api测试框架支持的所有请求类型
 */
public enum ParaType {

    UNKNOWN {public String getName(){return "unknown";}},

    //整形类型
    INTEGER {public String getName(){return "integer";}},

    //整形类型
    BOOLEAN {public String getName(){return "boolean";}},

    // 字符串类型
    STRING {public String getName(){return "string";}},

    // 时间戳
    TIMESTAMP_ISO8601 {public String getName(){return "timeStamp_iso8601";}},

    // 16位随机数
    RANDOM_16 {public String getName(){return "random16";}},

    // HMAC-SHA1算法
    SIG_HMAC_SHA1 {public String getName(){return "HMAC-SHA1";}},

    // HmacSHA256
    SIG_HMAC_SHA256 {public String getName(){return "HMAC-SHA256";}},

    // 文件类型(multipart)
    FILE {public String getName(){return "file";}},

    ;

    public abstract String getName();

    /**
     *获取GMT时区时间
     * @return 返回时间
     */
    static public String buildTimeStamp() {
        long timestamp = System.currentTimeMillis();
        return Common.formatIso8601Date(new Date(timestamp));
    }

    static public String buildRandom16() {
        return UUID.randomUUID().toString();
    }

    static public String buildRandom32() {
        return "";
    }

}
