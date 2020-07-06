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

    LinkedList<WebSocket> availNodes;
    LinkedList<WebSocket> busyNodes;
    volatile List<Task> pendingTasks;
    volatile Map<WebSocket, Task> executingTasks;
    volatile Collection<String> finishedTasks;
//    Server server;

    Dispatcher() {
        availNodes = new LinkedList<>();
        busyNodes = new LinkedList<>();
        pendingTasks = new LinkedList<>();
        executingTasks = new HashMap<>();
        finishedTasks = new HashSet<>();
//        server = s;
    }

    private synchronized void loopDispatch() {
        while (!availNodes.isEmpty() && !pendingTasks.isEmpty()) {
            WebSocket avail = availNodes.removeFirst();
            Task t = pendingTasks.remove(0);
            avail.send(task2json(t));
//            logAppend("send: " + task2json(t));
            busyNodes.add(avail);
            executingTasks.put(avail, t);
        }
    }

    public void dispatch() {
        while (true) {
            loopDispatch();
        }
    }


    public synchronized boolean allTasksFinish() {
        return executingTasks.isEmpty() && pendingTasks.isEmpty();
    }

//    public boolean isTaskFinished(String str){
//        return finishedTasks.contains(str);
//    }

    public void blockUntilAllTasksFinish() {
        try {
            synchronized (this) {
                if (!allTasksFinish()) {
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

        if (executingTasks.remove(conn) == null) {
            logAppend("executingTasks removal failed!");
            return false;
        }
        finishedTasks.add(t.meta);

        if (allTasksFinish()) {
            this.notify();
        }

//        printTaskCount();
        return true;
    }

    public synchronized void addAvailNode(WebSocket node) {
        availNodes.add(node);
//        synchronized (this){
        this.notify();
//        }
    }


    public synchronized void addPendingTask(Task t) {
        pendingTasks.add(t);
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

    @Override
    public void run() {
        dispatch();
    }
}
