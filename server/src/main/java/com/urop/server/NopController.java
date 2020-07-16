package com.urop.server;

import com.urop.common.Task;

import org.java_websocket.WebSocket;

import java.util.Map;

import static com.urop.server.Utils.getAddress;
import static com.urop.server.Utils.logAppend;

public class NopController implements TaskController {
    Dispatcher dispatcher;

    @Override
    public void run() {
        if (dispatcher == null) {
            logAppend("Dispatcher not set. Abort.");
            return;
        }

        final int LOOP_COUNT = 5;
        final int ARR_LENGTH = 1000000;

//        int[] subarr = Arrays.copyOfRange(arr, 0, ARR_LENGTH);
//        t.data = arr2json(subarr);
//        for (int i = 0; i < LOOP_COUNT; i++) {
//            int[] arr = new int[1];
//            Task t = new Task("NOP", toJson(arr), "1");
//            dispatcher.addPendingTask(t);
//        }
        dispatcher.blockUntilSomeNodeConnect(1);
//        logAppend("A node connected");

        long startTime = System.currentTimeMillis();

        dispatcher.blockUntilAllTasksFinish();

        long endTime = System.currentTimeMillis();
        logAppend("Done. Time: " + (endTime - startTime) + "ms");

        Map<WebSocket, Integer> timespent = dispatcher.getRealWorkingTime();
        timespent.forEach((conn, ttt) -> logAppend(getAddress(conn) + " spent " + ttt + "ms."));
    }

    @Override
    public void submitTask(WebSocket conn, Task t) {
//        if()
//        dispatcher.addPendingTask(t);
        dispatcher.commitTask(conn, t);

    }
}
