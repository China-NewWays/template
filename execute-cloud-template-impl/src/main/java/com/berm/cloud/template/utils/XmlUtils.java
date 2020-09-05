package com.berm.cloud.template.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
/**
 * @version : 1.0.0
 * @ClassName: XmlUtils
 * @Description: TODO
 * @Auther: China
 * @Date: 2020/5/21 17:37
 */
public class XmlUtils {
    private static Logger logger = LoggerFactory.getLogger(XmlUtils.class);
    public static String TOKEN = "";
    private static String XML_FORMAT = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>";
    public static String setXml(Map<String,Object> map){
        StringBuffer sb = new StringBuffer();
        String str = "<?xml version=\"1.0\" encoding=\"gb2312\"?><rows><row";

        Iterator<Map.Entry<String, Object>> its = map.entrySet().iterator();
        sb.append(str);
        while (its.hasNext()) {
            Map.Entry<String, Object> entry = its.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            sb.append(" "+key + "=\"").append(value+ "\"");
            System.out.println(key+":"+value);
        }
        sb.append("></row></rows>");
        String xml = sb.toString();
        return xml;
    }
    public static JSONObject getInParams(String xml) {
        JSONObject params = new JSONObject();
        try {
            params = xml2JSONWithAttr(xml,false);
        }catch (DocumentException e) {
            e.printStackTrace();
        }
    if (params != null && params.getJSONObject("head") != null) {
            return params.getJSONObject("head");
        }
        return params;
    }

    /**
     * xml转map 带属性
     * @param xmlStr
     * @param needRootKey 是否需要在返回的map里加根节点键
     * @return
     * @throws DocumentException
     */
    public static Map xml2mapWithAttr(String xmlStr, boolean needRootKey) throws DocumentException {
        Document doc = DocumentHelper.parseText(xmlStr);
        Element root = doc.getRootElement();
        Map<String, Object> map = (Map<String, Object>) xml2mapWithAttr(root);
        if(root.elements().size()==0 && root.attributes().size()==0){
            return map; //根节点只有一个文本内容
        }
        if(needRootKey){
            //在返回的map里加根节点键（如果需要）
            Map<String, Object> rootMap = new HashMap<String, Object>();
            rootMap.put(root.getName(), map);
            return rootMap;
        }
        return map;
    }


    /**
     * xml转map 带属性
     * @param
     * @return
     */
    private static Map xml2mapWithAttr(Element element) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();

        List<Element> list = element.elements();
        List<Attribute> listAttr0 = element.attributes(); // 当前节点的所有属性的list
        for (Attribute attr : listAttr0) {
            map.put("@" + attr.getName(), attr.getValue());
        }
        if (list.size() > 0) {

            for (int i = 0; i < list.size(); i++) {
                Element iter = list.get(i);
                List mapList = new ArrayList();

                if (iter.elements().size() > 0) {
                    Map m = xml2mapWithAttr(iter);
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!(obj instanceof List)) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(m);
                        }
                        if (obj instanceof List) {
                            mapList = (List) obj;
                            mapList.add(m);
                        }
                        map.put(iter.getName(), mapList);
                    } else
                        map.put(iter.getName(), m);
                } else {

                    List<Attribute> listAttr = iter.attributes(); // 当前节点的所有属性的list
                    Map<String, Object> attrMap = null;
                    boolean hasAttributes = false;
                    if (listAttr.size() > 0) {
                        hasAttributes = true;
                        attrMap = new LinkedHashMap<String, Object>();
                        for (Attribute attr : listAttr) {
                            attrMap.put("@" + attr.getName(), attr.getValue());
                        }
                    }

                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!(obj instanceof List)) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            // mapList.add(iter.getText());
                            if (hasAttributes) {
                                attrMap.put("#text", iter.getText());
                                mapList.add(attrMap);
                            } else {
                                mapList.add(iter.getText());
                            }
                        }
                        if (obj instanceof List) {
                            mapList = (List) obj;
                            // mapList.add(iter.getText());
                            if (hasAttributes) {
                                attrMap.put("#text", iter.getText());
                                mapList.add(attrMap);
                            } else {
                                mapList.add(iter.getText());
                            }
                        }
                        map.put(iter.getName(), mapList);
                    } else {
                        // map.put(iter.getName(), iter.getText());
                        if (hasAttributes) {
                            attrMap.put("#text", iter.getText());
                            map.put(iter.getName(), attrMap);
                        } else {
                            map.put(iter.getName(), iter.getText());
                        }
                    }
                }
            }
        } else {
            // 根节点的
            if (listAttr0.size() > 0) {
                map.put("#text", element.getText());
            } else {
                map.put(element.getName(), element.getText());
            }
        }
        return map;
    }

    public static String mapToXmlNode(String nodeName,Map<String,Object> map){
        StringBuffer sb = new StringBuffer();
        sb.append("<").append(nodeName).append(">");
        sb.append(mapToXmlNode(map));
        sb.append("</").append(nodeName).append(">");
        String xml = sb.toString();
        return xml;
    }

    /**
     * map直接转xml字符串
     * 可遍历map集合
     * @param map
     * @return
     */
    public static String mapToXmlNode(Map<String,Object> map){
        StringBuffer sb = new StringBuffer();
        Iterator<Map.Entry<String, Object>> its = map.entrySet().iterator();
        while (its.hasNext()) {
            Map.Entry<String, Object> entry = its.next();
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value != null) {
                if(value instanceof ArrayList) {
                    ArrayList<Map<String, Object>> list = (ArrayList) value;
                    for(Map<String, Object> m:list) {
                        sb.append("<").append(key).append(">");
                        sb.append(mapToXmlNode(m));
                        sb.append("</").append(key).append(">\n");
                    }
                }else {
                    sb.append("<").append(key).append(">");
                    sb.append(value);
                    sb.append("</").append(key).append(">\n");
                }
            }
        }
        return sb.toString();
    }


    public static StringBuffer getStartXMLFormat(JSONObject inparams) {
        return getStartXMLFormat(inparams,true);
    }

    public static String getXmlFormat() {
        return XML_FORMAT;
    }

    public static StringBuffer getStartXMLFormat(JSONObject inparams,boolean isReponseResult) {
        StringBuffer sb = new StringBuffer(XmlUtils.getXmlFormat());
        sb.append("<xml>");
        String xmlHeader = XmlUtils.getXmlHeader(inparams);
        sb.append(xmlHeader);
        sb.append("<body>");
        if (isReponseResult) {
            String  xmlResponseResult = XmlUtils.getResponseResult(new JSONObject());
            sb.append(xmlResponseResult);
        }
        return sb;
    }

    public static String getXmlHeader(JSONObject inparams) {
        Map<String,Object> map = new HashMap<>();
        for (Map.Entry e: inparams.entrySet()) {
            String key = (String)e.getKey();
            Object value = e.getValue();
            map.put(key,value);
        }
        String xmlHeader = mapToXmlNode("head",map);
        return xmlHeader;
    }

    public static String getResponseResult(Map<String, Object> map) {
        Map outParams = new HashMap();
        outParams.put("IsSuccess",map.get("IsSuccess") == null ? 1 : ((int)map.get("IsSuccess") == 0 ? 1 : 0));
        outParams.put("Message","查询成功".equalsIgnoreCase(map.get("Message") + "") ? "ok" : map.get("Message"));
        String xmlResponseResult = mapToXmlNode("ResponseResult",outParams);
        return xmlResponseResult;
    }

    public static StringBuffer getEndXMLFormat(){
        StringBuffer sb = new StringBuffer();
        sb.append("</body>");
        sb.append(XmlUtils.mapToXmlNode("sign",new JSONObject()));
        sb.append("</xml>");
        return sb;
    }

    private static JSONObject responseResult(Integer code , String msg){
        if (code == null){
            code = -1;
        }
        JSONObject res = new JSONObject();
        res.put("code",code);
        res.put("msg",msg);
        return res;
    }
    public static JSONObject xml2JSONWithAttr(String xmlStr, boolean needRootKey) throws DocumentException {
        Document doc = DocumentHelper.parseText(xmlStr);
        Element root = doc.getRootElement();
        JSONObject resJSON = xml2JSONWithAttr(root);
        if (root.elements().size() == 0 && root.attributes().size() == 0) {
            return resJSON;
        } else if (needRootKey) {
            JSONObject rootMap = new JSONObject();
            rootMap.put(root.getName(), resJSON);
            return rootMap;
        } else {
            return resJSON;
        }
    }
    private static JSONObject xml2JSONWithAttr(Element element) {
        JSONObject resJSON = new JSONObject();
        List<Element> list = element.elements();
        List<Attribute> listAttr0 = element.attributes();
        Iterator var4 = listAttr0.iterator();

        while(var4.hasNext()) {
            Attribute attr = (Attribute)var4.next();
            resJSON.put(attr.getName(), attr.getValue());
        }

        if (list.size() > 0) {
            for(int i = 0; i < list.size(); ++i) {
                Element iter = (Element)list.get(i);
                List mapList = new ArrayList();
                if (iter.elements().size() > 0) {
                    JSONObject json = xml2JSONWithAttr(iter);
                    if (!StringUtils.isEmpty(resJSON.getString(iter.getName()))) {
                        Object obj = json.get(iter.getName());
                        if (!(obj instanceof List)) {
                            mapList = new ArrayList();
                            ((List)mapList).add(obj);
                            ((List)mapList).add(json);
                        }

                        if (obj instanceof List) {
                            mapList = (List)obj;
                            ((List)mapList).add(json);
                        }

                        resJSON.put(iter.getName(), mapList);
                    } else {
                        resJSON.put(iter.getName(), json);
                    }
                } else {
                    List<Attribute> listAttr = iter.attributes();
                    JSONObject attrJSON = null;
                    boolean hasAttributes = false;
                    if (listAttr.size() > 0) {
                        hasAttributes = true;
                        attrJSON = new JSONObject();
                        Iterator var10 = listAttr.iterator();

                        while(var10.hasNext()) {
                            Attribute attr = (Attribute)var10.next();
                            attrJSON.put(attr.getName(), attr.getValue());
                        }
                    }

                    if (resJSON.get(iter.getName()) != null) {
                        Object obj = resJSON.get(iter.getName());
                        if (!(obj instanceof List)) {
                            mapList = new ArrayList();
                            ((List)mapList).add(obj);
                            if (hasAttributes) {
                                if (!StringUtils.isEmpty(iter.getText())) {
                                    attrJSON.put("text", iter.getText());
                                }

                                ((List)mapList).add(attrJSON);
                            } else {
                                ((List)mapList).add(iter.getText());
                            }
                        }

                        if (obj instanceof List) {
                            mapList = (List)obj;
                            if (hasAttributes) {
                                if (!StringUtils.isEmpty(iter.getText())) {
                                    attrJSON.put("text", iter.getText());
                                }

                                ((List)mapList).add(attrJSON);
                            } else {
                                ((List)mapList).add(iter.getText());
                            }
                        }

                        resJSON.put(iter.getName(), mapList);
                    } else if (hasAttributes) {
                        if (!StringUtils.isEmpty(iter.getText())) {
                            attrJSON.put("text", iter.getText());
                        }

                        resJSON.put(iter.getName(), attrJSON);
                    } else {
                        resJSON.put(iter.getName(), iter.getText());
                    }
                }
            }
        } else if (listAttr0.size() > 0) {
            if (!StringUtils.isEmpty(element.getText())) {
                resJSON.put("text", element.getText());
            }
        } else {
            resJSON.put(element.getName(), element.getText());
        }

        return resJSON;
    }

    public static String formatXml(String xmlStr) throws DocumentException, IOException {
        Document document = DocumentHelper.parseText(xmlStr);
        return formatXml(document);
    }

    public static String formatXml(Document document) throws DocumentException, IOException {
        OutputFormat format = OutputFormat.createPrettyPrint();
        StringWriter writer = new StringWriter();
        XMLWriter xmlWriter = new XMLWriter(writer, format);
        xmlWriter.write(document);
        xmlWriter.close();
        return writer.toString();
    }
}
