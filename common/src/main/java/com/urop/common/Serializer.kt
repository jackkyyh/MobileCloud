package com.urop.common

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.esotericsoftware.kryonet.EndPoint
import com.google.gson.Gson
import java.nio.ByteBuffer

val gson = Gson()


fun String.toIArr(): IntArray {
    return gson.fromJson(this, IntArray::class.java)
}

fun IntArray.toJson(): String {
    return gson.toJson(this)
}

fun Task.toJson(): String {
    return gson.toJson(this)
}


fun ByteArray.toIArr(): IntArray {
    val input = Input(this, 0, this.size)
    return MyKryo.myKryo.readObject(input, IntArray::class.java)
}

fun ByteArray.toIntArr2d(): Array<IntArray> {
    val input = Input(this, 0, this.size)
    return MyKryo.myKryo.readObject(input, Array<IntArray>::class.java)
}
//fun ByteArray.toString_(): String {
//    val input = Input(this, 0, this.size)
//    return MyKryo.kryo.readObject(input, String::class.java)
//}

fun ByteArray.toTask(): Task {
    val input = Input(this, 0, this.size)
    return MyKryo.myKryo.readObject(input, Task::class.java)
}

fun ByteBuffer.toTask(): Task {
    val input = Input(this.array(), 0, this.remaining())
    return MyKryo.myKryo.readObject(input, Task::class.java)
}

fun ByteBuffer.toIArr(): IntArray {
    val input = Input(this.array(), 0, this.remaining())
    return MyKryo.myKryo.readObject(input, IntArray::class.java)
}

fun CharArray.toBArr(): ByteArray {
    val output = Output(MyKryo.BUFFER_SIZE)
    MyKryo.myKryo.writeObject(output, this)
    return output.toBytes()
}

fun IntArray.toBArr(): ByteArray {
    val output = Output(MyKryo.BUFFER_SIZE)
    MyKryo.myKryo.writeObject(output, this)
    return output.toBytes()
}

fun Array<IntArray>.toBArr(): ByteArray {
    val output = Output(MyKryo.BUFFER_SIZE)
    MyKryo.myKryo.writeObject(output, this)
    return output.toBytes()
}

fun String.toBArr(): ByteArray {
    val output = Output(MyKryo.BUFFER_SIZE)
    MyKryo.myKryo.writeObject(output, this)
    return output.toBytes()
}

fun Task.toBArr(): ByteArray {
    val output = Output(MyKryo.BUFFER_SIZE)
    MyKryo.myKryo.writeObject(output, this)
    return output.toBytes()
}

//fun IntArray.toBB(): ByteBuffer {
//    return ByteBuffer.wrap(this.toBAarr())
//}

fun Task.toBB(): ByteBuffer {
    return ByteBuffer.wrap(this.toBArr())
}

//fun ByteArray.equals(b2: ByteArray): Boolean{
//
//}
//fun IntArray.pack

object MyKryo {
    val myKryo = Kryo()

    // 50MB
    const val BUFFER_SIZE = 50 * 1024 * 1024

    //    private val byteBuffer: ByteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE)
//    private val barr = ByteArray(BUFFER_SIZE)
    const val enable = true

    init {
        register(myKryo)
//        byteBuffer.
    }

}

const val BUFFER_SIZE = 50 * 1024 * 1024

fun register(ep: EndPoint) {
    val kryo = ep.kryo
    register(kryo)
}

fun register(kryo: Kryo) {
    kryo.register(Task::class.java)
    kryo.register(IntArray::class.java)
    kryo.register(ByteArray::class.java)
    kryo.register(String::class.java)
    kryo.register(Array<IntArray>::class.java)
    kryo.references = false
//        kryo.register(BooleanArray::class.java)
//        kryo.instantiatorStrategy = StdInstantiatorStrategy()
}