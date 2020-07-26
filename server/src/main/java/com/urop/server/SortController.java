package com.urop.server;

import com.esotericsoftware.kryonet.Connection;
import com.urop.common.Task;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import static com.urop.common.SerializerKt.toIArr;
import static com.urop.common.SerializerKt.toJson;
import static com.urop.server.Utils.logAppend;


public class SortController extends TaskController {

    final int MIN_SEG_LENGTH;
    final int ARR_LENGTH;
    public int[] arr;

    volatile Map<String, Collection<Task>> blockedTasks;


    public SortController() {
        this(100000, 5000);
    }

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
    public synchronized void commitTask(Connection conn, Task t) {

//        logAppend("receive: " + t.id);
        int[] res;
//        profiler.add("deserialization", ()->{res = toIArr(t.data);});
//        logAppend("parse done");
//        res = profiler.add("deserial(IntArr)", SerializerKt::toIArr, t.iArrData);
        res = t.iArrData;
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

//        int[] subarr = profiler.add("arrayCopy",
//                (ind)->{
//            return Arrays.copyOfRange(arr, ind[0], ind[1]);}, index);

//        t.data = profiler.add("serialization", UtilsKt::toBArr, subarr);
//        profiler.add("serial(IntArr)", ()->{
//            t.iArrData = SerializerKt.toBArr(subarr);
//        });
        t.iArrData = Arrays.copyOfRange(arr, index[0], index[1]);
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
