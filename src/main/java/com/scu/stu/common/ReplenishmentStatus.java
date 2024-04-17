package com.scu.stu.common;

public enum ReplenishmentStatus {
    VALID(1,"有效"),
    INVALID(2,"无效"),
    CANCEL(3,"取消"),
    CREATE(4,"新建"),
    ;

    private int code;

    private String desc;

    public int getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    ReplenishmentStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public static String getDescByCode(int code){
        if(code == VALID.code){
            return VALID.desc;
        } else if(code == INVALID.code){
            return INVALID.desc;
        } else if(code == CREATE.code){
            return CREATE.desc;
        } else {
            return CANCEL.desc;
        }
    }
    public static int getCodeByDesc(String desc){
        if(desc.equals(VALID.desc)){
            return VALID.code;
        } else if(desc.equals(INVALID.desc)){
            return INVALID.code;
        } else if(desc.equals(CREATE.desc)){
            return CREATE.code;
        } else {
            return CANCEL.code;
        }
    }
}
