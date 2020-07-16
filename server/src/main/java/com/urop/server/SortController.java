package com.urop.server;


import com.urop.common.Task;

import org.java_websocket.WebSocket;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import static com.urop.common.UtilsKt.arr2json;
import static com.urop.common.UtilsKt.json2arr;
import static com.urop.server.Utils.getAddress;
import static com.urop.server.Utils.logAppend;

public class SortController implements TaskController {

    final int MIN_SEG_LENGTH = 5000000;
    final int ARR_LENGTH = 5000000;
    final int WAIT_FOR = 1;
    public int[] arr;
    Dispatcher dispatcher;
    volatile Map<String, Collection<Task>> blockedTasks;


    SortController() {
        blockedTasks = new HashMap<>();

        Random r = new Random();
        arr = new int[ARR_LENGTH];
        for (int i = 0; i < ARR_LENGTH; i++) {
            arr[i] = r.nextInt(10000);
        }
//        dispatcher = dis;
//        dispatcher = new Dispatcher(this);
    }

    public void sort(int start, int end) {
        if (end - start <= MIN_SEG_LENGTH) {
            Task t = createTask("QSRT", start, end);
            addBlockedTask(null, t);
            return;
        }

        int middle = (start + end) / 2;
        sort(start, middle);
        sort(middle, end);


        String dep1 = arr2json(new int[]{start, middle});
        String dep2 = arr2json(new int[]{middle, end});
        Task t = createTask("MSRT", start, end);
        addBlockedTask(dep1, t);
        addBlockedTask(dep2, t);
    }

//    public void addBlockedTask()

    public synchronized void addBlockedTask(String depend, Task t) {
//        Collection set = blockedTasks.getOrDefault(depend, new HashSet<>());
        if (depend == null) {
            fillTaskData(t);
            dispatcher.addPendingTask(t);
            return;
        }

//        fillTaskData(t);
        Collection<Task> set = blockedTasks.get(depend);
        if (set == null) {
            set = new HashSet<>();
            set.add(t);
            blockedTasks.put(depend, set);
        } else {
            set.add(t);
        }
    }

    public Task createTask(String header, int start, int end) {
        int[] meta = {start, end};
        String jsonMeta = arr2json(meta);
        return new Task(header, null, jsonMeta);
    }

    @Override
    public synchronized void submitTask(WebSocket conn, Task t) {

//        logAppend("result: " + t.meta);
        int[] res = json2arr(t.data);
        int index = json2arr(t.meta)[0];
//        logAppend("index = "+index);
        int i = 0;
        while (i < res.length) {
            arr[index++] = res[i++];
        }
//        logAppend("new arr: " + arr2json(arr));
        if (dispatcher.commitTask(conn, t)) {
            Collection<Task> set = blockedTasks.get(t.meta);
            if (set != null) {
                for (Task task : set) {
                    task.waitCount--;
                    if (task.waitCount == 0) {
                        set.remove(task);

                        fillTaskData(task);
                        dispatcher.addPendingTask(task);
                    }
                }
                blockedTasks.remove(t.meta);
            }
            if (blockedTasks.isEmpty()) {
                this.notifyAll();
            }
//            logAppend("commit success");
        } else {
            logAppend("commit failed");
        }
//        dispatcher.loopDispatch();
    }

    public void fillTaskData(Task t) {

        int[] index = json2arr(t.meta);
        int[] subarr = Arrays.copyOfRange(arr, index[0], index[1]);
        t.data = arr2json(subarr);
    }

    @Override
    public void run() {
        if (dispatcher == null) {
            logAppend("Dispatcher not set. Abort.");
            return;
        }

        blockUntilSomeNodesConnect(WAIT_FOR);
//        logAppend("A node connected");

        long startTime = System.currentTimeMillis();

        sort(0, arr.length);
//        logAppend("All tasks created. Executing...");
        blockUntilAllTasksFinish();

//        logAppend("All done!");

        long endTime = System.currentTimeMillis();
        logAppend("Done. Time: " + (endTime - startTime) + "ms");


        if (checkSortResult()) {
            logAppend("Check succeeded!");
        } else {
            logAppend("Check failed!");
            logAppend(arr2json(arr));
        }

        Map<WebSocket, Integer> timespent = dispatcher.getTimespent();
        timespent.forEach((conn, t) -> {
            logAppend(getAddress(conn) + " spent " + t + "ms.");
        });
    }


    public void blockUntilAllTasksFinish() {

        try {
            synchronized (this) {
                while (!blockedTasks.isEmpty()) {
                    this.wait();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dispatcher.blockUntilAllTasksFinish();
    }

    public void blockUntilSomeNodesConnect(int num) {
        try {
            synchronized (dispatcher) {
                while (dispatcher.numOfNode() < num) {
                    logAppend("Waiting for more nodes to connect...");
                    dispatcher.wait();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        while(!dispatcher.hasNode()){};
    }

    public boolean checkSortResult() {

        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] > arr[i + 1]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void setDispatcher(Dispatcher dis) {
        dispatcher = dis;
    }

}
