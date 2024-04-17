package com.scu.stu.common;

public enum CommonStatusEnum {

    SUCCESS(200,"成功"),
    ERROR(400,"失败"),
    ;

    private int code;

    private String desc;

    public int getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    CommonStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
