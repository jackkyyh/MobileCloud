package com.urop.mobilecloud

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.urop.common.*
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.measureTimeMillis


class MainActivity : AppCompatActivity() {

    private var logString: String = ""
    private val webSocket = WebSocketClient(this)
    private val worker = Worker()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logAppend("Welcome to Mobile Cloud!")
        logAppend("Tap Connect to start ... ")
//        val ip =

        netSwitcher.setOnCheckedChangeListener { _, isChecked -> switchChecked(isChecked) }
        clearLogBotton.setOnClickListener { clearLog() }
    }


    private fun switchChecked(isChecked: Boolean) {
        if (isChecked) {
            webSocket.connect("ws://jackys-windows:9544")
//            webSocket.sendMessage(Task("Message", "Hi, server!"))
        } else {
            webSocket.close(4321, "bye")
        }
    }

    fun clearLog() {
        logString = ""
        logText.text = ""
    }

    fun logAppend(str: String) {
        runOnUiThread {
            val curTime = SimpleDateFormat("[HH:mm:ss:SSS] ", Locale.getDefault()).format(Date())
            logString = curTime + str + "\n" + logString
            logText.text = logString
        }
    }


    fun msgParser(msg: String) {
//        logAppend("receive msg: $msg")
        val task = msg.json2task()
        if (task.cmd == "Message") {
            logAppend("Msg: ${task.data}")
        } else {
            worker.addTask(task)
            var res = Task()
            val duration = measureTimeMillis { res = worker.work() }
            res.waitCount = duration.toInt()
            logAppend("${res.cmd} ${res.meta} done: " + duration + "ms")
//            val res = worker.result

//            val sendMsg = res.task2json()
//            logAppend("sending msg: $sendMsg")
            webSocket.sendMessage(res.task2json())
        }

    }

}

class Worker {
//    var data: IntArray

    private var taskBuffer: MutableList<Task> = mutableListOf()
//    var result: String? = null
//    @Volatile var result: Task? = null


    fun addTask(task: Task) {
//        val t: Task =
        taskBuffer.add(task)
    }


    fun work(): Task {
        val task = taskBuffer.removeAt(0)

//        val (cmd, data) = task
//        var resCmd = cmd

        val resData: String = when (task.cmd) {
            "QSRT" -> qsort(task.data)
            "MSRT" -> msort(task.data)
            "NOP" -> nop(task.data)
            else -> "CMD not understood!"
        }
        val tt = task
        tt.data = resData
//        Thread.sleep(100)
        return tt
    }

    private fun qsort(data: String): String {
//        data.sorted()
        return data.json2arr().sortedArray().arr2json()
    }

    private fun msort(data: String): String {
//        data.sorted()
        val a = data.json2arr()
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

        return temp.arr2json()
    }

    private fun nop(data: String): String {
//        val a = data.json2arr()
//        a[0] = 1    // avoid compiler optimization
//        return a.arr2json()
        return data
    }

}
