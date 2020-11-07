package com.lyl.plugin.model;

import java.util.ArrayList;
import java.util.List;

/**
 * tag 节点对应服务的controller
 * @author yunlong.liu
 * @date 2020-11-04 15:19:48
 */

public class TagNode {

    /**
     * 对应controller名称
     */
    private String name = null;


    /**
     * controller的描述
     */
    private String description = null;

    /***
     * 请求接口列表
     */
    private List<RequestNode> requestNodes=new ArrayList<>();



    public List<RequestNode> getRequestNodes() {
        return requestNodes;
    }

    public void setRequestNodes(List<RequestNode> requestNodes) {
        this.requestNodes = requestNodes;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
