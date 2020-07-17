package com.urop.server;


import com.urop.common.Task;

import org.java_websocket.WebSocket;

import java.nio.ByteBuffer;

import static com.urop.common.UtilsKt.toBArr;
import static com.urop.common.UtilsKt.toTask;
import static com.urop.server.Utils.getAddress;
import static com.urop.server.Utils.logAppend;

public class Server {

    //    Gson gson;
    WebSocketServer webSocketServer;
    Dispatcher dispatcher;
    TaskController taskController;

    final static Server theServer = new Server();
    final boolean KRYO;

    private Server() {
        KRYO = true;
        int port = 9544;
        webSocketServer = new WebSocketServer(port, this);
        webSocketServer.start();
        System.out.println("Server started on port: " + webSocketServer.getPort());

        dispatcher = new Dispatcher();

    }

    public static void main(String[] args) {
//        TaskController sorter = new NopController();
        TaskController sorter = new SortController();

        getServer().run(sorter);
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
        new Thread(taskController, "sorter").start();
        new Thread(dispatcher, "dispatcher").start();
//        tDispatch;
//        tSort.start();

    }

    public void newNode(WebSocket conn) {
        conn.send(toBArr(Task.Greeting("Greetings from the server!"))); //This method sends a message to the new client

        dispatcher.addAvailNode(conn);
    }

    public void msgParser(WebSocket conn, ByteBuffer msg) {
        Task task = toTask(msg);
        if (task.cmd.equals("Message")) {
            logAppend(getAddress(conn) + ": " + task.meta);
        } else {
            taskController.submitTask(conn, task);
        }
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }
}
