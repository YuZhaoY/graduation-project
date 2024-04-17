package com.scu.stu.common;

public enum BookingStatus {
    VALID(1,"有效"),
    INVALID(2,"无效"),
    SIGN(3,"签到"),
    ;

    private int code;

    private String desc;

    public int getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    BookingStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public static String getDescByCode(int code){
        if(code == VALID.code){
            return VALID.desc;
        } else if(code == INVALID.code){
            return INVALID.desc;
        } else {
            return SIGN.desc;
        }
    }
    public static int getCodeByDesc(String desc){
        if(desc.equals(VALID.desc)){
            return VALID.code;
        } else if(desc.equals(INVALID.desc)){
            return INVALID.code;
        } else {
            return SIGN.code;
        }
    }
}
