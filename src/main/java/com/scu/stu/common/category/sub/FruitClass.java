package com.scu.stu.common.category.sub;

public enum FruitClass {
    APPLE(1,"苹果"),
    PEACH(2,"桃子"),
    CITRUS(3,"柑橘"),
    PEAR(4,"梨"),
    STRAWBERRY(5,"草莓"),
    SUGARCANE(6,"甘蔗"),
    OTHER(7,"其他"),
    ;

    private int code;

    private String desc;

    public int getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    FruitClass(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public static String getDescByCode(int code){
        for(FruitClass mainClass : FruitClass.values()){
            if(mainClass.getCode() == code){
                return mainClass.getDesc();
            }
        }
        return null;
    }
    public static int getCodeByDesc(String desc){
        for(FruitClass mainClass : FruitClass.values()){
            if(mainClass.getDesc().equals(desc)){
                return mainClass.getCode();
            }
        }
        return 0;
    }
}
