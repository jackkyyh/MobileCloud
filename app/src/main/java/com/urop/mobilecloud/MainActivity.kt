package com.urop.mobilecloud

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.urop.common.Profiler.profiler
import com.urop.common.Task
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private var logString: String = ""

    //    private val webSocket = WebSocketClient(this)
    private val kn = KryoNetClient(this)
    private val solver = Solver()

    var switchOffManually = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        kn.start()

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
            Thread {
                kn.connect(5000, "192.168.10.143", 9544, 9566)
                kn.sendTCP(Task.Message("Hi, this is ${android.os.Build.MODEL}"))

            }.start()

//            webSocket.connect("ws://192.168.10.143:9544")
//            webSocket.sendTask(Task.Message("Hi, this is ${android.os.Build.MODEL}"))
            switchOffManually = false
        } else {
//            webSocket.close(4321, "bye")
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

        if (!netSwitcher.isChecked) {
            return
        }
        runOnUiThread {
            netSwitcher.performClick()
//            netSwitcher.
        }
        Thread.sleep(5000)
        runOnUiThread {
            netSwitcher.performClick()
        }
        switchOffManually = true
    }


    fun taskParser(task: Task) {
        when (task.cmd) {
            "Message" -> {
                logAppend("Msg: ${task.id}")
            }
            "Profile" -> {
                kn.sendTCP(Task.Message(profiler.dump()))
            }
            else -> {
                solver.addTask(task)
                var res = Task()
                val dur = profiler.add("useful work") { res = solver.work() }

                //            if(res.id[res.id.length-1] == '1'){
                if (res.id.slice(0..2) == "[0,") {
                    logAppend(
                        "${res.cmd} ${res.id} done: "
                                + dur + "ms"
                    )
                }
                kn.sendTCP(res)
            }
        }
    }

}

