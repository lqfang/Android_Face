package com.xhkj.facesdk.module;

/**
 * created by admin on 2019/12/17
 * Describe
 */
public class IndexCount {
    private int code;
    private int indexCount;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getIndexCount() {
        return indexCount;
    }

    public void setIndexCount(int indexCount) {
        this.indexCount = indexCount;
    }

    @Override
    public String toString() {
        return "IndexCount{" +
                "code=" + code +
                ", indexCount=" + indexCount +
                '}';
    }
}
