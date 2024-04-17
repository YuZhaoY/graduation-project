package com.scu.stu.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class RandomUtils {

    public static int generateRandomNumber() {
        Random random = new Random();
        return random.nextInt(9000) + 1000;
    }
    public static String generateID(){
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        String date = df.format(new Date()).replaceAll(":|\\s","");
        return date+generateRandomNumber();
    }
}
