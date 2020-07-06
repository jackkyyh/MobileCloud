package com.urop.server;

import org.java_websocket.WebSocket;

public interface TaskController extends Runnable {

    void run();

    void setDispatcher(Dispatcher dis);

    void submitTask(WebSocket conn, Task t);
//    public void blockUntilFinish
}
