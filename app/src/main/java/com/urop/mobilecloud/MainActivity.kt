package com.urop.mobilecloud

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.urop.common.Profiler.profiler
import com.urop.common.Task
import com.urop.common.toBB
import com.urop.common.toTask
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private var logString: String = ""
    private val webSocket = WebSocketClient(this)
    private val solver = Solver()

    var switchOffManually = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logAppend("Welcome to Mobile Cloud!")
        logAppend("Tap Connect to start ... ")
//        val ip =

        netSwitcher.setOnCheckedChangeListener { _, isChecked -> switchChecked(isChecked) }
        clearLogBotton.setOnClickListener { clearLog() }
        clearProfileBotton.setOnClickListener { clearProfile() }

        netSwitcher.performClick()
//        miniTest()
    }


    private fun switchChecked(isChecked: Boolean) {
        if (isChecked) {
            webSocket.connect("ws://192.168.10.143:9544")
            webSocket.sendTask(Task.Message("Hi, this is ${android.os.Build.MODEL}"))
            switchOffManually = false
        } else {
            webSocket.close(4321, "bye")
            switchOffManually = true
        }
    }

    private fun clearLog() {
        logString = ""
        logText.text = ""
    }

    fun clearProfile() {
        profiler.clear()
        logAppend("Profile cleared")
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

    fun msgParser(msg: ByteArray) {
//        logAppend("Get a msg")
        var t = Task()
        profiler.add("deserial") { t = msg.toTask() }
//        logAppend("byte to task took $dur")
        taskParser(t)
//        logAppend("Duration: ${duration}")
    }

    private fun taskParser(task: Task) {
        when (task.cmd) {
            "Message" -> {
                logAppend("Msg: ${task.id}")
            }
            "Profile" -> {
                webSocket.sendTask(Task.Message(profiler.dump()))
            }
            else -> {
                solver.addTask(task)
                var res = Task()
                val dur = profiler.add("useful work") { res = solver.work() }

                //            if(res.id[res.id.length-1] == '1'){
                if (res.id.slice(0..2).equals("[0,")) {
                    logAppend(
                        "${res.cmd} ${res.id} done: "
                                + dur + "ms"
                    )
                }
                webSocket.sendMessage(
                    profiler.add(
                        "serial", Task::toBB, res
                    )
                )
                //            val res = worker.result

                //            val sendMsg = res.task2json()
                //            logAppend("sending msg: $sendMsg")
            }
        }
    }

}

