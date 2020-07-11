package com.urop.server;

import org.java_websocket.WebSocket;

public class Utils {
//    private static final GsonTaskConverter SINGLETON = new GsonTaskConverter();

//    private GsonTaskConverter(){};


    public static String getAddress(WebSocket conn) {
        return conn.getRemoteSocketAddress().getAddress().getHostAddress();
    }

    public static void logAppend(String text) {

//        String curTime = SimpleDateFormat("[HH:mm:ss:SSS] ", Locale.getDefault()).format(Date())
        String curTime = "";
        System.out.println(curTime + text);
    }
}
