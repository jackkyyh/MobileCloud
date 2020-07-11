package com.urop.common;

public class Task {
    public String cmd;
    public String data;
    public String meta; // id
    public int waitCount = 2;

    public Task(String cmd, String data, String meta) {
        this.cmd = cmd;
        this.data = data;
        this.meta = meta;
    }

    public Task() {
        cmd = "";
        data = "";
        meta = "";
    }
}
