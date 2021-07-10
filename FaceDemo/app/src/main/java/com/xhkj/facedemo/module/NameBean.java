package com.xhkj.facedemo.module;

/**
 * created by admin on 2019/11/27
 * Describe
 */
public class NameBean {

    private int index;
    private String name;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "NameBean{" +
                "index=" + index +
                ", name='" + name + '\'' +
                '}';
    }
}
