package com.urop.server.taskController;

//import com.esotericsoftware.kryonet.Connection;

import com.urop.common.Connection;
import com.urop.common.Task;

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
    public void commitTask(Connection conn, Task t) {

        dispatcher.commitTask(conn, t);
    }
}
