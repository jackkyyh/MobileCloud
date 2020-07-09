package com.urop.server;

import org.java_websocket.WebSocket;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.urop.server.Utils.logAppend;
import static com.urop.server.Utils.task2json;
//import static com.urop.server.Utils.

public class Dispatcher implements Runnable {

    List<WebSocket> availNodes;
    List<WebSocket> busyNodes;
    volatile List<Task> pendingTasks;
    volatile Map<WebSocket, Task> executingTasks;
    volatile Collection<String> finishedTasks;

    volatile Map<WebSocket, Integer> timespent;
//    Server server;

    Dispatcher() {
        availNodes = new LinkedList<>();
        busyNodes = new LinkedList<>();
        pendingTasks = new LinkedList<>();
        executingTasks = new HashMap<>();
        finishedTasks = new HashSet<>();
        timespent = new HashMap<>();
//        server = s;
    }

    private synchronized void loopDispatch() {
        while (!availNodes.isEmpty() && !pendingTasks.isEmpty()) {
            WebSocket avail = availNodes.remove(0);
            Task t = pendingTasks.remove(0);
            avail.send(task2json(t));
//            logAppend("send: " + task2json(t));
            busyNodes.add(avail);
            executingTasks.put(avail, t);
        }
    }

    public void dispatch() {
        while (true) {
            synchronized (this) {
                while (!availNodes.isEmpty() && !pendingTasks.isEmpty()) {
                    WebSocket avail = availNodes.remove(0);

                    Task t = pendingTasks.remove(0);
                    avail.send(task2json(t));
//            logAppend("send: " + task2json(t));
                    busyNodes.add(avail);
                    executingTasks.put(avail, t);
//                    logAppend(t.meta + " added to executing");
                }
//                logAppend("wait");
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
//                    logAppend("recover");
                }
            }
        }
    }


    public synchronized boolean isAllTasksFinished() {
        return executingTasks.isEmpty() && pendingTasks.isEmpty();
    }

//    public boolean isTaskFinished(String str){
//        return finishedTasks.contains(str);
//    }

    public void blockUntilAllTasksFinish() {
        try {
            synchronized (this) {
                while (!isAllTasksFinished()) {
                    this.wait();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void printTaskCount() {
        logAppend("Pending: " + pendingTasks.size() +
                ". Executing: " + executingTasks.size());
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
            logAppend("Task: " + t.cmd + t.meta);
            return false;
        } else {
            assert rem.meta.equals(t.meta);
//            logAppend(t.meta + " removed from executing");
        }
        finishedTasks.add(t.meta);

        Integer ws = timespent.getOrDefault(conn, 0);
        ws += t.waitCount;
        timespent.put(conn, ws);

        this.notifyAll();
        if (isAllTasksFinished()) {
        }

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


    public boolean hasNode() {
        return !availNodes.isEmpty() || !busyNodes.isEmpty();
    }

    public Map<WebSocket, Integer> getTimespent() {
        return timespent;
    }

    @Override
    public void run() {
        dispatch();
    }
}
