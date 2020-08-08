package com.urop.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.urop.common.Task;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.urop.common.SerializerKt.BUFFER_SIZE;
import static com.urop.common.SerializerKt.register;
import static com.urop.server.Server.server;

public class KryoNetServerEndPoint extends Listener implements ServerEndPoint {

    Server KNServer;
    private Map<Connection, com.urop.common.Connection> connectionMap = new HashMap<>();

    KryoNetServerEndPoint() {
        KNServer = new Server(BUFFER_SIZE, BUFFER_SIZE);
        register(KNServer);
        KNServer.addListener(this);
        KNServer.start();
        try {
            KNServer.bind(9544, 9566);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Server started on port: " + 9544);
    }

    private com.urop.common.Connection wrapConnection(Connection c) {
        com.urop.common.Connection cc = connectionMap.get(c);
        if (cc == null) {
            cc = new com.urop.common.Connection(c);
            connectionMap.put(c, cc);
        }
        return cc;
    }

    @Override
    public void received(Connection connection, Object o) {
        if (o instanceof Task) {
            server.taskParser(wrapConnection(connection), (Task) o);
        }
    }

    @Override
    public void connected(Connection connection) {
        connected(wrapConnection(connection));
    }

    @Override
    public void disconnected(Connection connection) {
        disconnected(wrapConnection(connection));
    }

    @Override
    public void connected(com.urop.common.Connection lc) {
        server.newNode(lc);
    }

    @Override
    public void disconnected(com.urop.common.Connection lc) {
        server.nodeDisconnect(lc);
    }
}
