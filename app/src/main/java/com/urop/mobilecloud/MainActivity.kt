package com.urop.mobilecloud

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.urop.common.Task
import com.urop.common.toBAarr
import com.urop.common.toIArr
import com.urop.common.toTask
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.measureTimeMillis


class MainActivity : AppCompatActivity() {

    private var logString: String = ""
    private val webSocket = WebSocketClient(this)
    private val worker = Worker()

    var switchOffManually = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logAppend("Welcome to Mobile Cloud!")
        logAppend("Tap Connect to start ... ")
//        val ip =

        netSwitcher.setOnCheckedChangeListener { _, isChecked -> switchChecked(isChecked) }
        clearLogBotton.setOnClickListener { clearLog() }

        netSwitcher.performClick()
    }


    private fun switchChecked(isChecked: Boolean) {
        if (isChecked) {
            webSocket.connect("ws://jackys-windows:9544")
            webSocket.sendMessage(Task.Greeting("Hi, this is ${android.os.Build.MODEL}"))
            switchOffManually = false
        } else {
            webSocket.close(4321, "bye")
            switchOffManually = true
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

    fun retryNetSwitch() {
        if (switchOffManually)
            return

        runOnUiThread {
            netSwitcher.performClick()
        }
        Thread.sleep(5000)
        runOnUiThread {
            netSwitcher.performClick()
        }
        switchOffManually = true
    }


    var duration: Long = 0

    fun msgParser(msg: ByteArray) {
        taskParser(msg.toTask())
//        logAppend("Duration: ${duration}")
    }

    fun taskParser(task: Task) {
        if (task.cmd == "Message") {
            logAppend("Msg: ${task.meta}")
        } else {
            worker.addTask(task)
            var res = Task()
            val duration = measureTimeMillis {
                res = worker.work()
            }
            res.waitCount = duration.toInt()
            webSocket.sendMessage(res)
            logAppend("${res.cmd} [${res.meta}] done: " + duration + "ms")
//            val res = worker.result

//            val sendMsg = res.task2json()
//            logAppend("sending msg: $sendMsg")
        }
    }

}

class Worker {
//    var data: IntArray

    var taskBuffer: MutableList<Task> = mutableListOf()
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

        val resData = when (task.cmd) {
            "QSRT" -> qsort(task.data)
            "MSRT" -> msort(task.data)
            "NOP" -> nop(task.data)
            else -> "CMD not understood!".toByteArray()
        }
        val tt = task
        tt.data = resData
//        Thread.sleep(100)
        return tt
    }

    fun qsort(data: ByteArray): ByteArray {
//        data.sorted()
        return data.toIArr().sortedArray().toBAarr()
    }

    fun msort(data: ByteArray): ByteArray {
//        data.sorted()
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

        return temp.toBAarr()
    }

    fun nop(data: ByteArray): ByteArray {
        return data
    }

}
