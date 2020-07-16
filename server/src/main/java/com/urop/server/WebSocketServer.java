package com.urop.server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

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

    @Override
    public void onMessage(WebSocket conn, String message) {
        logAppend("Received a String!!!");
    }


    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {

//        logAppend(getAddress(conn) + ": connection established.");
        server.newNode(conn);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        setConnectionLostTimeout(100);
    }


    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        server.msgParser(conn, message);
    }


}