package com.fasthttp.common;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: duanlei
 * @Date: 2019/3/11 10:37 AM
 * @Version 1.0
 */
public class Response {

    //响应的http代码
    private int    nHttpCode = 200;

    //响应的实际内容
    private String sContent  = "dummy";
    private File fDownFile = null;
    private Map<String, List<String>> mHeader = new HashMap<String, List<String>>();

    public int getHttpCode() {
        return nHttpCode;
    }

    public void setHttpCode(int nHttpCode) {
        this.nHttpCode = nHttpCode;
    }

    public File getDownLoadFile() {return fDownFile;}

    public void setDownLoadFile(File fDownFile) {
        this.fDownFile = fDownFile;
    }


    public String getContent() {
        return sContent;
    }

    public void setContent(String sContent) {
        this.sContent = sContent;
    }

    public Map<String, List<String>> getHeader() {
        return mHeader;
    }

    public void setHeader(Map<String, List<String>> mHeader) {
        this.mHeader.putAll(mHeader);
    }
}
