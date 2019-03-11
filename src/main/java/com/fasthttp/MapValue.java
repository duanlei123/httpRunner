package com.fasthttp;

import com.fasthttp.common.ParaType;

/**
 * @Author: duanlei
 * @Date: 2019/3/8 6:11 PM
 * @Version 1.0
 */
public class MapValue {
    public ParaType sMapValueType;
    public String sMapValueContent;

    public MapValue(ParaType sMapValueType, String sMapValueContent) {
        this.sMapValueType = sMapValueType;
        this.sMapValueContent = sMapValueContent;
    }
}
