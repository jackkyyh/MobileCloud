package com.urop.mobilecloud

import com.urop.common.Task
import com.urop.common.toBArr
import com.urop.common.toIArr


fun qsort(t: Task): Task {
//        data.sorted()
    t.data = t.data.toIArr().sortedArray().toBArr()
    return t
}

fun msort(t: Task): Task {
//        data.sorted()
    val data = t.data
    val a = data.toIArr()
    val size = a.size
    val mid = size / 2
//        val high = size

    val temp = IntArray(size)
    var i = 0
    var j = mid
    var k = 0
    while (i < mid && j < size) {
        temp[k++] = if (a[i] < a[j]) a[i++] else a[j++]
    }
    while (i < mid) temp[k++] = a[i++]
    while (j < size) temp[k++] = a[j++]

    t.data = temp.toBArr()
    return t
}