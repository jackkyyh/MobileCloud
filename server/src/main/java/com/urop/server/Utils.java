package com.urop.server;

import org.java_websocket.WebSocket;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
//    private static final GsonTaskConverter SINGLETON = new GsonTaskConverter();

//    private GsonTaskConverter(){};


    public static String getAddress(WebSocket conn) {
        return conn.getRemoteSocketAddress().getAddress().getHostAddress();
    }

    public static void logAppend(String text) {

        String curTime = new SimpleDateFormat("[HH:mm:ss:SSS] ", Locale.getDefault()).format(new Date());
        System.out.println(curTime + text);
    }
}
