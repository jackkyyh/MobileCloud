package com.urop.server;

import com.urop.common.Task;

import org.java_websocket.WebSocket;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.urop.common.UtilsKt.toBArr;
import static com.urop.common.UtilsKt.toIArr;
import static com.urop.server.Utils.logAppend;
//import static com.urop.server.Utils.

public class Dispatcher implements Runnable {

    List<WebSocket> availNodes;
    List<WebSocket> busyNodes;
    volatile List<Task> pendingTasks;
    volatile Map<WebSocket, Task> executingTasks;
    volatile Collection<String> finishedTasks;

    volatile Map<WebSocket, Integer> realWorkingTime;

    Dispatcher() {
        availNodes = new LinkedList<>();
        busyNodes = new LinkedList<>();
        pendingTasks = new LinkedList<>();
        executingTasks = new HashMap<>();
        finishedTasks = new HashSet<>();
        realWorkingTime = new HashMap<>();
//        server = s;
    }

    public void dispatch() {
        while (true) {
            synchronized (this) {
                while (!availNodes.isEmpty() && !pendingTasks.isEmpty()) {
                    WebSocket avail = availNodes.remove(0);

                    Task t = pendingTasks.remove(0);
                    avail.send(toBArr(t));
//            logAppend("send: " + task2json(t));
                    busyNodes.add(avail);
                    executingTasks.put(avail, t);
//                    logAppend(t.meta + " added to executing");
                }
//                logAppend("wait");
                safeWait();
            }
        }
    }


    public synchronized boolean isAllTasksFinished() {
        return executingTasks.isEmpty() && pendingTasks.isEmpty();
    }


    public synchronized void blockUntilAllTasksFinish() {
        while (!isAllTasksFinished()) {
            safeWait();
        }
    }

    public synchronized void blockUntilSomeNodeConnect(int wait) {
        while (numOfNode() < wait) {
            logAppend("Waiting for any nodes to connect...");
            safeWait();
        }
    }

    public synchronized boolean commitTask(WebSocket conn, Task t) {

        if (!busyNodes.remove(conn)) {
            logAppend("busyNodes removal failed!");
            return false;
        }
        availNodes.add(conn);

//        try{
//            Thread.sleep(10);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        Task rem = executingTasks.remove(conn);
        if (rem == null) {
            logAppend("executingTasks removal failed!");
            logAppend("Task: " + t.cmd + Arrays.toString(toIArr(t.meta)));
            return false;
        } else {
            assert rem.meta.equals(t.meta);
//            logAppend(t.meta + " removed from executing");
        }
        finishedTasks.add(t.meta);

        Integer ws = realWorkingTime.getOrDefault(conn, 0);
        ws += t.waitCount;
        realWorkingTime.put(conn, ws);

        this.notifyAll();

//        printTaskCount();
        return true;
    }

    public synchronized void addAvailNode(WebSocket node) {
        availNodes.add(node);
//        synchronized (this){
        this.notifyAll();
//        }
    }


    public synchronized void addPendingTask(Task t) {
        pendingTasks.add(t);
        this.notifyAll();
    }

    public synchronized void removeNode(WebSocket conn) {
        availNodes.remove(conn);
        busyNodes.remove(conn);
        Task t = executingTasks.remove(conn);
        if (t != null) {
            pendingTasks.add(t);
        }
    }


    public int numOfNode() {
        return availNodes.size() + busyNodes.size();
    }

    public Map<WebSocket, Integer> getRealWorkingTime() {
        return realWorkingTime;
    }

    @Override
    public void run() {
        dispatch();
    }

    public void safeWait() {
        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
