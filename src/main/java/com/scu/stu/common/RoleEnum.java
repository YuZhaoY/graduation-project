package com.scu.stu.common;

public enum RoleEnum {

    ADMIN(1,"admin"),
    EDITOR(2,"editor"),
    ;

    private int role;

    private String desc;

    public int getRole() {
        return this.role;
    }

    public String getDesc() {
        return this.desc;
    }

    RoleEnum(int role, String desc) {
        this.role = role;
        this.desc = desc;
    }

    public static String getDescByRole(int role){
        if(role==ADMIN.role){
            return ADMIN.desc;
        } else{
            return EDITOR.desc;
        }
    }

    public static int getRoleByDesc(String desc){
        if(desc.equals(ADMIN.desc)){
            return ADMIN.role;
        } else{
            return EDITOR.role;
        }
    }
}
