package com.urop.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.urop.common.Task;

import static com.urop.common.SerializerKt.BUFFER_SIZE;
import static com.urop.common.SerializerKt.register;
import static com.urop.server.Server.server;

public class KryoNetServer extends com.esotericsoftware.kryonet.Server {

    KryoNetServer() {
        super(BUFFER_SIZE, BUFFER_SIZE);
        register(this);

        addListener(new Listener() {
            @Override
            public void received(Connection connection, Object o) {
                if (o instanceof Task) {
                    server.taskParser(connection, (Task) o);
                }

            }

            @Override
            public void connected(Connection connection) {
                server.newNode(connection);
            }

            @Override
            public void disconnected(Connection connection) {
                server.nodeDisconnect(connection);
            }

//            @Override
//            public void idle(Connection connection) {
//                logAppend("idle...");
//            }
        });
    }
}
