package com.urop.common

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.ByteBufferOutputStream
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.google.gson.Gson
import java.nio.ByteBuffer


class Utils


val gson = Gson()

fun String.json2task(): Task {
    return gson.fromJson(this, Task::class.java)
}

fun String.json2arr(): IntArray {
    return gson.fromJson(this, IntArray::class.java)
}

fun IntArray.arr2json(): String {
    return gson.toJson(this)
}

fun Task.task2json(): String {
    return gson.toJson(this)
}


object MyKryo {
    private val kryo = Kryo()
    const val BUFFER_SIZE = 10 * 1024 * 1024  // 10MB
    private val byteBuffer: ByteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE)
    private val barr = ByteArray(BUFFER_SIZE)
    const val enable = true

    init {
        kryo.register(Task::class.java)
        kryo.register(IntArray::class.java)
        kryo.register(ByteArray::class.java)
        kryo.references = false
//        kryo.instantiatorStrategy = StdInstantiatorStrategy()
//        byteBuffer.
    }

    fun bb2task(bytes: ByteBuffer): Task {
//        val bis = ByteArrayInputStream
        val size = bytes.remaining()
        val barr = ByteArray(size)
        bytes.get(barr, 0, size)
        val input = Input(barr, 0, size)
        return kryo.readObject(input, Task::class.java)
    }

    fun task2bb(t: Task): ByteBuffer {
//        val baos = ByteBufferOutputStream()
        val output = Output(BUFFER_SIZE)
        kryo.writeObject(output, t)
//        barr =
//        val bf = ByteBuffer.wrap(barr)

//        output.close()

//        val output = Output(barr)
//        kryo.writeObject(output, t)
        return ByteBuffer.wrap(output.toBytes())
    }

    fun byte2arr(bytes: ByteArray): IntArray {
        val input = Input(bytes, 0, bytes.size)
        return kryo.readObject(input, IntArray::class.java)
    }

    fun bb2arr(bytes: ByteBuffer): IntArray {
//        val bis = ByteArrayInputStream
        val size = bytes.remaining()
        val barr = ByteArray(size)
        bytes.get(barr, 0, size)
        val input = Input(barr, 0, size)
        return kryo.readObject(input, IntArray::class.java)
    }

    fun arr2byte(t: IntArray): ByteArray {
//        val baos = ByteBufferOutputStream()
        val output = Output(BUFFER_SIZE)
        kryo.writeObject(output, t)
        return output.toBytes()
    }

    fun arr2bb(t: IntArray): ByteBuffer {
        val baos = ByteBufferOutputStream()
        val output = Output(baos, BUFFER_SIZE)
        kryo.writeObject(output, t)
        return ByteBuffer.wrap(output.toBytes())
    }
}

