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
}
