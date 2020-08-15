package com.urop.mobilecloud

import com.urop.common.*


class Solver {

    private val taskBuffer: MutableList<Task> = mutableListOf()


    fun addTask(task: Task) {
        taskBuffer.add(task)
    }

    fun work(): Task {
        val task = taskBuffer.removeAt(0)
        return work(task)
    }

    fun work(task: Task): Task {

        Thread.sleep(10)
        return when (task) {
            is QSortTask -> task.apply(qsort)
            is MSortTask -> task.apply(msort)
            is NQueenTask -> task.apply(nqueens)
            is FactorizationTask -> task.apply(factorize)
            is NOPTask -> task
            else -> Message("CMD not understood!")
        }
    }

}
