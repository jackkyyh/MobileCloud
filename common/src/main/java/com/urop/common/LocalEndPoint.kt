package com.urop.common

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue


open class LocalEndPoint<T>(private val receivable: Receivable<T>) : Thread("LocalEndPoint"),
    Receivable<T> {
    private val que: BlockingQueue<T> = LinkedBlockingQueue()

    init {
        start()
    }

    override fun received(obj: T) {
        que.put(obj)
    }

    override fun run() {
        while (true) {
            receivable.received(que.take())
        }
    }
}