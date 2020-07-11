package com.urop.common

import com.google.gson.Gson

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
