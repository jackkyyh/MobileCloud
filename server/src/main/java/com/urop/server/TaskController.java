package com.urop.server;

import com.urop.common.Task;

import org.java_websocket.WebSocket;

import java.util.Map;

import static com.urop.server.Utils.getAddress;
import static com.urop.server.Utils.logAppend;

public abstract class TaskController implements Runnable {

    final Dispatcher dispatcher;
    final int WAIT_FOR;

    public TaskController() {

        WAIT_FOR = 3;
        dispatcher = Server.getServer().getDispatcher();
    }

    @Override
    public void run() {

        dispatcher.blockUntilSomeNodeConnect(WAIT_FOR);

        logAppend("Starts...");
        long startTime = System.currentTimeMillis();

        reallyRun();

        blockUntilAllTasksFinish();

        long endTime = System.currentTimeMillis();
        logAppend("Done. Time: " + (endTime - startTime) + "ms");


        if (checkResult()) {
            logAppend("Check succeeded!");
        } else {
            logAppend("Check failed!");
        }

        Map<WebSocket, Integer> timespent = dispatcher.getRealWorkingTime();
        timespent.forEach((conn, t) ->
                logAppend(getAddress(conn) + " spent " + t + "ms."));
    }

    void blockUntilAllTasksFinish() {
        dispatcher.blockUntilAllTasksFinish();
    }

    abstract void reallyRun();

    abstract boolean checkResult();

    void commitTask(WebSocket conn, Task t) {
        dispatcher.commitTask(conn, t);
    }

    void safeWait() {
        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
//    public void blockUntilFinish
}
