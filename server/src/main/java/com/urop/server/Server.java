package com.urop.server;


import com.esotericsoftware.kryonet.Connection;
import com.urop.common.Message;
import com.urop.common.Profile;
import com.urop.common.Task;

import java.io.IOException;

import static com.urop.server.Utils.getAddress;
import static com.urop.server.Utils.logAppend;

public class Server {

    Dispatcher dispatcher;
    TaskController taskController;
    KryoNetServer kryoNetServer;

    private Server() {
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

    public final static Server server = new Server();

    public static void main(String[] args) {
//        TaskController ctrler = new NopController();
//        miniTest();

        TaskController ctrler = new SortController(1000000, 5000);
        ctrler.setWaitFor(1);
        server.run(ctrler);
    }

    public void nodeDisconnect(Connection conn) {
        logAppend(" disconnected.");
        dispatcher.removeNode(conn);
    }


    public void newNode(Connection conn) {
        conn.sendTCP(new Message("Greetings from the server!"));

        dispatcher.addAvailNode(conn);
    }


    public void taskParser(Connection conn, Task t) {
        if (t instanceof Message) {
            logAppend(getAddress(conn) + ": " + ((Message) t).getMsg());
        } else if (t instanceof Profile) {
            logAppend(getAddress(conn) + " profile: " + t.toString());
        } else {
            taskController.commitTask(conn, t);
        }
    }

    public void run(TaskController r) {

        taskController = r;

        new Thread(taskController, "Controller").start();
        new Thread(dispatcher, "Dispatcher").start();
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

}
