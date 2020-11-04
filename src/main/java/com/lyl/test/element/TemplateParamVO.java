package com.lyl.test.element;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yunlong.liu
 * @date 2020-11-04 15:16:56
 */

public class TemplateParamVO {

    private String serviceName;

    private List<TagNode> tagList=new ArrayList<>();


    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public List<TagNode> getTagList() {
        return tagList;
    }

    public void setTagList(List<TagNode> tagList) {
        this.tagList = tagList;
    }
}
