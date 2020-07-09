package com.urop.server;

import com.google.gson.Gson;

import org.java_websocket.WebSocket;

public class Utils {
//    private static final GsonTaskConverter SINGLETON = new GsonTaskConverter();

//    private GsonTaskConverter(){};

    private static Gson gson = new Gson();

    public static Task json2task(String str) {
        return gson.fromJson(str, Task.class);
    }

    public static String task2json(Task t) {
        return gson.toJson(t);
    }

    public static String arr2json(int[] arr) {
        return gson.toJson(arr);
    }

    public static int[] json2arr(String str) {
        return gson.fromJson(str, int[].class);
    }

    public static String getAddress(WebSocket conn) {
        return conn.getRemoteSocketAddress().getAddress().getHostAddress();
    }

    public static void logAppend(String text) {

//        String curTime = SimpleDateFormat("[HH:mm:ss:SSS] ", Locale.getDefault()).format(Date())
        String curTime = "";
        System.out.println(curTime + text);
    }
}
