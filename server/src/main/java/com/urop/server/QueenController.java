package com.urop.server;

import com.urop.common.Task;

import org.java_websocket.WebSocket;

import static com.urop.common.SerializerKt.toIArr;
import static com.urop.common.SerializerKt.toIntArr2d;
import static com.urop.common.SerializerKt.toJson;
import static com.urop.server.Utils.logAppend;

public class QueenController extends TaskController {

    int numOfSolution;
    final int N;
    final int step;


    public QueenController() {
        this(8, 2);
    }

    public QueenController(int N, int step) {
        this.N = N;
        this.step = step;
    }

    @Override
    void reallyRun() {
        int[] arr = new int[N];
        Task t = new Task("QUEEN");
        t.strData = toJson(new int[]{N, step});
        t.iArrData = arr;
        dispatcher.addPendingTask(t);
    }

    @Override
    boolean checkResult() {
        logAppend("num of solution: " + numOfSolution);
        return true;
    }

    @Override
    public void commitTask(WebSocket conn, Task t) {

//        logAppend("recieved");
        int[][] res = toIntArr2d(t.bArrData);
//        logAppend("parsed");


        if (toIArr(t.strData)[0] == 0 && res.length > 0) {
//            logAppend("find " + res.length + " solution");
            numOfSolution += res.length;
        } else {
            for (int[] arr : res) {
                Task newT = new Task("QUEEN");
                newT.strData = t.strData;
                newT.iArrData = arr;
                dispatcher.addPendingTask(newT);
//                logAppend("new task created");
            }
        }
        super.commitTask(conn, t);

    }
}
