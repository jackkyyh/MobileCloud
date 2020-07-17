package com.urop.mobilecloud

import com.urop.common.Task


class Solver {
//    var data: IntArray

    var taskBuffer: MutableList<Task> = mutableListOf()


    fun addTask(task: Task) {
//        val t: Task =
        taskBuffer.add(task)
    }


    fun work(): Task {
        val task = taskBuffer.removeAt(0)

        val tt = when (task.cmd) {
            "QSRT" -> qsort(task)
            "MSRT" -> msort(task)
            "QUEEN" -> queens(task)
            "NOP" -> nop(task)
            else -> {
                task.meta = "CMD not understood!"
                return task
            }
        }
        return tt
    }
}

fun nop(t: Task): Task {
    return t
}