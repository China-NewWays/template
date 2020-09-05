package com.berm.cloud.template.service;

import com.alibaba.fastjson.JSONObject;
import com.berm.cloud.template.utils.XmlUtils;

import java.util.Map;

public abstract class ImplBase {
    private His his = new HisService();

    /**
     * 自动调用，实施固定流程
     * @param param
     * @return
     * @throws Exception
     */
    public String  core(JSONObject param)throws Exception{
        //封装xml头部
        StringBuffer sb = XmlUtils.getStartXMLFormat(param);

        sb.append(doHis(HisService.BASE_URL, param));

        //封装xml尾部
        sb.append(XmlUtils.getEndXMLFormat());

        return sb.toString();
    }

    /**
     * 可单独调用HIS方法
     * @param url
     * @param param
     * @return
     * @throws Exception
     */
    public String doHis(String url, JSONObject param)throws Exception{
        //设置his参数
        Map<String, Object> map = setHisInParams(param);

        //发送参数到his
        String res = his.invoke(url, map);

        //将his返回字符串转换为map解析
        JSONObject jsonObject = setOutParams(XmlUtils.xml2JSONWithAttr(res, false));

        //将map转为xml
        res = XmlUtils.mapToXmlNode(jsonObject);

        return res;
    }
    /**
     * 设置请求His的参数
     * @param inParam
     * @return HIS传参
     */
    public abstract Map<String, Object> setHisInParams(JSONObject inParam);

    /**
     * 解析HIS返回的参数，
     * 并封装到xml中
     * @param param HIS返回的数据
     * @return xml格式的字符串
     */
    public abstract JSONObject setOutParams(JSONObject param);
}
