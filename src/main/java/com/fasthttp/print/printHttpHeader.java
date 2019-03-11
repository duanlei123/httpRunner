package com.fasthttp.print;

import java.util.Vector;

/**
 * @Author: duanlei
 * @Date: 2019/3/8 7:03 PM
 * @Version 1.0
 */
public class printHttpHeader {
    //记录待打印的HTTP头部信息
    static private Vector<String> vecPrintHttpHeader = new Vector<String>();

    public static void clear() {
        vecPrintHttpHeader.clear();
    }

    public static void add(String sKey,String sName) {
        String sTmp = sKey +"="+sName;
        vecPrintHttpHeader.add(sTmp);
    }

    public static String getAll() {
        String sContent = new String("");
        for (int i=vecPrintHttpHeader.size()-1;i>=0;i--) {
            sContent += vecPrintHttpHeader.get(i);
            sContent += "\r\n";
        }

        return sContent;
    }
}
