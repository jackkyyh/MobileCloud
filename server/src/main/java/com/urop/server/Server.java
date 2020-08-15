package com.urop.server;


//import com.esotericsoftware.kryonet.Connection;

import com.urop.common.Connection;
import com.urop.common.Message;
import com.urop.common.Profile;
import com.urop.common.Task;
import com.urop.server.taskController.FactorizationController;
import com.urop.server.taskController.TaskController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Server {

    Dispatcher dispatcher;
    TaskController taskController;
    public static Server server;
    ServerEndPoint ep;

    private Server(boolean LOCALMODE) {
        logAppend("local: " + LOCALMODE);
        ep = LOCALMODE ? new LocalServerEndPoint() : new KryoNetServerEndPoint();

        dispatcher = new Dispatcher();

    }

    public static void main(String[] args) {
//        TaskController ctrler = new NopController();
//        miniTest();

        logAppend("This is the server!");

        server = new Server(args.length > 0 && Boolean.parseBoolean(args[0]));
        TaskController ctrler = new FactorizationController("90562281679333");
        // 27065524748708
        // 2620725173057
        // 1495156599569
        // 150812360887663
        // 38299603481
        ctrler.setWaitFor(1);
        server.run(ctrler);
    }

    public void nodeDisconnect(Connection conn) {
        logAppend(" disconnected.");
        dispatcher.removeNode(conn);
    }

    public static void logAppend(String text) {

        String curTime = new SimpleDateFormat("[HH:mm:ss:SSS] ", Locale.getDefault()).format(new Date());
        System.out.println(curTime + text);
    }

    public void newNode(Connection conn) {
        conn.send(new Message("Greetings from the server!"));

        dispatcher.addAvailNode(conn);
    }


    public void run(TaskController r) {

        taskController = r;

        new Thread(taskController, "Controller").start();
        new Thread(dispatcher, "Dispatcher").start();
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public void taskParser(Connection conn, Task t) {
//        logAppend("task parser receives task " + t.id);
        if (t instanceof Message) {
            logAppend(conn.getName() + ": " + ((Message) t).getMsg());
        } else if (t instanceof Profile) {
            logAppend(conn.getName() + " profile: " + t.toString());
        } else {
//            logAppend("go to task controller commit");
            taskController.commitTask(conn, t);
        }
//        logAppend("task parser done");
    }

    public ServerEndPoint getEp() {
        return ep;
    }

}
