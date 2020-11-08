package com.lyl.plugin.model;

/**
 *
 * 请求的参数节点
 * @author yunlong.liu
 * @date 2020-11-04 15:22:02
 */

public class ParamNode {

    /**
     * 参数名称
     */
    private String paramName;

    /**
     * 参数值
     */
    private String paramValue;

    /***
     * 参数类型
     */
    private String  paramType;

    /**
     * 参数描述
     */
    private String description;


    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
