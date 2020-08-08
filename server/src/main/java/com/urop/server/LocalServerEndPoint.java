package com.urop.server;


import com.urop.common.Connection;
import com.urop.common.LocalEndPoint;
import com.urop.common.Pair;
import com.urop.common.Task;

import static com.urop.server.Server.server;


public class LocalServerEndPoint extends LocalEndPoint<Pair<Connection, Object>> implements ServerEndPoint {

    public LocalServerEndPoint() {
        super(obj -> {
            if (obj.second instanceof Task) {
                server.taskParser(obj.first, (Task) obj.second);
            }
        });
    }

    @Override
    public void connected(Connection lc) {
        server.newNode(lc);
    }

    @Override
    public void disconnected(Connection lc) {
        server.nodeDisconnect(lc);
    }

}
