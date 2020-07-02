package com.urop.server;

import com.google.gson.Gson;

import org.java_websocket.WebSocket;

import java.util.Arrays;
import java.util.Random;

import static com.urop.server.Utils.arr2json;
import static com.urop.server.Utils.json2arr;
import static com.urop.server.Utils.json2task;
import static com.urop.server.Utils.logAppend;

public class Server {

    Gson gson;
    volatile int[] arr;
    WebSocketServer webSocketServer;
    Dispatcher dispatcher;

    public Server() {
        gson = new Gson();
        dispatcher = new Dispatcher();

        int port = 9544;
        webSocketServer = new WebSocketServer(port, this);
        webSocketServer.start();

        System.out.println("Server started on port: " + webSocketServer.getPort());


//        logAppend(Thread.currentThread().getName() + "working");
    }

    public static void main(String[] args) throws InterruptedException {

        Server s = new Server();
        Thread.sleep(5000);

        s.sort();
//        BufferedReader sysin = new BufferedReader( new InputStreamReader( System.in ) );
//        while ( true ) {
//            String in = sysin.readLine();
//            s.broadcast( in );
//            if( in.equals( "exit" ) ) {
//                s.stop(1000);
//                break;
//            }
//        }
    }

    public boolean checkSortResult() {
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] > arr[i + 1]) {
                return false;
            }
        }
        return true;
    }

    public void msgParser(WebSocket conn, String msg) {
//        String header = msg.substring(0, 7);
//        String body = msg.substring(9, msg.length());
//        System.out.println("__"+header+"__");
//        System.out.println("__"+body+"__");
        Task task = json2task(msg);
        if (task.header.equals("Message")) {
            logAppend("Msg: " + task.body);
//            try{
//                Thread.sleep(5000);
//            } catch (InterruptedException ex){
//                logAppend(ex.getMessage());
//            }
        } else {
//            logAppend(msg);
            resultReceived(conn, task);
        }

//        logAppend(Thread.currentThread().getName() + "working");
    }

    public void sort() {
        Random r = new Random();
        int count = 100000;
        arr = new int[count];
        for (int i = 0; i < count; i++) {
            arr[i] = r.nextInt(10000);
        }
//        arr = new int[]{4,2,5,6,2,5,8,4,2,3,5,2,2,4,54,6,3};


        sort(0, arr.length);
        while (!dispatcher.allTasksFinished()) {
        }
        logAppend("All done!");
        if (checkSortResult()) {
            logAppend("Check success!");
        } else {
            logAppend("Check failed!");
            logAppend(arr2json(arr));
        }
    }

    public void sort(int start, int end) {
        if (end - start <= 500) {
            addTask("QSRT", start, end);
            return;
        }
        int middle = (start + end) / 2;
        sort(start, middle);
        sort(middle, end);

        while (!dispatcher.allTasksFinished()) {
//            try{
//                Thread.sleep(1000);
//            } catch (InterruptedException ex){
//                logAppend(ex.getMessage());
//            }
//            logAppend("testing failed");
        }
        dispatcher.printTaskSize();
//        try{
//            wait();
//        } catch (InterruptedException ex){
//            logAppend(ex.getMessage());
//        }
//        logAppend("all task finished");

        addTask("MSRT", start, end);
    }

    public void addTask(String header, int start, int end) {
        int[] subarr = Arrays.copyOfRange(arr, start, end);
        int[] meta = {start, end};
//        String header = "MSRT";

        Task t = new Task(header, arr2json(subarr), arr2json(meta));
        dispatcher.addTask(t);
//        logAppend("task " + header + arr2json(meta) + " created");
        dispatcher.loopDispatch();
    }

    public void newNode(WebSocket conn) {

        Task greetings = new Task("Message", "Greetings from the server!", "");
        conn.send(gson.toJson(greetings)); //This method sends a message to the new client
        dispatcher.addAvaiNode(conn);
        dispatcher.loopDispatch();
    }

    private void resultReceived(WebSocket conn, Task task) {
//        logAppend("result: " + task.body);
        int[] res = json2arr(task.body);
        int index = json2arr(task.meta)[0];
//        logAppend("index = "+index);
        int i = 0;
        while (i < res.length) {
            arr[index++] = res[i++];
        }
//        logAppend("new arr: " + arr2json(arr));
        if (dispatcher.commitTask(conn, task)) {
//            logAppend("commit success");
        } else {
            logAppend("commit failed");
        }
        dispatcher.loopDispatch();
    }

    public void nodeDisconnect(WebSocket conn) {
        dispatcher.removeNode(conn);
    }

}
