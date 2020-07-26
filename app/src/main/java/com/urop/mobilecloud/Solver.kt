package com.urop.mobilecloud

import com.urop.common.Task


class Solver {
//    var data: IntArray

    private val taskBuffer: MutableList<Task> = mutableListOf()


    fun addTask(task: Task) {
        taskBuffer.add(task)
    }

    fun work(): Task {
        val task = taskBuffer.removeAt(0)
        return work(task)
    }

    fun work(task: Task): Task {

        val tt = when (task.cmd) {
            "QSRT" -> task.apply(qsort)
            "MSRT" -> msort(task)
            "QUEEN" -> queens(task)
            "NOP" -> task
            else -> {
                task.strData = "CMD not understood!"
                return task
            }
        }
        Thread.sleep(10)
        return tt
    }

}
