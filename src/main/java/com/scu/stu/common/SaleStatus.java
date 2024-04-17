package com.scu.stu.common;

public enum SaleStatus {
    CREATE(1,"新建"),
    CONFIRM(2,"确认"),
    CANCEL(3,"取消"),
    BOOKING(4, "预约"),
    ;

    private int code;

    private String desc;

    public int getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    SaleStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public static String getDescByCode(int code){
        if(code == CREATE.code){
            return CREATE.desc;
        } else if(code == CONFIRM.code){
            return CONFIRM.desc;
        } else if(code == CANCEL.code){
            return CANCEL.desc;
        } else {
            return BOOKING.desc;
        }
    }
    public static int getCodeByDesc(String desc){
        if(desc.equals(CREATE.desc)){
            return CREATE.code;
        } else if(desc.equals(CONFIRM.desc)){
            return CONFIRM.code;
        } else if(desc.equals(CANCEL.desc)){
            return CANCEL.code;
        } else {
            return BOOKING.code;
        }
    }
}
