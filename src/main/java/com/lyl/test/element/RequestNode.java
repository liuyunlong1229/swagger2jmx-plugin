package com.lyl.test.element;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yunlong.liu
 * @date 2020-11-04 15:20:13
 */

public class RequestNode {

    private List<String> tag;

    private String oprationName;

    private String  requestUrl;

    private List<ParamNode> queryParamNodes=new ArrayList<>();

    private List<ParamNode> headerParamNodes=new ArrayList<>();

    private String requetsBody =null;

    private String httpMethod;


    public String getOprationName() {
        return oprationName;
    }

    public void setOprationName(String oprationName) {
        this.oprationName = oprationName;
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

    public String getRequetsBody() {
        return requetsBody;
    }

    public void setRequetsBody(String requetsBody) {
        this.requetsBody = requetsBody;
    }
}
