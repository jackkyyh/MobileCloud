package com.urop.common;

public class Task {
    public String cmd;
    public byte[] data;
    public String meta; // id
    public int waitCount; // working time

    public Task(String cmd, String meta) {
        this.cmd = cmd;
        this.data = new byte[0];
        this.meta = meta;
        this.waitCount = 2;
    }

    public Task() {
        cmd = "EMPTY";
        data = null;
        meta = null;
        waitCount = 0;
    }

    public static Task Greeting(String msg) {
        return new Task("Message", msg);
    }
}
