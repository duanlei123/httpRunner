package com.fasthttp.common;

import com.fasthttp.MapValue;
import com.fasthttp.utils.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author: duanlei
 * @Date: 2019/3/11 11:35 AM
 * @Version 1.0
 */
public class Signature {

    private static final String ENCODING = "UTF-8";
    //private static String httpMethod = "POST";

    public static MapValue computeSignature(Map<String, MapValue> parameters, Map<String, MapValue> customSIgParams,
                                            String accessKeySecret, String sHttpMethod, ParaType tSignatureAlgorithm) throws Exception {

        Map<String, MapValue> allParameters = new LinkedHashMap<String, MapValue>();
        for (   String key: parameters.keySet()) {
            allParameters.put(key,parameters.get(key) );
        }
        for (   String key: customSIgParams.keySet()) {
            allParameters.put(key,customSIgParams.get(key) );
        }
        // 将参数Key按字典顺序排序
        String[] sortedKeys = allParameters.keySet().toArray(new String[] {});
        Arrays.sort(sortedKeys);

        final String SEPARATOR = "&";

        // 生成规范化请求字符串
        StringBuilder canonicalizedQueryString = new StringBuilder();
        for (String key : sortedKeys) {
            canonicalizedQueryString.append("&").append(percentEncode(key))
                    .append("=").append(percentEncode(allParameters.get(key).sMapValueContent));
        }

        // 生成用于计算签名的字符串 stringToSign
        StringBuilder stringToSign = new StringBuilder();
        stringToSign.append(sHttpMethod).append(SEPARATOR);
        stringToSign.append(percentEncode("/")).append(SEPARATOR);
        stringToSign.append(percentEncode(canonicalizedQueryString.toString()
                .substring(1)));


        // 注意accessKeySecret后面要加入一个字符"&"
        String signature = calculateSignature(accessKeySecret + "&",
                stringToSign.toString(), tSignatureAlgorithm);

        //返回对象中的type和签名的加密算法一致
        return new MapValue(tSignatureAlgorithm,signature);
    }

    private static String calculateSignature(String key, String stringToSign, ParaType tSignatureAlgorithm )
            throws Exception {
        // 使用HmacSHA1算法计算HMAC值
        String ALGORITHM = "HmacSHA1";
        if (tSignatureAlgorithm == ParaType.SIG_HMAC_SHA1) {
        } else if (tSignatureAlgorithm == ParaType.SIG_HMAC_SHA256) {
            ALGORITHM = "HmacSHA256";
        }
        Mac mac = Mac.getInstance(ALGORITHM);
        mac.init(new SecretKeySpec(key.getBytes(ENCODING), ALGORITHM));
        byte[] signData = mac.doFinal(stringToSign.getBytes(ENCODING));

        return Base64.encodeToString(signData, false);
    }

    private static String percentEncode(String value)
            throws UnsupportedEncodingException {
        // 使用URLEncoder.encode编码后，将"+","*","%7E"做替换即满足 API规定的编码规范
        return value != null ? URLEncoder.encode(value, ENCODING)
                .replace("+", "%20").replace("*", "%2A").replace("%7E", "~")
                : null;
    }
}
