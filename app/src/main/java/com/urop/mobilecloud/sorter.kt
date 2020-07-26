package com.urop.mobilecloud


import com.urop.common.Task


val qsort: Task.() -> Unit = {
//    iArrData = iArrData.sortedArray()
    iArrData.sort()
}

fun msort(t: Task): Task {
    val arr = t.iArrData

//    profiler.add("serial") { arr = t.iArrData.toIArr() }

    val size = arr.size
    val temp = IntArray(size)

    val mid = size / 2
//        val high = size

    var i = 0
    var j = mid
    var k = 0
    while (i < mid && j < size) {
        temp[k++] = if (arr[i] < arr[j]) arr[i++] else arr[j++]
    }
    while (i < mid) temp[k++] = arr[i++]
    while (j < size) temp[k++] = arr[j++]

    t.iArrData = temp
//    profiler.add("deserial") { t.iArrData = temp }

    return t
}