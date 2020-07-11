package com.urop.common;

public class Task {
    public String cmd = "";
    public String data = "";
    public byte[] bdata = new byte[0];
    public String meta = ""; // id
    public byte[] bmeta = new byte[0];
    public int waitCount = 2;

    public Task(String cmd, String data, String meta) {
        this.cmd = cmd;
        this.data = data;
        this.meta = meta;
    }

    public Task(String cmd, byte[] bdata, byte[] bmeta) {
        this.cmd = cmd;
        this.bdata = bdata;
        this.bmeta = bmeta;
    }

    public Task() {
    }
}
