package com.urop.server;

import com.urop.common.Task;

import org.java_websocket.WebSocket;

public class NopController extends TaskController {

    @Override
    void reallyRun() {
        Task t = new Task();
        dispatcher.addPendingTask(t);
    }

    @Override
    boolean checkResult() {
        return true;
    }

    @Override
    public void commitTask(WebSocket conn, Task t) {

        dispatcher.commitTask(conn, t);
    }
}
