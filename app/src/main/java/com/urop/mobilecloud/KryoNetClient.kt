package com.urop.mobilecloud

import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import com.urop.common.BUFFER_SIZE
import com.urop.common.Task
import com.urop.common.register
import java.io.IOException
import java.net.InetAddress

class KryoNetClient(private val mainActivity: MainActivity) : Client(BUFFER_SIZE, BUFFER_SIZE) {
    init {
        addListener(MyListener(this))
        register(kryo)
    }

    fun taskReceived(t: Task) {
        mainActivity.taskParser(t)
    }

    override fun connect(p0: Int, p1: InetAddress?, p2: Int, p3: Int) {

        try {
            super.connect(p0, p1, p2, p3)
        } catch (e: IOException) {
            mainActivity.logAppend(e.message!!)
            mainActivity.retryNetSwitch()
        }

    }

    internal class MyListener(private val kn: KryoNetClient) : Listener() {
        override fun received(p0: Connection?, p1: Any?) {
            if (p1 is Task) {
                kn.taskReceived(p1)
            }
        }

    }
}