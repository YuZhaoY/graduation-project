package com.scu.stu.utils;

import com.scu.stu.common.category.MainClass;
import com.scu.stu.common.category.sub.*;

public class CategoryUtils {

    public static String getCategoryCode(String mainDesc, String subDesc){
        int mainCode = MainClass.getCodeByDesc(mainDesc);
        if(mainCode == MainClass.OTHER.getCode()){
            return ""+mainCode;
        }
        int subCode = getSubClassCode(mainCode,subDesc);
        return ""+mainCode+subCode;
    }

    public static String getCategoryDesc(String category){
        int mainCode = category.charAt(0)-'0';
        if(mainCode == MainClass.OTHER.getCode()){
            return MainClass.OTHER.getDesc();
        }
        int subCode = category.charAt(1)-'0';
        return getSubClassDesc(mainCode, subCode);
    }

    public static int getSubClassCode(int mainCode, String subDesc){
        int result = 0;
        switch (mainCode){
            case 1:
                result = FruitClass.getCodeByDesc(subDesc);
                break;
            case 2:
                result = VegetableClass.getCodeByDesc(subDesc);
                break;
            case 3:
                result = GrainClass.getCodeByDesc(subDesc);
                break;
            case 4:
                result = PoultryClass.getCodeByDesc(subDesc);
                break;
            case 5:
                result = AquaticClass.getCodeByDesc(subDesc);
                break;
            case 6:
                result = EggsClass.getCodeByDesc(subDesc);
                break;
            default:
                break;
        }
        return result;
    }
    public static String getSubClassDesc(int mainCode, int subCode){
        String result = null;
        switch (mainCode){
            case 1:
                result = FruitClass.getDescByCode(subCode);
                break;
            case 2:
                result = VegetableClass.getDescByCode(subCode);
                break;
            case 3:
                result = GrainClass.getDescByCode(subCode);
                break;
            case 4:
                result = PoultryClass.getDescByCode(subCode);
                break;
            case 5:
                result = AquaticClass.getDescByCode(subCode);
                break;
            case 6:
                result = EggsClass.getDescByCode(subCode);
                break;
            default:
                break;
        }
        return result;
    }
}
