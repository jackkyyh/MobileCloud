package com.urop.common;

public class Task {
    static int globalID = 0;
    final public String cmd;
    final public String id; // id, unmodifiable
    public int[] iArrData;
    public byte[] bArrData;
    public int waitCount; // working time


    public Task(String cmd, String id) {
        this.cmd = cmd;
        this.id = id;
    }

    public Task(String cmd) {
        this(cmd, Integer.toString(globalID++));
    }

    public String strData; // explain data

    public Task() {
        this("NOP");
    }

    public static Task Message(String message) {
        return new Task("Message", message);
    }
}
