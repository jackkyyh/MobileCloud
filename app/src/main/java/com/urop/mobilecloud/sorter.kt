package com.urop.mobilecloud


import com.urop.common.MSortTask
import com.urop.common.QSortTask
import com.urop.common.Task


val qsort: QSortTask.() -> Unit = {
    arr.sort()
}


fun msort(t: MSortTask): Task {
    val arr = t.arr

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

    t.arr = temp
//    profiler.add("deserial") { t.iArrData = temp }

    return t
}