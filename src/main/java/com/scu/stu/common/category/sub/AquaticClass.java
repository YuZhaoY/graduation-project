package com.scu.stu.common.category.sub;

public enum AquaticClass {
    FISH(1,"鱼"),
    SHRIMP(2,"虾"),
    CRAB(3,"蟹"),
    SEASHELL(4,"贝"),
    SEAWEED(5,"海藻"),
    OTHER(6,"其他"),
    ;

    private int code;

    private String desc;

    public int getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    AquaticClass(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public static String getDescByCode(int code){
        for(AquaticClass aquaticClass : AquaticClass.values()){
            if(aquaticClass.getCode() == code){
                return aquaticClass.getDesc();
            }
        }
        return null;
    }
    public static int getCodeByDesc(String desc){
        for(AquaticClass aquaticClass : AquaticClass.values()){
            if(aquaticClass.getDesc().equals(desc)){
                return aquaticClass.getCode();
            }
        }
        return 0;
    }
}
