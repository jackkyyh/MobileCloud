package com.urop.mobilecloud


import okhttp3.*
import java.util.concurrent.TimeUnit

class WebSocketClient(val mainActivity: MainActivity) {
    //    private val TAG = WebSocketClient::class.java.simpleName
    private val client: OkHttpClient = OkHttpClient.Builder()
        .writeTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .connectTimeout(5, TimeUnit.SECONDS)
        .build()
    private var mWebSocket: WebSocket? = null

    fun connect(url: String) {
//        mWebSocket?.cancel()
        val request = Request.Builder()
            .url(url)
            .build()
        mWebSocket =
            client.newWebSocket(request, SocketListener(this))

        logAppend("Connection starts")
    }

    fun sendMessage(message: String) {
        mWebSocket!!.send(message)
//        logAppend("Sent: $message")
    }

    fun close(code: Int, reason: String?) {
        mWebSocket!!.close(code, reason)
        logAppend("Connection closed")
    }

    fun logAppend(str: String) {
        mainActivity.logAppend((str))
    }

    fun msgParser(msg: String) {
        mainActivity.msgParser(msg)
    }

    internal class SocketListener(var webSocket: WebSocketClient) : WebSocketListener() {

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            msgParser(text)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            logAppend("Failure: " + t.message)
        }

        fun logAppend(str: String) {
            webSocket.logAppend((str))
        }

        fun msgParser(msg: String) {
            webSocket.msgParser(msg)
        }

//        override fun onOpen(webSocket: WebSocket, response: Response) {
//            super.onOpen(webSocket, response)
//        }
//        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
//            super.onMessage(webSocket, bytes)
//            Log.i(TAG, "onMessage bytes=$bytes")
//        logAppend("Msg: $bytes")
//    }

//        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
//            super.onClosing(webSocket, code, reason)
//
//        }

//        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
//            super.onClosed(webSocket, code, reason)
//        }

    }

    fun sendMessage(t: Task) {
        sendMessage(t.task2json())
    }
}
