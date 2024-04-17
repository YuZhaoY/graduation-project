package com.scu.stu.common;

public enum RoleEnum {

    ADMIN(1,"admin"),
    EDITOR(2,"editor"),
    SUPERADMIN(3,"super-admin"),
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
        } else if(role == EDITOR.role){
            return EDITOR.desc;
        } else{
            return SUPERADMIN.desc;
        }
    }

    public static int getRoleByDesc(String desc){
        if(desc.equals(ADMIN.desc)){
            return ADMIN.role;
        } else if(desc.equals(EDITOR.desc)){
            return EDITOR.role;
        } else{
            return SUPERADMIN.role;
        }
    }
}
