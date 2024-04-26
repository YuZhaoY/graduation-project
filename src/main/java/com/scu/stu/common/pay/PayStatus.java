package com.scu.stu.common.pay;

public enum PayStatus {
    CREATE(1,"未支付"),
    PAY(2,"已支付"),
    ;

    private int code;

    private String desc;

    public int getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    PayStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public static String getDescByCode(int code){
        if(code == CREATE.code){
            return CREATE.desc;
        } else {
            return PAY.desc;
        }
    }
    public static int getCodeByDesc(String desc){
        if(desc.equals(CREATE.desc)){
            return CREATE.code;
        } else {
            return PAY.code;
        }
    }
}
