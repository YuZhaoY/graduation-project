package com.scu.stu.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtils {

    private static final String format = "yyyy-MM-dd";

    public static Date parse(String date) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(format);
        Date parse = df.parse(date);
        return parse;
    }

    public static String format(Date date){
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    public static String format(Date date,String format){
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    public static boolean isThisTime(Date time, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String param = sdf.format(time);//参数时间
        String now = sdf.format(new Date());//当前时间
        if (param.equals(now)) {
            return true;
        }
        return false;
    }

    public static boolean isLastYear(Date time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String param = sdf.format(time);//参数时间
        String now = sdf.format(new Date());//当前时间
        param = String.valueOf(Integer.parseInt(param) + 1);
        if (param.equals(now)) {
            return true;
        }
        return false;
    }

    public static Integer getMonth(Date time) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        return Integer.parseInt(sdf.format(time));
    }
}
