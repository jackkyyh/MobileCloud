package com.urop.server;

import com.urop.common.Task;

import org.java_websocket.WebSocket;

import static com.urop.common.Profiler.profiler;
import static com.urop.server.Server.getServer;
import static com.urop.server.Utils.logAppend;

public abstract class TaskController implements Runnable {

    final Dispatcher dispatcher;
    int WAIT_FOR;
    boolean doProfile;

    public TaskController() {
        WAIT_FOR = 2;
//        profile = false;
        dispatcher = getServer().getDispatcher();
    }


    @Override
    public void run() {

        dispatcher.blockUntilSomeNodeConnect(WAIT_FOR);

        logAppend("Starts...");

        long time = profiler.add("total", () -> {
            reallyRun();
            blockUntilAllTasksFinish();
        });
        logAppend("All done. Time: " + time + "ms");

        if (checkResult()) {
            logAppend("Check succeeded!");
        } else {
            logAppend("Check failed!");
        }

//        Map<WebSocket, Integer> timespent = dispatcher.getRealWorkingTime();
//        timespent.forEach((conn, t) ->
//                logAppend(getAddress(conn) + " spent " + t + "ms."));

        logAppend("Server profile:" + profiler.dump());
        dispatcher.broadcast(new Task("Profile"));
    }

    void blockUntilAllTasksFinish() {
        dispatcher.blockUntilAllTasksFinish();
    }

    abstract void reallyRun();

    abstract boolean checkResult();

    void commitTask(WebSocket conn, Task t) {
        dispatcher.commitTask(conn, t);
    }

    public void setWAIT_FOR(int W) {
        WAIT_FOR = W;
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
