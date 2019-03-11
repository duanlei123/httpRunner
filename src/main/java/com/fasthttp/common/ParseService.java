package com.fasthttp.common;

import com.jayway.jsonpath.*;
import com.jayway.jsonpath.spi.mapper.JsonSmartMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingException;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: duanlei
 * @Date: 2019/3/11 10:45 AM
 * @Version 1.0
 */
public class ParseService {

    private DocumentContext json;
    private boolean bParseXML =false;
    private String content = null;

    private static String dateFormatString ="";

    public static void setCustomDateFormat (String customDateFormat) {
        dateFormatString =customDateFormat;
    }
    public void ParseXml(String xmlString) throws Exception {
        bParseXML = true;
        content = xmlString;
        String json = xml2json(xmlString);
        ParseJson(json);
    }

    public void ParseJson(String jsonString){

        if (jsonString == null) {
            if (bParseXML) {
                throw new NullPointerException("Parse xml string is null!!");
            } else {
                throw new NullPointerException("Parse json string is null!!");
            }
        }
        if (!bParseXML) {
            content = jsonString;
        }
        Configuration configuration = Configuration.defaultConfiguration();

        class EnhancedMappingProvider extends JsonSmartMappingProvider {

            MappingProvider provider = null;
            public EnhancedMappingProvider(MappingProvider provider) {
                this.provider = provider;
            }
            public <T> T map(Object source, Class<T> targetType, Configuration configuration) {
                T result;
                try {
                    result= provider.map(source, targetType, configuration);
                } catch (MappingException e) {
                    if (targetType.equals(Date.class)) {
                        if (dateFormatString !=null && dateFormatString.length()>0) {
                            try {
                                result =(T)new SimpleDateFormat(dateFormatString).parse(source.toString());
                                return result;
                            } catch (ParseException e1) {
                            }
                        }

                        try {
                            result =(T)DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).parse(source.toString());
                            return result;
                        } catch (ParseException e1) {
                        }
                        try {
                            result =(T)DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).parse(source.toString());
                            return result;
                        } catch (ParseException e1) {
                        }
                        try {
                            result =(T)DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).parse(source.toString());
                            return result;
                        } catch (ParseException e1) {
                        }

                        try {
                            result =(T)DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).parse(source.toString());
                            return result;
                        } catch (ParseException e1) {
                        }
                        try {
                            result =(T)new SimpleDateFormat("yy/MM/dd aHH:mm").parse(source.toString());
                            return result;
                        } catch (ParseException e1) {
                        }
                        try {
                            result =(T)new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(source.toString());
                            return result;
                        } catch (ParseException e1) {
                        }
                        try {
                            result =(T)new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(source.toString());
                            return result;
                        } catch (ParseException e1) {
                        }
                        try {
                            result =(T)new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(source.toString());
                            return result;
                        } catch (ParseException e1) {
                        }
                        try {
                            result =(T)new SimpleDateFormat("yyyy-MM-dd").parse(source.toString());
                            return result;
                        } catch (ParseException e1) {
                        }
                        try {
                            result =(T)new SimpleDateFormat("yyyy/MM/dd").parse(source.toString());
                            return result;
                        } catch (ParseException e1) {
                            throw e;
                        }
                    } else {
                        throw e;
                    }
                }
                return result;
            }
        }

        MappingProvider provider = configuration.mappingProvider();
        configuration = configuration.mappingProvider( new EnhancedMappingProvider(provider) );
        json = JsonPath.parse(jsonString,configuration);
    }

    private static String xml2json(String xml) throws Exception {
        JSONObject obj = XML.toJSONObject(xml);
        return obj.toString();
    }

    public <T> T jsonPath(String jsonPath, Predicate... var3) {
        if (this.json == null) {
            return null;
        }
        return this.json.read(jsonPath, var3);
    }

    public <T> T jsonPath(String jsonPath, Class<T> type, Predicate... var3) {
        if (this.json == null) {
            return null;
        }
        return this.json.read(jsonPath,type,var3);
    }
    public <T> List<T> jsonPathList(String jsonPath, Class<T> type, Predicate... var3) {
        if (this.json == null) {
            return null;
        }
        List<T> arrayList = new ArrayList<T>();
        Object obs =  this.json.read(jsonPath,var3);
        if (obs ==null) {
            return arrayList;
        }
        if (!(obs instanceof  List)) {
            throw new RuntimeException("Json Object is not a List!!");
        }
        List list = (List)obs;
        for (Object ob: list) {
            T thing =  this.json.configuration().mappingProvider().map(ob,type,this.json.configuration());
            arrayList.add(thing);
        }
        return arrayList;
    }

    public Vector<String> findElement(String sName, boolean isFullName) {
        return findElement(sName,isFullName,false);
    }
    public Vector<String> findElement(String sName, boolean isFullName, boolean bOnldyFindOne) {
        Vector<String> expectValueList = new Vector<String>();
        if (sName == null || sName.trim().length() == 0 || this.json == null) {
            return expectValueList;
        }
        sName = sName.trim();
        if (!isFullName) {
            if (sName.contains(":")) {
                isFullName = true;
            }
        }
        Object result;
        String jsonPath;
        List<Object> results = new ArrayList<Object>();

        if (!isFullName) {
            jsonPath = "$.."+ sName;
            result = this.json.read(jsonPath);
            // 必然返回Array
            if (result instanceof net.minidev.json.JSONArray) {
                net.minidev.json.JSONArray arr = (net.minidev.json.JSONArray)result;
                for (int i=0 ;i<arr.size();i++) {
                    results.add(arr.get(i));
                }
            } else {
                results.add(result);
            }
        } else {
            String [] eleNames=sName.split(":");
            jsonPath = "$";
            result = this.json.read(jsonPath);
            if (result instanceof net.minidev.json.JSONArray) {
                jsonPath+="[*]";
            }
            for (String ele:eleNames) {

                // 路径不存在，后续不用查找了
                jsonPath=jsonPath +"." + ele ;
                try {
                    result = this.json.read(jsonPath);
                } catch (PathNotFoundException e) {
                    break;
                }
                //System.out.println(jsonPath + "==" + result);
                // 检查路径 元素本身是数组  还是 多个
                if ( result instanceof net.minidev.json.JSONArray ) {
                    net.minidev.json.JSONArray arr = (net.minidev.json.JSONArray)result;
                    boolean bAllSame = true;
                    // 按数组方式取值，查看各个元素是否一致
                    for (int i=0 ;i<arr.size();i++) {
                        Object ob =  this.json.read(jsonPath + "[" + i +"]");
                        //System.out.println("  " + jsonPath + "[" + i +"]" + "==>" + ob);
                        Object ob2 =  arr.get(i);
                        if (!ob.equals(ob2)) {
                            bAllSame = false;
                            break;
                        }
                    }
                    if (bAllSame) {
                        jsonPath +="[*]";
                    }
                }
            }
            try {
                result = this.json.read(jsonPath);
                //System.out.println("  "+jsonPath + ":" + result);
                results.add(result);
            } catch (PathNotFoundException e) {
                // 路径不存在
            }
        }
        for (Object r : results) {
            if (r instanceof List) {
                List list = (List) r;
                for (Object a :list) {
                    addElement(expectValueList, a);
                    if (bOnldyFindOne) {
                        return expectValueList;
                    }
                }
            }  else {
                addElement(expectValueList,r);
                if (bOnldyFindOne) {
                    return expectValueList;
                }
            }
        }
        return expectValueList;
    }

    private void addElement(Vector<String> expectValueList , Object obj) {
        if (obj instanceof List || obj instanceof HashMap) {
            DocumentContext context = JsonPath.parse(obj);
            expectValueList.add(context.jsonString());
        } else {
            if (obj == null) {
                expectValueList.add("null");
            } else {
                expectValueList.add(obj.toString());
            }

        }
    }

    public String  toPrettyJson() {
        if (this.json == null) {
            return "";
        }
        String json = this.json.jsonString();
        try {
            JSONObject obj= new JSONObject(json);
            return obj.toString(2);
        } catch(JSONException je1) {
            try {
                JSONArray obj= new JSONArray(json);
                return obj.toString(2);
            } catch (JSONException je2) {
                return "";
            }
        }
    }
    public void  printAllElement() {
        printAllElement(false);
    }
    public void  printAllElement(boolean printOnlyFullPath) {

        System.out.println("********** 打印解析结果  ************");
        System.out.println("--路径列表");
        String[] paths =  this.getAllElementPath();
        Set<String> pathSets = new HashSet<String>();
        for (String path : paths) {
            if (path.charAt(0)==':') {
                pathSets.add(path.substring(1));
            } else {
                pathSets.add(path);
            }
        }
        for (String path2: pathSets) {
            System.out.println(path2);
        }
        for (String path : paths) {
            if (path.charAt(0)!=':') {
                Vector<String> result =this.findElement(path, true);
                System.out.println("--查找 " + path +  " 结果(全路径名匹配) 数量: "  + result.size() + " --" );
                for (String r :result) {
                    System.out.println("  " + r);
                }
            } else if (path.equals("::")) {
                Vector<String> result =this.findElement(":", true);
                System.out.println("--查找 " + ":" +  " 结果(全路径名匹配) 数量: "  + result.size() + " --" );
                for (String r :result) {
                    System.out.println("  " + r);
                }
            }
        }
        if (!printOnlyFullPath) {
            for (String path : paths) {
                if (path.charAt(0)==':'&&path.charAt(1)!=':') {
                    Vector<String> result =this.findElement(path.substring(1), false);
                    System.out.println("--查找 " + path.substring(1) +  " 结果(仅匹配名称) 数量: "  + result.size() + " --");
                    for (String r :result) {
                        System.out.println("  " + r);
                    }
                }
            }
        }
        System.out.println("**********************");
    }

    private  String[] getAllElementPath( ) {
        Set<String> pathSet= new HashSet<String>();
        if (this.json == null ) {
            return new String[]{};
        }
        Object obj = this.json.json();
        getAllElementPath("", obj, pathSet);
        if (content!=null && content.trim().length()>0) {
            pathSet.add("::");
        }
        return pathSet.toArray( new String[pathSet.size()] );
    }


    private  void getAllElementPath(String pathKey, Object object,   Set<String> pathSet) {
        //System.out.println(/*"["+ object.getClass().getName() +"]"+"    "+*/ pathKey + " = " + object);
        String keysep= ":";
        if (object == null) {
            return;
        }

        if (pathKey==null || pathKey.length() == 0) {
            keysep ="";
        } else {
            pathSet.add(pathKey);
        }
        if ( object instanceof HashMap ) {
            HashMap map = (HashMap) object;
            for (Object key: map.keySet() ) {
                if (key instanceof  String) {
                    Object value =map.get(key);
                    if (bParseXML && key.equals("#text")) {
                    } else {
                        pathSet.add(":" + (String) key);  // 可以部分匹配的东东
                    }
                    getAllElementPath(pathKey + keysep + (String) key, value, pathSet);
                }
            }
        } else if( object instanceof List) {
            List list = (List) object;
            //int index = 0;
            for (Object value: list) {
                getAllElementPath(pathKey, value, pathSet);
                // index++;
            }
        }
    }



//测试转换类
// static class MyClass {
//
//       private Integer id;
//        private String key;
//
//        public Integer getId() {
//            return id;
//        }
//
//        public void setId(Integer id) {
//            this.id = id;
//        }
//
//        public String getKey() {
//            return key;
//        }
//
//        public void setKey(String key) {
//            this.key = key;
//        }
//    }

//    public static void main(String[] args) {
        //String json = "{\"code\":[0,1,2],\"message\":\"success\",\"data\":{\"queryResult\":[[\"Authorization exception - Authorization Failed [4031], You have NO privilege 'odps:Select' on {acs:odps:*:projects/odpstablelabletest/tables/labeltest}. CheckLabelSecurity failed. The sensitive label of column 'value' is 3, but your effective label is 2. Context ID:e1a59998-85e6-488d-a7eb-b1ab9e88b385.\\n\"], [\"Authorization exception - Authorization Failed [4032], You have NO privilege 'odps:Select' on {acs:odps:*:projects/odpstablelabletest/tables/labeltest}. CheckLabelSecurity failed. The sensitive label of column 'value' is 3, but your effective label is 3. Context ID:e1a59998-85e6-488d-a7eb-b1ab9e88b385.\\n\"]],\"unQueryResult\":\"\",\"SQLCommand\":\"select value from labeltest;\",\"instanceID\":\"20151225091340766gat07xs6\",\"query\":true,\"status\":\"Terminated\"}}";
        //String json= "[{\"id\":\"2011-07-19 上午3:33\", \"key\":\"DT1\"},{\"id\":2 , \"key\":\"DT2\"},{\"id\":3, \"key\":\"DT3\"} ]";
        //String json= "[{\"id\": 1, \"key\":\"DT1\"},{\"id\":2 , \"key\":\"DT2\"},{\"id\":3, \"key\":\"DT3\"} ]";
        //String json= "[1,2,3 ]";

//     String xml ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//                "<recipe>\n" +
//                "<recipename>Ice Cream Sundae</recipename>\n" +
//                "<ingredlist>\n" +
//                "<listitem>\n" +
//                "<quantity>3</quantity>\n" +
//                "<itemdescription>chocolate syrup or chocolate fudge</itemdescription>\n" +
//                "</listitem>\n" +
//                "<listitem>\n" +
//                "<a>"+
//                "<quantity>2</quantity>\n" +
//                "</a>"+
//                "<itemdescription>nuts</itemdescription>\n" +
//                "</listitem>\n" +
//                "<listitem>\n" +
//                "<quantity>1</quantity>\n" +
//                "<itemdescription>cherry</itemdescription>\n" +
//                "</listitem>\n" +
//                "</ingredlist>\n" +
//                "<preptime>5 minutes</preptime>\n" +
//                "</recipe>";
//        String xml ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//                "<recipe type=\"dessert\"> 2\n" +
//                "<recipename cuisine=\"american\" servings=\"1\">Ice Cream Sundae</recipename>\n" +
//                "<recipename cuisine=\"american2\" servings=\"3\">Ice Cream Sundae2</recipename>\n" +
//                "<preptime>5 minutes</preptime>\n" +
//                "</recipe>";

//        ParseService service = new ParseService();
//        try {
//            service.ParseJson(json);
//            service.ParseXml(xml);
//            System.out.println(service.toPrettyJson());
//            service.printAllElement();
//            System.out.println(new Date());
//            System.out.println(service.jsonPath("$[0].id").getClass());
//            System.out.println(service.jsonPath("$[0].id",Date.class).getClass());
//            System.out.println(service.jsonPath("$[0].id",Date.class));
//            System.out.println(service.jsonPath("$[*]"));
//            MyClass myTests= service.jsonPath("$[2]", MyClass.class);
//            System.out.println(myTests.getId() + " " + myTests.getKey());
//            System.out.println("---------------------");
//            List<MyClass> list = service.jsonPathList("$[*]",MyClass.class);
//
//            for (MyClass c: list) {
//                System.out.println(c.getId() + " " + c.getKey());
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    public static void main(String[] args) {
//        String json ="{\n" +
//                "  \"actions\" : [\n" +
//                "    [\n" +
//                "      {\n" +
//                "        \"name\" : \"CiMergeJob\",\n" +
//                "        \"value\" : \"Git 111\"\n" +
//                "      }\n" +
//                "    ],\n" +
//                "    {\n" +
//                "      \"causes\" : [\n" +
//                "        {\n" +
//                "          \"shortDescription\" : \"Started by user jzm\",\n" +
//                "          \"userId\" : \"jzm\",\n" +
//                "          \"userName\" : \"jzm\"\n" +
//                "        }\n" +
//                "      ]\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \n" +
//                "    },\n" +
//                "    {\n" +
//                "      \n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"buildsByBranchName\" : {\n" +
//                "        \"refs/remotes/origin/master\" : {\n" +
//                "          \"buildNumber\" : 1,\n" +
//                "          \"buildResult\" : null,\n" +
//                "          \"marked\" : {\n" +
//                "            \"SHA1\" : \"12d93c2c2252c0cbb24368533c62ef79869bc0f9\",\n" +
//                "            \"branch\" : [\n" +
//                "              {\n" +
//                "                \"SHA1\" : \"12d93c2c2252c0cbb24368533c62ef79869bc0f9\",\n" +
//                "                \"name\" : \"refs/remotes/origin/master\"\n" +
//                "              }\n" +
//                "            ]\n" +
//                "          },\n" +
//                "          \"revision\" : {\n" +
//                "            \"SHA1\" : \"12d93c2c2252c0cbb24368533c62ef79869bc0f9\",\n" +
//                "            \"branch\" : [\n" +
//                "              {\n" +
//                "                \"SHA1\" : \"12d93c2c2252c0cbb24368533c62ef79869bc0f9\",\n" +
//                "                \"name\" : \"refs/remotes/origin/master\"\n" +
//                "              }\n" +
//                "            ]\n" +
//                "          }\n" +
//                "        }\n" +
//                "      },\n" +
//                "      \"lastBuiltRevision\" : {\n" +
//                "        \"SHA1\" : \"12d93c2c2252c0cbb24368533c62ef79869bc0f9\",\n" +
//                "        \"branch\" : [\n" +
//                "          {\n" +
//                "            \"SHA1\" : \"12d93c2c2252c0cbb24368533c62ef79869bc0f9\",\n" +
//                "            \"name\" : \"refs/remotes/origin/master\"\n" +
//                "          }\n" +
//                "        ]\n" +
//                "      },\n" +
//                "      \"remoteUrls\" : [\n" +
//                "        \"git@gitlab01.qsc.com:TD/dtpNew.git\"\n" +
//                "      ],\n" +
//                "      \"scmName\" : \"\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"tags\" : [\n" +
//                "        \n" +
//                "      ]\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \n" +
//                "    },\n" +
//                "    {\n" +
//                "      \n" +
//                "    },\n" +
//                "    {\n" +
//                "      \n" +
//                "    },\n" +
//                "    {\n" +
//                "      \n" +
//                "    },\n" +
//                "    {\n" +
//                "      \n" +
//                "    },\n" +
//                "    {\n" +
//                "      \n" +
//                "    },\n" +
//                "    {\n" +
//                "      \n" +
//                "    },\n" +
//                "    {\n" +
//                "      \n" +
//                "    }\n" +
//                "  ],\n" +
//                "  \"artifacts\" : [\n" +
//                "    \n" +
//                "  ],\n" +
//                "  \"building\" : false,\n" +
//                "  \"description\" : null,\n" +
//                "  \"displayName\" : \"#1\",\n" +
//                "  \"duration\" : 122052,\n" +
//                "  \"estimatedDuration\" : 122052,\n" +
//                "  \"executor\" : null,\n" +
//                "  \"fullDisplayName\" : \"MyJob12345647ba0bf0-3a3c-47f7-a75b-d79821854e2b #1\",\n" +
//                "  \"id\" : \"1\",\n" +
//                "  \"keepLog\" : false,\n" +
//                "  \"number\" : 1,\n" +
//                "  \"queueId\" : 2631,\n" +
//                "  \"result\" : \"FAILURE\",\n" +
//                "  \"timestamp\" : 1464918814381,\n" +
//                "  \"url\" : \"http://10.150.0.81/jenkins/job/MyJob12345647ba0bf0-3a3c-47f7-a75b-d79821854e2b/1/\",\n" +
//                "  \"builtOn\" : \"slave83\",\n" +
//                "  \"changeSet\" : [\n" +
//                "    \n" +
//                "  ],\n" +
//                "  \"culprits\" : [\n" +
//                "    \n" +
//                "  ],\n" +
//                "  \"fingerprint\" : [\n" +
//                "    \n" +
//                "  ],\n" +
//                "  \"subBuilds\" : [\n" +
//                "    {\n" +
//                "      \"abort\" : false,\n" +
//                "      \"build\" : {\n" +
//                "        \"actions\" : [\n" +
//                "          [\n" +
//                "            {\n" +
//                "              \"name\" : \"CiMergeJob\",\n" +
//                "              \"value\" : \"Git 111\"\n" +
//                "            }\n" +
//                "          ],\n" +
//                "          {\n" +
//                "            \n" +
//                "          },\n" +
//                "          {\n" +
//                "            \"causes\" : [\n" +
//                "              {\n" +
//                "                \"shortDescription\" : \"Started by upstream project \\\"MyJob12345647ba0bf0-3a3c-47f7-a75b-d79821854e2b\\\" build number 1\",\n" +
//                "                \"upstreamBuild\" : 1,\n" +
//                "                \"upstreamProject\" : \"MyJob12345647ba0bf0-3a3c-47f7-a75b-d79821854e2b\",\n" +
//                "                \"upstreamUrl\" : \"job/MyJob12345647ba0bf0-3a3c-47f7-a75b-d79821854e2b/\"\n" +
//                "              }\n" +
//                "            ]\n" +
//                "          },\n" +
//                "          {\n" +
//                "            \n" +
//                "          },\n" +
//                "          {\n" +
//                "            \n" +
//                "          },\n" +
//                "          {\n" +
//                "            \"buildsByBranchName\" : {\n" +
//                "              \"refs/remotes/origin/JunoDevelopment\" : {\n" +
//                "                \"buildNumber\" : 161,\n" +
//                "                \"buildResult\" : null,\n" +
//                "                \"marked\" : {\n" +
//                "                  \"SHA1\" : \"82edd876cde47a355c8c035092f635aa1ccba5b4\",\n" +
//                "                  \"branch\" : [\n" +
//                "                    {\n" +
//                "                      \"SHA1\" : \"82edd876cde47a355c8c035092f635aa1ccba5b4\",\n" +
//                "                      \"name\" : \"refs/remotes/origin/JunoDevelopment\"\n" +
//                "                    }\n" +
//                "                  ]\n" +
//                "                },\n" +
//                "                \"revision\" : {\n" +
//                "                  \"SHA1\" : \"82edd876cde47a355c8c035092f635aa1ccba5b4\",\n" +
//                "                  \"branch\" : [\n" +
//                "                    {\n" +
//                "                      \"SHA1\" : \"82edd876cde47a355c8c035092f635aa1ccba5b4\",\n" +
//                "                      \"name\" : \"refs/remotes/origin/JunoDevelopment\"\n" +
//                "                    }\n" +
//                "                  ]\n" +
//                "                }\n" +
//                "              },\n" +
//                "              \"refs/remotes/origin/master\" : {\n" +
//                "                \"buildNumber\" : 3,\n" +
//                "                \"buildResult\" : null,\n" +
//                "                \"marked\" : {\n" +
//                "                  \"SHA1\" : \"00fa37f2c8249c3f226484ba3f2d470a5a7f9f3d\",\n" +
//                "                  \"branch\" : [\n" +
//                "                    {\n" +
//                "                      \"SHA1\" : \"00fa37f2c8249c3f226484ba3f2d470a5a7f9f3d\",\n" +
//                "                      \"name\" : \"refs/remotes/origin/master\"\n" +
//                "                    }\n" +
//                "                  ]\n" +
//                "                },\n" +
//                "                \"revision\" : {\n" +
//                "                  \"SHA1\" : \"00fa37f2c8249c3f226484ba3f2d470a5a7f9f3d\",\n" +
//                "                  \"branch\" : [\n" +
//                "                    {\n" +
//                "                      \"SHA1\" : \"00fa37f2c8249c3f226484ba3f2d470a5a7f9f3d\",\n" +
//                "                      \"name\" : \"refs/remotes/origin/master\"\n" +
//                "                    }\n" +
//                "                  ]\n" +
//                "                }\n" +
//                "              }\n" +
//                "            },\n" +
//                "            \"lastBuiltRevision\" : {\n" +
//                "              \"SHA1\" : \"82edd876cde47a355c8c035092f635aa1ccba5b4\",\n" +
//                "              \"branch\" : [\n" +
//                "                {\n" +
//                "                  \"SHA1\" : \"82edd876cde47a355c8c035092f635aa1ccba5b4\",\n" +
//                "                  \"name\" : \"refs/remotes/origin/JunoDevelopment\"\n" +
//                "                }\n" +
//                "              ]\n" +
//                "            },\n" +
//                "            \"remoteUrls\" : [\n" +
//                "              \"git@gitlab02.qsc.com:CSpot/oslo.messaging.git\"\n" +
//                "            ],\n" +
//                "            \"scmName\" : \"\"\n" +
//                "          },\n" +
//                "          {\n" +
//                "            \"tags\" : [\n" +
//                "              \n" +
//                "            ]\n" +
//                "          },\n" +
//                "          {\n" +
//                "            \n" +
//                "          },\n" +
//                "          {\n" +
//                "            \n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"artifacts\" : [\n" +
//                "          \n" +
//                "        ],\n" +
//                "        \"building\" : false,\n" +
//                "        \"description\" : null,\n" +
//                "        \"displayName\" : \"#161\",\n" +
//                "        \"duration\" : 11662,\n" +
//                "        \"estimatedDuration\" : 10967,\n" +
//                "        \"executor\" : null,\n" +
//                "        \"fullDisplayName\" : \"CSPOT-Master-Check-oslo.messaging #161\",\n" +
//                "        \"id\" : \"161\",\n" +
//                "        \"keepLog\" : false,\n" +
//                "        \"number\" : 161,\n" +
//                "        \"queueId\" : 2632,\n" +
//                "        \"result\" : \"SUCCESS\",\n" +
//                "        \"timestamp\" : 1464918923839,\n" +
//                "        \"url\" : \"http://10.150.0.81/jenkins/job/CSPOT-Master-Check-oslo.messaging/161/\",\n" +
//                "        \"builtOn\" : \"slave84\",\n" +
//                "        \"changeSet\" : [\n" +
//                "          {\n" +
//                "            \"affectedPaths\" : [\n" +
//                "              \"oslo_messaging/_drivers/impl_rabbit.py\"\n" +
//                "            ],\n" +
//                "            \"commitId\" : \"bc9d47446767292779c7fd84c76797f7390d941c\",\n" +
//                "            \"timestamp\" : 1461131142000,\n" +
//                "            \"author\" : {\n" +
//                "              \"absoluteUrl\" : \"http://10.150.0.81/jenkins/user/chenl0281\",\n" +
//                "              \"description\" : null,\n" +
//                "              \"fullName\" : \"chenl0281\",\n" +
//                "              \"id\" : \"chenl0281\",\n" +
//                "              \"property\" : [\n" +
//                "                {\n" +
//                "                  \n" +
//                "                },\n" +
//                "                {\n" +
//                "                  \n" +
//                "                },\n" +
//                "                {\n" +
//                "                  \"triggers\" : [\n" +
//                "                    \n" +
//                "                  ]\n" +
//                "                },\n" +
//                "                {\n" +
//                "                  \n" +
//                "                },\n" +
//                "                {\n" +
//                "                  \n" +
//                "                },\n" +
//                "                {\n" +
//                "                  \"insensitiveSearch\" : false\n" +
//                "                },\n" +
//                "                {\n" +
//                "                  \"address\" : \"chenl0281@qsc.com\"\n" +
//                "                }\n" +
//                "              ]\n" +
//                "            },\n" +
//                "            \"comment\" : \"CSPOT-1563\\n\",\n" +
//                "            \"date\" : \"2016-04-20 13:45:42 +0800\",\n" +
//                "            \"id\" : \"bc9d47446767292779c7fd84c76797f7390d941c\",\n" +
//                "            \"msg\" : \"CSPOT-1563\",\n" +
//                "            \"paths\" : [\n" +
//                "              {\n" +
//                "                \"editType\" : \"edit\",\n" +
//                "                \"file\" : \"oslo_messaging/_drivers/impl_rabbit.py\"\n" +
//                "              }\n" +
//                "            ]\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"culprits\" : [\n" +
//                "          {\n" +
//                "            \"absoluteUrl\" : \"http://10.150.0.81/jenkins/user/chenl0281\",\n" +
//                "            \"description\" : null,\n" +
//                "            \"fullName\" : \"chenl0281\",\n" +
//                "            \"id\" : \"chenl0281\",\n" +
//                "            \"property\" : [\n" +
//                "              {\n" +
//                "                \n" +
//                "              },\n" +
//                "              {\n" +
//                "                \n" +
//                "              },\n" +
//                "              {\n" +
//                "                \"triggers\" : [\n" +
//                "                  \n" +
//                "                ]\n" +
//                "              },\n" +
//                "              {\n" +
//                "                \n" +
//                "              },\n" +
//                "              {\n" +
//                "                \n" +
//                "              },\n" +
//                "              {\n" +
//                "                \"insensitiveSearch\" : false\n" +
//                "              },\n" +
//                "              {\n" +
//                "                \"address\" : \"chenl0281@qsc.com\"\n" +
//                "              }\n" +
//                "            ]\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"fingerprint\" : [\n" +
//                "          \n" +
//                "        ]\n" +
//                "      },\n" +
//                "      \"buildNumber\" : 161,\n" +
//                "      \"duration\" : \"11 sec\",\n" +
//                "      \"icon\" : \"blue.png\",\n" +
//                "      \"jobName\" : \"CSPOT-Master-Check-oslo.messaging\",\n" +
//                "      \"parentBuildNumber\" : 1,\n" +
//                "      \"parentJobName\" : \"MyJob12345647ba0bf0-3a3c-47f7-a75b-d79821854e2b\",\n" +
//                "      \"phaseName\" : \"Check!\",\n" +
//                "      \"result\" : \"SUCCESS\",\n" +
//                "      \"retry\" : false,\n" +
//                "      \"url\" : \"job/CSPOT-Master-Check-oslo.messaging/161/\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"abort\" : false,\n" +
//                "      \"build\" : {\n" +
//                "        \"actions\" : [\n" +
//                "          [\n" +
//                "            {\n" +
//                "              \"name\" : \"CiMergeJob\",\n" +
//                "              \"value\" : \"Git 111\"\n" +
//                "            }\n" +
//                "          ],\n" +
//                "          {\n" +
//                "            \n" +
//                "          },\n" +
//                "          {\n" +
//                "            \"causes\" : [\n" +
//                "              {\n" +
//                "                \"shortDescription\" : \"Started by upstream project \\\"MyJob12345647ba0bf0-3a3c-47f7-a75b-d79821854e2b\\\" build number 1\",\n" +
//                "                \"upstreamBuild\" : 1,\n" +
//                "                \"upstreamProject\" : \"MyJob12345647ba0bf0-3a3c-47f7-a75b-d79821854e2b\",\n" +
//                "                \"upstreamUrl\" : \"job/MyJob12345647ba0bf0-3a3c-47f7-a75b-d79821854e2b/\"\n" +
//                "              }\n" +
//                "            ]\n" +
//                "          },\n" +
//                "          {\n" +
//                "            \n" +
//                "          },\n" +
//                "          {\n" +
//                "            \n" +
//                "          },\n" +
//                "          {\n" +
//                "            \n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"artifacts\" : [\n" +
//                "          {\n" +
//                "            \"displayPath\" : \"profile.txt\",\n" +
//                "            \"fileName\" : \"profile.txt\",\n" +
//                "            \"relativePath\" : \"profile.txt\"\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"building\" : false,\n" +
//                "        \"description\" : null,\n" +
//                "        \"displayName\" : \"#68\",\n" +
//                "        \"duration\" : 310,\n" +
//                "        \"estimatedDuration\" : 143,\n" +
//                "        \"executor\" : null,\n" +
//                "        \"fullDisplayName\" : \"test-dtp-test-param #68\",\n" +
//                "        \"id\" : \"68\",\n" +
//                "        \"keepLog\" : false,\n" +
//                "        \"number\" : 68,\n" +
//                "        \"queueId\" : 2633,\n" +
//                "        \"result\" : \"FAILURE\",\n" +
//                "        \"timestamp\" : 1464918923840,\n" +
//                "        \"url\" : \"http://10.150.0.81/jenkins/job/test-dtp-test-param/68/\",\n" +
//                "        \"builtOn\" : \"slave83\",\n" +
//                "        \"changeSet\" : [\n" +
//                "          \n" +
//                "        ],\n" +
//                "        \"culprits\" : [\n" +
//                "          \n" +
//                "        ],\n" +
//                "        \"fingerprint\" : [\n" +
//                "          \n" +
//                "        ]\n" +
//                "      },\n" +
//                "      \"buildNumber\" : 68,\n" +
//                "      \"duration\" : \"0.31 sec\",\n" +
//                "      \"icon\" : \"red.png\",\n" +
//                "      \"jobName\" : \"test-dtp-test-param\",\n" +
//                "      \"parentBuildNumber\" : 1,\n" +
//                "      \"parentJobName\" : \"MyJob12345647ba0bf0-3a3c-47f7-a75b-d79821854e2b\",\n" +
//                "      \"phaseName\" : \"Check!\",\n" +
//                "      \"result\" : \"FAILURE\",\n" +
//                "      \"retry\" : false,\n" +
//                "      \"url\" : \"job/test-dtp-test-param/68/\"\n" +
//                "    }\n" +
//                "  ]\n" +
//                "}";
//
//        ParseService service = new ParseService();
//        try {
//            service.ParseJson(json);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        service.printAllElement(true);
//    }

}
