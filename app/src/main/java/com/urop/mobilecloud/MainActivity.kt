package com.urop.mobilecloud

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.urop.common.Message
import com.urop.common.Profile
import com.urop.common.Profile.profile
import com.urop.common.Task
import com.urop.server.Server
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


private val LOCALMODE = false

class MainActivity : AppCompatActivity() {

    private var logString = ""
    private val solver = Solver()


    private val ep: ClientEndPoint =
        if (LOCALMODE) LocalClientEndPoint() else KryoNetClientEndPoint()

    private var switchOffManually = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainActivity = this

        logAppend("Welcome to Mobile Cloud!")
        logAppend("Tap Connect to start ... ")

        if (LOCALMODE) {
            Server.main(arrayOf(true.toString()))
        }
//        val ip =

        netSwitcher.setOnCheckedChangeListener { _, isChecked -> switchChecked(isChecked) }
        clearLogBotton.setOnClickListener { clearLog() }
        clearProfileBotton.setOnClickListener { clearProfile() }

        netSwitcher.performClick()

//        miniTest()
    }


    private fun switchChecked(isChecked: Boolean) {
        if (isChecked) {
//            if(!ep.connect()){
//                retryNetSwitch()
//            }
            Thread {
                if (ep.connect()) {

                }
            }.start()
            ep.send(Message("Hi, this is ${Build.MODEL}"))
//            webSocket.connect("ws://192.168.10.143:9544")
//            webSocket.sendTask(Task.Message("Hi, this is ${android.os.Build.MODEL}"))
//            switchOffManually = false
        } else {
//            webSocket.close(4321, "bye")
//            switchOffManually = true
            ep.disconnect()
        }
    }

    private fun clearLog() {
        logString = ""
        logText.text = ""
    }

    private fun clearProfile() {
        profile.clear()
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
//        if (switchOffManually)
//            return

        if (!netSwitcher.isChecked) {
            return
        }
        runOnUiThread {
            netSwitcher.performClick()
        }
        Thread.sleep(5000)
        runOnUiThread {
            netSwitcher.performClick()
        }
//        switchOffManually = true
    }


    fun taskParser(task: Task) {
//        logAppend(task.id + " received ")
        when (task) {
            is Message -> {
                logAppend("Msg: ${task.msg}")
            }
            is Profile -> {
                ep.send(profile)
            }
            else -> {
//                logAppend("received" + task.id)
                solver.addTask(task)
                var res = Task()
                val dur = profile.add("useful") { res = solver.work() }

                //            if(res.id[res.id.length-1] == '1'){
//                if (res.id.slice(0..2) == "[0,") {
                    val name = res.javaClass.simpleName
                    logAppend(
                        "$name ${res.id} done: "
                                + dur + "ms"
                    )
//                }
                ep.send(res)
            }
        }
    }

    companion object {
        lateinit var mainActivity: MainActivity
    }

}

