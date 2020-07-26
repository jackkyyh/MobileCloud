package com.urop.server;


import com.urop.common.SerializerKt;
import com.urop.common.Task;

import org.java_websocket.WebSocket;

import java.nio.ByteBuffer;

import static com.urop.common.Profiler.profiler;
import static com.urop.common.SerializerKt.toBArr;
import static com.urop.server.Utils.getAddress;
import static com.urop.server.Utils.logAppend;

public class Server {


    WebSocketServer webSocketServer;
    Dispatcher dispatcher;
    TaskController taskController;

    final static Server theServer = new Server();

    private Server() {
        int port = 9544;
        webSocketServer = new WebSocketServer(port, this);
        webSocketServer.start();
        System.out.println("Server started on port: " + webSocketServer.getPort());

        dispatcher = new Dispatcher();

    }

    public static void main(String[] args) {
//        TaskController sorter = new NopController();
//        TaskController sorter = new SortController();
//        miniTest();

        TaskController cter = new SortController(1000000, 5000);
        cter.setWAIT_FOR(1);
        getServer().run(cter);
    }


    public static Server getServer() {
        return theServer;
    }


    public void nodeDisconnect(WebSocket conn) {
        dispatcher.removeNode(conn);
    }

    public void run(TaskController r) {

        taskController = r;

//        Thread t1 = new Thread()
        new Thread(taskController, "controller").start();
        new Thread(dispatcher, "dispatcher").start();
//        tDispatch;
//        tSort.start();

    }

    public void newNode(WebSocket conn) {
        conn.send(toBArr(Task.Message("Greetings from the server!")));
//        logAppend("sent a msg");

        dispatcher.addAvailNode(conn);
    }

    public void msgParser(WebSocket conn, ByteBuffer msg) {
        Task task = profiler.add("deserial", SerializerKt::toTask, msg);
        if (task.cmd.equals("Message")) {
            logAppend(getAddress(conn) + ": " + task.id);
        } else {
            taskController.commitTask(conn, task);
        }

    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

}
