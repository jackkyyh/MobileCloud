package com.urop.common

import com.esotericsoftware.kryonet.Connection


class Connection private constructor(val name: String) {
    var kn: Connection? = null
    var re: Receivable<Any>? = null

    // remote connection
    constructor(kn: Connection) : this(kn.remoteAddressTCP.hostName) {
        this.kn = kn
    }

    // local connection
    constructor(name: String, re: Receivable<Any>) : this(name) {
        this.re = re
    }

    fun send(obj: Any) {
        if (kn != null) {
            kn!!.sendTCP(obj)
        } else {
            re!!.received(obj)
        }
    }
}

interface Receivable<T> {
    fun received(obj: T)
}

data class Pair<T, V>(@JvmField var first: T, @JvmField var second: V)