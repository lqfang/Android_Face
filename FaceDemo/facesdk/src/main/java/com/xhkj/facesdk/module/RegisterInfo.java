package com.xhkj.facesdk.module;

/**
 * created by ThinkPad on 2019/11/26
 * Describe 人脸注册返回的信息
 */

public class RegisterInfo {

    private int code;
    private int index;

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

    @Override
    public String toString() {
        return "RegisterInfo{" +
                "code=" + code +
                ", index=" + index +
                '}';
    }
}
