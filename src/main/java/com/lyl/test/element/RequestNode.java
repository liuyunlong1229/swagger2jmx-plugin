package com.lyl.test.element;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yunlong.liu
 * @date 2020-11-04 15:20:13
 */

public class RequestNode {

    private List<String> tag;

    private String operationName;

    private String  requestUrl;

    private List<ParamNode> queryParamNodes=new ArrayList<>();

    private List<ParamNode> headerParamNodes=new ArrayList<>();

    private String requestBody =null;

    private String httpMethod;

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }



    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public List<ParamNode> getQueryParamNodes() {
        return queryParamNodes;
    }

    public void setQueryParamNodes(List<ParamNode> queryParamNodes) {
        this.queryParamNodes = queryParamNodes;
    }

    public List<ParamNode> getHeaderParamNodes() {
        return headerParamNodes;
    }

    public void setHeaderParamNodes(List<ParamNode> headerParamNodes) {
        this.headerParamNodes = headerParamNodes;
    }

}
