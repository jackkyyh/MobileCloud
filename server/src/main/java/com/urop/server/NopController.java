package com.urop.server;

import org.java_websocket.WebSocket;

import java.util.Map;

import static com.urop.server.Utils.arr2json;
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
        for (int i = 0; i < LOOP_COUNT; i++) {
            int[] arr = new int[1];
            Task t = new Task("NOP", arr2json(arr), "1");
            dispatcher.addPendingTask(t);
        }
        for (int i = 0; i < LOOP_COUNT; i++) {
            int[] arr1 = new int[1000];
            Task t1 = new Task("NOP", arr2json(arr1), "1e3");
            dispatcher.addPendingTask(t1);
        }
        for (int i = 0; i < LOOP_COUNT; i++) {
            int[] arr2 = new int[100000];
            Task t2 = new Task("NOP", arr2json(arr2), "1e5");
            dispatcher.addPendingTask(t2);
        }
        for (int i = 0; i < LOOP_COUNT; i++) {
            int[] arr3 = new int[1000000];
            Task t3 = new Task("NOP", arr2json(arr3), "1e6");
            dispatcher.addPendingTask(t3);
        }
        try {
            synchronized (dispatcher) {
                if (!dispatcher.hasNode()) {
                    logAppend("Waiting for any nodes to connect...");
                    dispatcher.wait();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        logAppend("A node connected");

        long startTime = System.currentTimeMillis();

        dispatcher.blockUntilAllTasksFinish();

        long endTime = System.currentTimeMillis();
        logAppend("Done. Time: " + (endTime - startTime) + "ms");

        Map<WebSocket, Integer> timespent = dispatcher.getTimespent();
        timespent.forEach((conn, ttt) -> {
            logAppend(getAddress(conn) + " spent " + ttt + "ms.");
        });
    }

    @Override
    public void setDispatcher(Dispatcher dis) {
        dispatcher = dis;
    }

    @Override
    public void submitTask(WebSocket conn, Task t) {
//        if()
//        dispatcher.addPendingTask(t);
        dispatcher.commitTask(conn, t);

    }
}
