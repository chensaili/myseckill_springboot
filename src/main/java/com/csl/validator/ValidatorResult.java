package com.csl.validator;


import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
//要返回的校验结果
public class ValidatorResult {
    //校验结果是否有错
    private boolean hasError=false;
    //存放错误信息的map
    private Map<String,String>errorMsgMap=new HashMap<>();

    public boolean isHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public Map<String, String> getErrorMsgMap() {
        return errorMsgMap;
    }

    public void setErrorMsgMap(Map<String, String> errorMsgMap) {
        this.errorMsgMap = errorMsgMap;
    }

    //创建一个通用的通过格式化字符串信息获取错误结果的msg方法
    public String getErrMsg(){
        return StringUtils.join(errorMsgMap.values().toArray(),",");
    }
}
