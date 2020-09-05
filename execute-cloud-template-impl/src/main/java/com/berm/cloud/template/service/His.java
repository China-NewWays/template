package com.berm.cloud.template.service;

import java.util.Map;

public interface His {
    /**
     * 请求his
     * 处理请求His的方式
     * @return xml/json
     */
    String invoke(String url, Map<String, Object> params);
}
