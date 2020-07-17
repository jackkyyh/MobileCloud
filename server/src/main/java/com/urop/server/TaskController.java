package com.urop.server;

import com.urop.common.Task;

import org.java_websocket.WebSocket;

public interface TaskController extends Runnable {

    void run();

    void submitTask(WebSocket conn, Task t);
//    public void blockUntilFinish
}
