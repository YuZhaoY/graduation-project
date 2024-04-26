package com.scu.stu.common.pay;

public enum PayType {
    INBOUND(1,"入库"),
    REFUND(2,"退供"),
    ;

    private int code;

    private String desc;

    public int getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    PayType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public static String getDescByCode(int code){
        if(code == INBOUND.code){
            return INBOUND.desc;
        } else {
            return REFUND.desc;
        }
    }
    public static int getCodeByDesc(String desc){
        if(desc.equals(INBOUND.desc)){
            return INBOUND.code;
        } else {
            return REFUND.code;
        }
    }
}
