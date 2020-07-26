package com.urop.mobilecloud


import com.urop.common.Profiler.profiler
import com.urop.common.Task
import com.urop.common.toBB
import okhttp3.*
import okio.ByteString
import okio.ByteString.Companion.toByteString
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit

class WebSocketClient(private val mainActivity: MainActivity) {
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
    }


    fun sendMessage(message: ByteBuffer) {
        mWebSocket!!.send(message.toByteString())
//        logAppend("Sent a msg")
    }

    fun sendTask(message: Task) {
        val bb = profiler.add("serial", Task::toBB, message)
        sendMessage(bb)
    }

    fun close(code: Int, reason: String?) {
        mWebSocket!!.close(code, reason)
        logAppend("Connection closed")
    }

    fun logAppend(str: String) {
        mainActivity.logAppend((str))
    }

    fun msgParser(msg: ByteArray) {
        mainActivity.msgParser(msg)
    }

    fun failure() {
        mainActivity.retryNetSwitch()
    }

    internal class SocketListener(var wsClient: WebSocketClient) : WebSocketListener() {


        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            super.onMessage(webSocket, bytes)
            msgParser(bytes.toByteArray())
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            logAppend("Failure: " + t.message)
            wsClient.failure()
        }

        fun logAppend(str: String) {
            wsClient.logAppend((str))
        }

        fun msgParser(msg: ByteArray) {
            wsClient.msgParser(msg)
        }
    }
}
