package com.urop.mobilecloud

import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import com.urop.common.BUFFER_SIZE
import com.urop.common.Task
import com.urop.common.register
import com.urop.mobilecloud.MainActivity.Companion.mainActivity
import java.io.IOException

class KryoNetClientEndPoint : Client(BUFFER_SIZE, BUFFER_SIZE), ClientEndPoint {
    val serverAdd = "192.168.10.143"

    init {
        addListener(MyListener())
        register(this)
        start()
    }

//    override fun connect(p0: Int, p1: InetAddress?, p2: Int, p3: Int) {
//        try {
//            super.connect(p0, p1, p2, p3)
//        } catch (e: IOException) {
//            mainActivity.logAppend(e.message!!)
////            mainActivity.retryNetSwitch()
//        }
//    }

    internal class MyListener : Listener() {
        override fun received(p0: Connection?, p1: Any?) {
            if (p1 is Task) {
                mainActivity.taskParser(p1)
            }
        }

        override fun disconnected(p0: Connection?) {
            mainActivity.logAppend("Disconnected.")
        }

    }

    override fun connect(): Boolean {
        try {
            connect(5000, serverAdd, 9544, 9566)
        } catch (e: IOException) {
            mainActivity.logAppend(e.message!!)
            return false
        }
        return true
    }

    override fun send(obj: Any) {
        sendTCP(obj)
    }

    override fun disconnect() {
        close()
    }
}