package com.urop.common;

public class Task {
    public String cmd;
    public byte[] data;
    static int globalID = 0;
    public String meta;
    public int waitCount; // working time
    public String id; // id, unmodifiable

    public Task(String cmd, String id) {
        this.cmd = cmd;
        this.id = id;
    }

    public Task(String cmd) {
        this(cmd, Integer.toString(globalID++));
    }

    public Task() {
        cmd = "NOP";
    }

    public static Task Greeting(String msg) {
        return new Task("Message", msg);
    }
}
