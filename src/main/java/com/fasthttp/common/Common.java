package com.fasthttp.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

/**
 * @Author: duanlei
 * @Date: 2019/3/8 5:28 PM
 * @Version 1.0
 */
public class Common {
    /**
     * 将时间戳格式化后经过设置时区后返回
     * @param date 时间戳
     * @return   返回格式化后时间
     */
    static public String formatIso8601Date(Date date) { // 1552037569570
        SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_ISO8601);
        // 注意使用GMT时间
        df.setTimeZone(new SimpleTimeZone(0, "GMT")); //设置时区GMT
        return df.format(date);//2019-03-08T09:32:49Z
    }






    public static void main(String[] args) {
        long timestamp = System.currentTimeMillis(); //获取时间戳
        System.out.println(timestamp);
        String iso8601Date = formatIso8601Date(new Date(timestamp));
        System.out.println(iso8601Date);
    }
}
