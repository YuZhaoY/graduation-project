package com.scu.stu.common;

public enum AgreementStatus {
    CREATE(1,"新建"),
    SIGN(2,"签署"),
    CANCEL(3,"取消"),
    ;

    private int code;

    private String desc;

    public int getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    AgreementStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public static String getDescByCode(int code){
        if(code == CREATE.code){
            return CREATE.desc;
        } else if(code == SIGN.code){
            return SIGN.desc;
        } else {
            return CANCEL.desc;
        }
    }
    public static int getCodeByDesc(String desc){
        if(desc.equals(CREATE.desc)){
            return CREATE.code;
        } else if(desc.equals(SIGN.desc)){
            return SIGN.code;
        } else {
            return CANCEL.code;
        }
    }
}
