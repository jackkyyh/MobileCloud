package com.urop.server;

import com.urop.common.Task;

import org.java_websocket.WebSocket;

import static com.urop.common.UtilsKt.toBArr;
import static com.urop.common.UtilsKt.toIArr;
import static com.urop.common.UtilsKt.toIntArr2d;
import static com.urop.common.UtilsKt.toJson;
import static com.urop.server.Utils.logAppend;

public class QueenController extends TaskController {

    int numOfSolution;

    @Override
    void reallyRun() {
        int[] arr = new int[8];
        Task t = new Task("QUEEN");
        t.meta = toJson(new int[]{15, 7});
        t.data = toBArr(arr);
        dispatcher.addPendingTask(t);
    }

    @Override
    boolean checkResult() {
        logAppend(Integer.toString(numOfSolution));
        return true;
    }

    @Override
    public void commitTask(WebSocket conn, Task t) {

        int[][] res = toIntArr2d(t.data);


        if (toIArr(t.meta)[0] == 0 && res.length > 0) {
//            logAppend("find " + res.length + " solution");
            numOfSolution += res.length;
        } else {
            for (int[] arr : res) {
                Task newT = new Task("QUEEN");
                newT.meta = t.meta;
                newT.data = toBArr(arr);
                dispatcher.addPendingTask(newT);
//                logAppend("new task created");
            }
        }
//        logAppend("num of solution: " + res.length);
        super.commitTask(conn, t);


//        dispatcher.addPendingTask(t);
    }
}
