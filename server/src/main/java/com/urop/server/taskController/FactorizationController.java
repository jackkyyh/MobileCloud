package com.urop.server.taskController;

import com.urop.common.Connection;
import com.urop.common.FactorizationTask;
import com.urop.common.Task;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static com.urop.server.Server.logAppend;

public class FactorizationController extends TaskController {
    BigInteger num;
    Map<BigInteger, Integer> factors;

    public FactorizationController(String num) {
        this.num = new BigInteger(num);
        factors = new HashMap<>();
    }

    @Override
    void reallyRun() {
        dispatcher.addPendingTask(new FactorizationTask(num));
    }

    @Override
    public void commitTask(Connection conn, Task tt) {
        FactorizationTask t = (FactorizationTask) tt;
//        logAppend("convert");
        if (t.f1.equals(BigInteger.ONE)) {
//            logAppend("get a prime");
            int c = factors.getOrDefault(t.f2, 0);
            factors.put(t.f2, c + 1);
        } else {
            dispatcher.addPendingTask(new FactorizationTask(t.f1));
            dispatcher.addPendingTask(new FactorizationTask(t.f2));
        }
        dispatcher.commitTask(conn, tt);
    }

    @Override
    boolean checkResult() {

        BigInteger prod = new BigInteger("1");
        for (Map.Entry<BigInteger, Integer> entry : factors.entrySet()) {
            prod = prod.multiply(entry.getKey().pow(entry.getValue()));
            logAppend(entry.getKey().toString() + "^" + entry.getValue());
        }
        return prod.equals(num);
    }
}
