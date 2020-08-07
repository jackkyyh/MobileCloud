package com.urop.server;

import com.esotericsoftware.kryonet.Connection;
import com.urop.common.MSortTask;
import com.urop.common.QSortTask;
import com.urop.common.SortTask;
import com.urop.common.Task;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import static com.urop.server.Utils.logAppend;

//import static com.urop.common.SerializerKt.toIArr;
//import static com.urop.common.SerializerKt.toJson;


public class SortController extends TaskController {

    final int MIN_SEG_LENGTH;
    final int ARR_LENGTH;
    public int[] arr;

    volatile Map<String, Collection<SortTask>> blockedTasks;


    public SortController(int arrLength, int segLength) {

        ARR_LENGTH = arrLength;
        MIN_SEG_LENGTH = segLength;

        blockedTasks = new HashMap<>();

//        profiler.add("init", ()->{
        Random r = new Random();
        arr = new int[ARR_LENGTH];
        for (int i = 0; i < ARR_LENGTH; i++) {
            arr[i] = r.nextInt(100);
        }
//        });


//        arr = new int[]{4,1,2,5,9,8,7,7,2};
    }

    void sort(int start, int end) {
        if (end - start <= MIN_SEG_LENGTH) {
            SortTask t = new QSortTask(start, end);
            addBlockedTask(null, t);
            return;
        }

        int middle = (start + end) / 2;
        sort(start, middle);
        sort(middle, end);


//        while(!dispatcher.isTaskFinished(dep1) || !dispatcher.isTaskFinished(dep2)){}
        SortTask t = new MSortTask(start, end);
//        String dep1 = createHeader()
        String dep1 = SortTask.Companion.encodeID(start, middle);
        String dep2 = SortTask.Companion.encodeID(middle, end);
        addBlockedTask(new String[]{dep1, dep2}, t);
//        addBlockedTask(dep2, t);
    }


    synchronized void addBlockedTask(String[] depend, SortTask t) {
        if (depend == null) {
            fillTaskData(t);
            dispatcher.addPendingTask(t);
            return;
        }

        for (String dep : depend) {
            if (!dispatcher.isTaskFinished(dep)) {
                Collection<SortTask> set = blockedTasks.get(dep);
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

    }


    @Override
    public synchronized void commitTask(Connection conn, Task tt) {
        SortTask t = (SortTask) tt;
//        logAppend("receive: " + t.id + ": " + toJson(t.arr));
//        logAppend("receive: " + t.id);

        int[] res = t.arr;
        int index = t.start;
        int i = 0;
        while (i < res.length) {
            arr[index++] = res[i++];
        }
//        logAppend("new arr: " + toJson(arr));
        if (dispatcher.commitTask(conn, t)) {
            Collection<SortTask> set = blockedTasks.get(t.id);
            if (set != null) {
                for (SortTask task : set) {
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

    void fillTaskData(SortTask t) {

        t.arr = Arrays.copyOfRange(arr, t.start, t.end);
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
