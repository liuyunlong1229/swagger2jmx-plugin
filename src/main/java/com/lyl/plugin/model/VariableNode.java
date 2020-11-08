package com.lyl.plugin.model;

/**
 * 全局变量节点
 */
public class VariableNode {

    /**
     * 变量名称
     */
    private String name;

    /**
     * 变量值
     */
    private String value;

    /**
     * 变量描述
     */
    private String description;

    public VariableNode(String name, String value, String description) {
        this.name = name;
        this.value = value;
        this.description = description;
    }

    public VariableNode(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
