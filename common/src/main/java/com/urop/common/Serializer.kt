package com.urop.common

import com.esotericsoftware.kryonet.EndPoint

const val BUFFER_SIZE = 50 * 1024 * 1024

//val gson = Gson()

fun IntArray.toJson(): String {
    return "[" + this.joinToString() + "]"
}

fun register(ep: EndPoint) {
    val kryo = ep.kryo
    kryo.register(Task::class.java)
    kryo.register(Message::class.java)
    kryo.register(QSortTask::class.java)
    kryo.register(MSortTask::class.java)
    kryo.register(NQueenTask::class.java)
    kryo.register(NOPTask::class.java)
    kryo.register(Profile::class.java)
    kryo.register(HashMap::class.java)

    kryo.register(IntArray::class.java)
//    kryo.register(ByteArray::class.java)
//    kryo.register(String::class.java)
    kryo.register(Array<IntArray>::class.java)
    kryo.references = false
}
