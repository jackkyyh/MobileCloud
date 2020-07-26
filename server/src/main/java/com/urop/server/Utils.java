package com.urop.server;

import com.esotericsoftware.kryonet.Connection;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
//    private static final GsonTaskConverter SINGLETON = new GsonTaskConverter();

//    private GsonTaskConverter(){};


    public static String getAddress(Connection conn) {
        return conn.getRemoteAddressTCP().getHostName();
    }

    public static void logAppend(String text) {

        String curTime = new SimpleDateFormat("[HH:mm:ss:SSS] ", Locale.getDefault()).format(new Date());
        System.out.println(curTime + text);
    }

//    public static long measureTimeMillis(Runnable r){
//        long startTime = System.currentTimeMillis();
//        r.run();
//        long endTime = System.currentTimeMillis();
//        return endTime-startTime;
//    }
}
