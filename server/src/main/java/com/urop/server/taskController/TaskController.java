package com.urop.server.taskController;

//import com.esotericsoftware.kryonet.Connection;

import com.urop.common.Connection;
import com.urop.common.Profile;
import com.urop.common.Task;
import com.urop.server.Dispatcher;

import static com.urop.common.Profile.profile;
import static com.urop.server.Server.logAppend;
import static com.urop.server.Server.server;

public abstract class TaskController implements Runnable {

    final Dispatcher dispatcher;
    int waitFor;
    boolean printProfile;

    public TaskController() {
        waitFor = 2;
        printProfile = true;
        dispatcher = server.getDispatcher();
    }


    @Override
    public void run() {

        dispatcher.blockUntilSomeNodeConnect(waitFor);

        logAppend("Starts...");

        long time = profile.add("total", () -> {
            reallyRun();
            blockUntilAllTasksFinish();
        });
        logAppend("All done. Time: " + time + "ms");

        if (checkResult()) {
            logAppend("Check succeeded!");
        } else {
            logAppend("Check failed!");
        }

        if (printProfile) {
            logAppend("Server profile:" + profile);
            dispatcher.broadcast(new Profile());
        }
    }

    void blockUntilAllTasksFinish() {
        dispatcher.blockUntilAllTasksFinish();
    }

    abstract void reallyRun();

    abstract boolean checkResult();

    public void commitTask(Connection conn, Task t) {
        dispatcher.commitTask(conn, t);
    }

    public void setWaitFor(int w) {
        waitFor = w;
    }

    public void setPrintProfile(boolean p) {
        printProfile = p;
    }

    void safeWait() {
        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
