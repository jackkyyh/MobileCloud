package com.urop.server;


import com.urop.common.Task;

import org.java_websocket.WebSocket;

import static com.urop.common.UtilsKt.json2task;
import static com.urop.common.UtilsKt.task2json;
import static com.urop.server.Utils.logAppend;

public class Server {

    //    Gson gson;
    WebSocketServer webSocketServer;
    Dispatcher dispatcher;
    TaskController taskController;

    public Server() {

        int port = 9544;
        webSocketServer = new WebSocketServer(port, this);
        webSocketServer.start();
        System.out.println("Server started on port: " + webSocketServer.getPort());

        dispatcher = new Dispatcher();

    }

    public static void main(String[] args) {
        Server server = new Server();

//        TaskController sorter = new NopController();
        TaskController sorter = new SortController();

        server.run(sorter);
    }

    public void run(TaskController r) {

        taskController = r;
        r.setDispatcher(dispatcher);

        Thread tDispatch = new Thread(dispatcher);
        Thread tSort = new Thread(r);
        tDispatch.start();
        tSort.start();

    }


    public void nodeDisconnect(WebSocket conn) {
        dispatcher.removeNode(conn);
    }

    public void newNode(WebSocket conn) {

//        Gson gson = new Gson();
        Task greetings = new Task("Message", "Greetings from the server!", "");
        conn.send(task2json(greetings)); //This method sends a message to the new client

        dispatcher.addAvailNode(conn);
    }

    public void msgParser(WebSocket conn, String msg) {
//        String header = msg.substring(0, 7);
//        String body = msg.substring(9, msg.length());
//        System.out.println("__"+header+"__");
//        System.out.println("__"+body+"__");
        Task task = json2task(msg);
        if (task.cmd.equals("Message")) {
            logAppend("Msg: " + task.data);
//            try{
//                Thread.sleep(5000);
//            } catch (InterruptedException ex){
//                logAppend(ex.getMessage());
//            }
        } else {
//            logAppend(msg);
            taskController.submitTask(conn, task);
        }

//        logAppend(Thread.currentThread().getName() + "working");
    }
}
