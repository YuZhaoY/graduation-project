package com.scu.stu.common.category.sub;

public enum  EggsClass {
    EGG(1,"鸡蛋"),
    DUCK(2,"鸭蛋"),
    GOOSE(3,"鹅蛋"),
    QUAIL(4,"鹌鹑蛋"),
    PIGEON(5,"鸽子蛋"),
    OTHER(6,"其他");

    private int code;

    private String desc;

    public int getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    EggsClass(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public static String getDescByCode(int code){
        for(EggsClass eggClass : EggsClass.values()){
            if(eggClass.getCode() == code){
                return eggClass.getDesc();
            }
        }
        return null;
    }
    public static int getCodeByDesc(String desc){
        for(EggsClass eggClass : EggsClass.values()){
            if(eggClass.getDesc().equals(desc)){
                return eggClass.getCode();
            }
        }
        return 0;
    }
}
