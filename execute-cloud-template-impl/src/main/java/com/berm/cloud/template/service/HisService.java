package com.berm.cloud.template.service;

import com.berm.cloud.template.utils.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class HisService extends HttpUtils implements His{

    static final String BASE_URL = "";

    /**
    * 请求his
    * 处理请求His的方式
    * 参数可变
    * @return xml/json
    */
    @Override
    public String invoke(String url, Map<String, Object> params){
        // 逻辑处理代码


        return doPost(url, params);
    }
}
