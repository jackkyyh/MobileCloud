package com.urop.server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;

import static com.urop.server.Utils.logAppend;

public class WebSocketServer extends org.java_websocket.server.WebSocketServer {

    private Server server;

    public WebSocketServer(int port, Server s) {
        super(new InetSocketAddress(port));
        this.server = s;
    }


    public static String getAddress(WebSocket conn) {
        return conn.getRemoteSocketAddress().getAddress().getHostAddress();
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
//        broadcast( conn + " has left the room!" );
        logAppend(getAddress(conn) + ": disconnected.");
        server.nodeDisconnect(conn);
    }
//    @Override
//    public void onMessage( WebSocket conn, ByteBuffer message ) {
//        broadcast( message.array() );
//        System.out.println( getAddress(conn) + ": " + message );
//    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {

//        logAppend(getAddress(conn) + ": connection established.");
        server.newNode(conn);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
//        if (conn != null) {
        // some errors like port binding failed may not be assignable to a specific websocket
//        }
    }

    @Override
    public void onStart() {
//        System.out.println("Server started.");
//        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
//        logAppend("msg" + message);
//        conn.send(message);
        server.msgParser(conn, message);
//        System.out.println(getAddress(conn) + ": " + message);
    }

}