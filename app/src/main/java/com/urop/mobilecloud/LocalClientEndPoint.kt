package com.urop.mobilecloud

import com.urop.common.*
import com.urop.server.LocalServerEndPoint
import com.urop.server.Server


class LocalClientEndPoint : ClientEndPoint, LocalEndPoint<Any>(object : Receivable<Any> {
    override fun received(obj: Any) {
        if (obj is Task) {
            MainActivity.mainActivity.taskParser(obj)
        }
    }
}) {
    lateinit var serverEP: LocalServerEndPoint
    lateinit var lc: Connection
    var connected = false
    var pendingObj: Any? = null
    val lock = Object()


    override fun connect(): Boolean {
        if (Server.server.ep is LocalServerEndPoint) {
            serverEP = Server.server.ep as LocalServerEndPoint
            lc = Connection("LocalNode", this)
            serverEP.connected(lc)
            connected = true
            Thread(this, "ClientEndPoint").start()
            return true
        } else {
            if (Server.server.ep == null) {
                MainActivity.mainActivity.logAppend("server ep null!")
            }
            MainActivity.mainActivity.logAppend("Local server connection failed!")
            return false
        }
    }

    override fun send(obj: Any) {
        if (connected) {
            serverEP.received(Pair<Connection, Any>(lc, obj))
        }
    }

    override fun disconnect() {
        serverEP.disconnected(lc)
    }
}