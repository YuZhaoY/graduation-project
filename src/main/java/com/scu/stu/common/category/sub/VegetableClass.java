package com.scu.stu.common.category.sub;

public enum VegetableClass {
    TURNIP(1,"萝卜"),
    GREENS(2,"青菜"),
    POTATO(3,"土豆"),
    TOMATO(4,"西红柿"),
    OTHER(5,"其他"),
    ;

    private int code;

    private String desc;

    public int getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    VegetableClass(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public static String getDescByCode(int code){
        for(VegetableClass mainClass : VegetableClass.values()){
            if(mainClass.getCode() == code){
                return mainClass.getDesc();
            }
        }
        return null;
    }
    public static int getCodeByDesc(String desc){
        for(VegetableClass mainClass : VegetableClass.values()){
            if(mainClass.getDesc().equals(desc)){
                return mainClass.getCode();
            }
        }
        return 0;
    }
}
