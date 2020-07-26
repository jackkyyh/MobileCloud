package com.urop.server;


import com.esotericsoftware.kryonet.Connection;
import com.urop.common.Task;

import java.io.IOException;

import static com.urop.server.Utils.getAddress;
import static com.urop.server.Utils.logAppend;

public class Server {


    public final static Server server = new Server();
    Dispatcher dispatcher;
    TaskController taskController;
    //    WebSocketServer webSocketServer;
    KryoNetServer kryoNetServer;

    private Server() {
        int port = 9544;
//        webSocketServer = new WebSocketServer(port, this);
//        webSocketServer.start();
        kryoNetServer = new KryoNetServer();
        kryoNetServer.start();
        try {
            kryoNetServer.bind(9544, 9566);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Server started on port: " + 9544);

        dispatcher = new Dispatcher();

    }

    public static void main(String[] args) {
//        TaskController cter = new NopController();
//        TaskController sorter = new SortController();
//        miniTest();

        TaskController cter = new SortController(1000000, 5000);
        cter.setWAIT_FOR(1);
        server.run(cter);
    }

    public void nodeDisconnect(Connection conn) {
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


    public void newNode(Connection conn) {
        conn.sendTCP(Task.Message("Greetings from the server!"));

        dispatcher.addAvailNode(conn);
    }


    public void taskParser(Connection conn, Task t) {
        if (t.cmd.equals("Message")) {
            logAppend(getAddress(conn) + ": " + t.id);
        } else {
            taskController.commitTask(conn, t);
        }
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

}
