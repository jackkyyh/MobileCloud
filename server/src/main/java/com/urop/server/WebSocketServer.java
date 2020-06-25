package com.urop.server;

import com.google.gson.Gson;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;

public class WebSocketServer extends org.java_websocket.server.WebSocketServer {

    Gson gson;
    int[] arr;

    public WebSocketServer(int port) {
        super(new InetSocketAddress(port));
        gson = new Gson();
        arr = new int[]{6, 2, 4, 7, 2, 1, 5, 7, 8};
    }

    public static void main(String[] args) {
        int port = 9544;
        WebSocketServer s = new WebSocketServer(port);
        s.start();
        System.out.println("Server started on port: " + s.getPort());

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

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.send("Msag: Greetings from the server!"); //This method sends a message to the new client
//        broadcast( "new connection: " + handshake.getResourceDescriptor() ); //This method sends a message to all clients connected

        System.out.println(getAddress(conn) + ": connection established.");

        String jsonStr = gson.toJson(arr);
        conn.send("Data: " + jsonStr);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
//        broadcast( conn + " has left the room!" );
        System.out.println(getAddress(conn) + ": disconnected.");
    }
//    @Override
//    public void onMessage( WebSocket conn, ByteBuffer message ) {
//        broadcast( message.array() );
//        System.out.println( getAddress(conn) + ": " + message );
//    }

    @Override
    public void onMessage(WebSocket conn, String message) {

        System.out.println(getAddress(conn) + ": " + message);
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

    private String getAddress(WebSocket conn) {
        return conn.getRemoteSocketAddress().getAddress().getHostAddress();
    }
}