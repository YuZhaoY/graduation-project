package com.scu.stu.common.category;

public enum MainClass {
    FRUIT(1,"水果"),
    VEGETABLE(2,"蔬菜"),
    GRAIN(3,"粮油"),
    POULTRY(4,"禽畜"),
    AQUATIC(5,"水产"),
    EGGS(6,"蛋类"),
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

    MainClass(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public static String getDescByCode(int code){
        for(MainClass mainClass : MainClass.values()){
            if(mainClass.getCode() == code){
                return mainClass.getDesc();
            }
        }
        return null;
    }
    public static int getCodeByDesc(String desc){
        for(MainClass mainClass : MainClass.values()){
            if(mainClass.getDesc().equals(desc)){
                return mainClass.getCode();
            }
        }
        return 0;
    }
}
