package com.scu.stu.common.category.sub;

public enum PoultryClass {
    CHICKEN(1,"鸡肉"),
    DUCK(2,"鸭肉"),
    BEEF(3,"牛肉"),
    PORK(4,"猪肉"),
    MUTTON(5,"羊肉"),
    DONKEY(6,"驴肉"),
    GOOSE(7,"鹅肉"),
    OTHER(8,"其他"),
    ;

    private int code;

    private String desc;

    public int getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    PoultryClass(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public static String getDescByCode(int code){
        for(PoultryClass poultryClass : PoultryClass.values()){
            if(poultryClass.getCode() == code){
                return poultryClass.getDesc();
            }
        }
        return null;
    }
    public static int getCodeByDesc(String desc){
        for(PoultryClass poultryClass : PoultryClass.values()){
            if(poultryClass.getDesc().equals(desc)){
                return poultryClass.getCode();
            }
        }
        return 0;
    }
}
