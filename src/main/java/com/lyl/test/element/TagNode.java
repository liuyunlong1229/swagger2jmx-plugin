package com.lyl.test.element;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yunlong.liu
 * @date 2020-11-04 15:19:48
 */

public class TagNode {

    private String name = null;

    private String description = null;

    public List<RequestNode> getRequestNodes() {
        return requestNodes;
    }

    public void setRequestNodes(List<RequestNode> requestNodes) {
        this.requestNodes = requestNodes;
    }

    private List<RequestNode> requestNodes=new ArrayList<>();

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
