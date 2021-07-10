package com.xhkj.facesdk.module;

/**
 * created by ThinkPad on 2019/11/26
 * Describe 查询返回的信息（照片的索引和相似度）
 */

public class QueryInfo {

    private int code;
    private int index;
    private float score;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "QueryInfo{" +
                "code=" + code +
                ", index=" + index +
                ", score=" + score +
                '}';
    }
}
