package com.lyl.test.element;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yunlong.liu
 * @date 2020-11-04 15:16:56
 */

public class TemplateParamVO {

    private String title;

    private String description;

   private String host;

   private int port;

    private List<TagNode> tagList=new ArrayList<>();

    public List<TagNode> getTagList() {
        return tagList;
    }

    public void setTagList(List<TagNode> tagList) {
        this.tagList = tagList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
