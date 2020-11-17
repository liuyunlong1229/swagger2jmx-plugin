package com.lyl.plugin.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 请求节点对应每个http接口
 * @author yunlong.liu
 * @date 2020-11-04 15:20:13
 */

public class RequestNode {

    /**
     * 接口上面的tag列表
     */
    private List<String> tag;

    /**
     * 操作名称，swagger声明的@ApiOperation的value值
     */
    private String operationName;


    /**
     * 操作id,由接口名称加请求方式构成，如：getByNameUsingPOST
     */
    private String operationId;


    /**
     * 接口请求路径
     */
    private String  requestUrl;


    /**
     * 接口的query类型参数名称列表
     */
    private List<ParamNode> queryParamNodes=new ArrayList<>();

    /**
     * 接口的header类型参数名称列表
     */
    private List<ParamNode> headerParamNodes=new ArrayList<>();

    /**
     * 接口的body类型参数结构
     */
    private String requestBody =null;


    /**
     * 接口排序号
     */
    private int sortNo;

    public String getHttpMethod() {
        return httpMethod;
    }

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

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public int getSortNo() {
        return sortNo;
    }

    public void setSortNo(int sortNo) {
        this.sortNo = sortNo;
    }
}
