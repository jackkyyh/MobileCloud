package com.urop.server.taskController;

//import com.esotericsoftware.kryonet.Connection;

import com.urop.common.Connection;
import com.urop.common.NQueenTask;
import com.urop.common.Task;

import static com.urop.server.Server.logAppend;

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
//        int[] arr = new int[N];
        NQueenTask t = new NQueenTask(N, step);
//        t. new int[]{N, step});
//        t.iArrData = arr;
        dispatcher.addPendingTask(t);
    }

    @Override
    boolean checkResult() {
        logAppend("num of solution: " + numOfSolution);
        return true;
    }

    @Override
    public void commitTask(Connection conn, Task tt) {
        NQueenTask t = (NQueenTask) tt;
//        logAppend("recieved");
        int[][] res = t.solution;
//        logAppend("parsed");


        if (t.remaining == 0 && res.length > 0) {
//            logAppend("find " + res.length + " solution");
            numOfSolution += res.length;
        } else {
            for (int[] arr : res) {
                NQueenTask newT = new NQueenTask(arr, t.remaining, t.step);
                dispatcher.addPendingTask(newT);
//                logAppend("new task created");
            }
        }
        super.commitTask(conn, t);

    }
}
