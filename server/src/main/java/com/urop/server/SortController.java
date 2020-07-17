package com.urop.server;

import com.urop.common.Task;
import com.urop.common.UtilsKt;

import org.java_websocket.WebSocket;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import static com.urop.common.UtilsKt.toIArr;
import static com.urop.common.UtilsKt.toJson;
import static com.urop.server.Utils.logAppend;


public class SortController extends TaskController {

    final int ARR_LENGTH = 40000;
    final int MIN_SEG_LENGTH = 10000;
    public int[] arr;

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
        String dep1 = encodeID(start, middle);
        String dep2 = encodeID(middle, end);
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
        return new Task(header, encodeID(start, end));
    }

    String encodeID(int start, int end) {
        return toJson(new int[]{start, end});
    }

    int[] decodeID(String id) {
//        String[] strArr = meta.split(",");
        return toIArr(id);
    }

    @Override
    public synchronized void commitTask(WebSocket conn, Task t) {

        logAppend("receive: " + t.id);
        int[] res = toIArr(t.data);
        logAppend("parse done");
        int index = decodeID(t.id)[0];
//        logAppend("index = "+index);
        int i = 0;
        while (i < res.length) {
            arr[index++] = res[i++];
        }
//        logAppend("new arr: " + toJson(arr));
        if (dispatcher.commitTask(conn, t)) {
            Collection<Task> set = blockedTasks.get(t.id);
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
                blockedTasks.remove(t.id);
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

        int[] index = decodeID(t.id);
        int[] subarr = Arrays.copyOfRange(arr, index[0], index[1]);
        t.data = UtilsKt.toBArr(subarr);
    }


    void blockUntilAllTasksFinish() {
//        logAppend("waiting for blocking task");
        synchronized (this) {
            while (!blockedTasks.isEmpty()) {
                this.safeWait();
            }
        }
//        logAppend("waiting for dispatcher task");
        super.blockUntilAllTasksFinish();
//        logAppend("done");
    }

    @Override
    void reallyRun() {
        sort(0, arr.length);
    }

    @Override
    boolean checkResult() {
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] > arr[i + 1]) {
                return false;
            }
        }
        return true;
    }

}
