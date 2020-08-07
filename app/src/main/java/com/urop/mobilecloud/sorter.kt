package com.urop.mobilecloud


import com.urop.common.MSortTask
import com.urop.common.QSortTask


val qsort: QSortTask.() -> Unit = {
//    println("qsort")
    arr = arr.sortedArray()
}

val msort: MSortTask.() -> Unit = {
//    println("msort")

    val size = arr.size
    val temp = IntArray(size)

    val mid = size / 2

    var i = 0
    var j = mid
    var k = 0
    while (i < mid && j < size) {
        temp[k++] = if (arr[i] < arr[j]) arr[i++] else arr[j++]
    }
    while (i < mid) temp[k++] = arr[i++]
    while (j < size) temp[k++] = arr[j++]

    arr = temp
}