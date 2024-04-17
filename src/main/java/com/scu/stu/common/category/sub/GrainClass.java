package com.scu.stu.common.category.sub;

public enum GrainClass {
    RICE(1,"稻谷"),
    WHEAT(2,"小麦"),
    CORN(3,"玉米"),
    SORGHUM(4,"高粱"),
    LEGUME(5,"豆类"),
    YAMS(6,"甘薯"),
    SESAME(7,"芝麻"),
    SEEDS(8,"籽类"),
    OTHER(9,"其他"),
    ;

    private int code;

    private String desc;

    public int getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    GrainClass(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public static String getDescByCode(int code){
        for(GrainClass mainClass : GrainClass.values()){
            if(mainClass.getCode() == code){
                return mainClass.getDesc();
            }
        }
        return null;
    }
    public static int getCodeByDesc(String desc){
        for(GrainClass mainClass : GrainClass.values()){
            if(mainClass.getDesc().equals(desc)){
                return mainClass.getCode();
            }
        }
        return 0;
    }
}
