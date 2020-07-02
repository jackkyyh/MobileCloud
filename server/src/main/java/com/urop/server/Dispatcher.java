package com.urop.server;

import org.java_websocket.WebSocket;

import java.util.LinkedList;

import static com.urop.server.Utils.task2json;

public class Dispatcher extends Thread {

    LinkedList<WebSocket> avaiNodes;
    LinkedList<WebSocket> busyNodes;
    volatile LinkedList<Task> pendingTasks;
    volatile LinkedList<String> executingTasks;

    Dispatcher() {
        avaiNodes = new LinkedList<>();
        busyNodes = new LinkedList<>();
        pendingTasks = new LinkedList<>();
        executingTasks = new LinkedList<>();
    }

//    public synchronized void tryDispatch() {
//        if (!avaiNodes.isEmpty() && !pendingTasks.isEmpty()) {
//            WebSocket avai = avaiNodes.removeFirst();
//            Task t = pendingTasks.removeFirst();
//            avai.send(task2json(t));
//            busyNodes.addLast(avai);
//            executingTasks.addLast(t.header);
//        }
//    }

    public synchronized void loopDispatch() {
        while (!avaiNodes.isEmpty() && !pendingTasks.isEmpty()) {
            WebSocket avai = avaiNodes.removeFirst();
            Task t = pendingTasks.poll();
            avai.send(task2json(t));
//            logAppend("send: " + task2json(t));
            busyNodes.add(avai);
            assert t != null;
            executingTasks.add(t.header);

        }
    }

//    public void run() {
//        while (true) {
//            tryDispatch();
//        }
//    }

    public synchronized void addAvaiNode(WebSocket node) {
        avaiNodes.add(node);
    }

    public synchronized void addTask(Task t) {
        pendingTasks.addLast(t);
    }

    public synchronized boolean allTasksFinished() {
//        logAppend("pending: " + pendingTasks.size());
        int pend = pendingTasks.size();
        int exe = executingTasks.size();
//        if(pend != 0 || exe != 0){
//            logAppend("pend: " + pend + " exe: " + exe);
//        }
        return executingTasks.isEmpty() && pendingTasks.isEmpty();
    }

    public synchronized void printTaskSize() {
//        logAppend("pending: " + pendingTasks.size());
//        logAppend("executing: " + executingTasks.size());
    }

    public synchronized boolean commitTask(WebSocket conn, Task t) {

        if (!busyNodes.remove(conn)) {
//            logAppend("busyNodes removal failed!");
            return false;
        }
        avaiNodes.addLast(conn);
        if (!executingTasks.remove(t.header)) {
//            logAppend("executingTasks removal failed!");
            return false;
        }

//        if(allTasksFinished()){
//            Thread.currentThread().notifyAll();
//        }
        printTaskSize();
        return true;
    }

    public synchronized void removeNode(WebSocket conn) {
        avaiNodes.remove(conn);
        busyNodes.remove(conn);
    }
}
