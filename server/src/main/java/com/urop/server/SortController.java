package com.urop.server;

import com.urop.common.Task;

import org.java_websocket.WebSocket;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import static com.urop.common.UtilsKt.toBAarr;
import static com.urop.common.UtilsKt.toIArr;
import static com.urop.common.UtilsKt.toJson;
import static com.urop.server.Utils.getAddress;
import static com.urop.server.Utils.logAppend;


public class SortController implements TaskController {

    final int ARR_LENGTH = 1000000;
    final int MIN_SEG_LENGTH = 100000;
    public int[] arr;

    final int WAIT_FOR = 1;

    final Dispatcher dispatcher;
    volatile Map<String, Collection<Task>> blockedTasks;


    SortController() {

        blockedTasks = new HashMap<>();

        Random r = new Random();
        arr = new int[ARR_LENGTH];
        for (int i = 0; i < ARR_LENGTH; i++) {
            arr[i] = r.nextInt(100);
        }

//        arr = new int[]{4,1,2,5,9,8,7,7,2};
//        ARR_LENGTH = arr.length;
//        MIN_SEG_LENGTH = 2;

        dispatcher = Server.getServer().getDispatcher();
    }

    void sort(int start, int end) {
        if (end - start <= MIN_SEG_LENGTH) {
            Task t = createEmptyTask("QSRT", start, end);
            addBlockedTask(null, t);
            return;
        }

        int middle = (start + end) / 2;
        sort(start, middle);
        sort(middle, end);


//        while(!dispatcher.isTaskFinished(dep1) || !dispatcher.isTaskFinished(dep2)){}
        Task t = createEmptyTask("MSRT", start, end);
//        String dep1 = createHeader()
        String dep1 = encodeMeta(start, middle);
        String dep2 = encodeMeta(middle, end);
        addBlockedTask(new String[]{dep1, dep2}, t);
//        addBlockedTask(dep2, t);
    }


    synchronized void addBlockedTask(String[] depend, Task t) {
        if (depend == null) {
            fillTaskData(t);
            dispatcher.addPendingTask(t);
            return;
        }

        for (String dep : depend) {
            if (!dispatcher.isTaskFinished(dep)) {
                Collection<Task> set = blockedTasks.get(dep);
                if (set == null) {
                    set = new HashSet<>();
                    set.add(t);
                    blockedTasks.put(dep, set);
//            logAppend("blocked task created");
                } else {
                    set.add(t);
                }
                t.waitCount++;
            }
        }

        if (t.waitCount == 0) {
            fillTaskData(t);
            dispatcher.addPendingTask(t);
        }

//        logAppend("[" + t.meta + "] depends on [" + depend + "]");
    }


    Task createEmptyTask(String header, int start, int end) {
        return new Task(header, encodeMeta(start, end));
    }

    String encodeMeta(int start, int end) {
        return start + "," + end;
    }

    int[] decodeMeta(String meta) {
        String[] strArr = meta.split(",");
        return new int[]{Integer.parseInt(strArr[0]), Integer.parseInt(strArr[1])};
    }

    @Override
    public synchronized void submitTask(WebSocket conn, Task t) {

        logAppend("receive: [" + t.meta + "]");
        int[] res = toIArr(t.data);
        int index = decodeMeta(t.meta)[0];
//        logAppend("index = "+index);
        int i = 0;
        while (i < res.length) {
            arr[index++] = res[i++];
        }
//        logAppend("new arr: " + toJson(arr));
        if (dispatcher.commitTask(conn, t)) {
            Collection<Task> set = blockedTasks.get(t.meta);
            if (set != null) {
                for (Task task : set) {
                    task.waitCount--;
//                    logAppend("[" + task.meta + "] block -1");
                    if (task.waitCount == 0) {
                        set.remove(task);
//                        logAppend("[" + task.meta + "] unblocked");
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

    void fillTaskData(Task t) {

        int[] index = decodeMeta(t.meta);
        int[] subarr = Arrays.copyOfRange(arr, index[0], index[1]);
        t.data = toBAarr(subarr);
    }

    @Override
    public void run() {

        dispatcher.blockUntilSomeNodeConnect(WAIT_FOR);

        logAppend("Starts...");
        long startTime = System.currentTimeMillis();

        sort(0, arr.length);

        blockUntilAllTasksFinish();

        long endTime = System.currentTimeMillis();
        logAppend("Done. Time: " + (endTime - startTime) + "ms");


        if (checkSortResult()) {
            logAppend("Check succeeded!");
        } else {
            logAppend("Check failed!");
            logAppend(toJson(arr));
        }

        Map<WebSocket, Integer> timespent = dispatcher.getRealWorkingTime();
        timespent.forEach((conn, t) ->
                logAppend(getAddress(conn) + " spent " + t + "ms."));
    }


    void blockUntilAllTasksFinish() {
//        logAppend("waiting for blocking task");
        synchronized (this) {
            while (!blockedTasks.isEmpty()) {
                this.safeWait();
            }
        }
//        logAppend("waiting for dispatcher task");
        dispatcher.blockUntilAllTasksFinish();
//        logAppend("done");
    }

    boolean checkSortResult() {

        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] > arr[i + 1]) {
                return false;
            }
        }
        return true;
    }

    private void safeWait() {
        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
