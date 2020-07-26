package com.urop.common

import java.util.*

fun miniTest() {
    val ARR_LENGTH = 100000
    val rep = 3
    val arr = IntArray(ARR_LENGTH)
    val r = Random()
    for (i in 0 until ARR_LENGTH) {
        arr[i] = r.nextInt(100)
    }
    repeat(rep) {
//        var barr = byteArrayOf()
//        var bb = ByteBuffer.wrap(barr)
//        println("Serial arr:\t\t" + measureTimeMillis { barr = arr.toBArr() })
//        var t = Task()
//        t.iArrData = barr
//        println("Serial task:\t" + measureTimeMillis { bb = t.toBB() })
//
//        println("deserial task:\t" + measureTimeMillis { t = bb.toTask() })
//        println("deserial task:\t" + measureTimeMillis { arr = t.iArrData.toIArr() })
    }
}