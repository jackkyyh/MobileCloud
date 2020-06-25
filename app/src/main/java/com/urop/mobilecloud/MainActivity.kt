package com.urop.mobilecloud

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.measureTimeMillis


class MainActivity : AppCompatActivity() {

    private var logString: String = ""
    private val webSocket = WebSocketClient(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logAppend("Welcome to Mobile Cloud!")
        logAppend("Tap Connect to start ... ")

        netSwitcher.setOnCheckedChangeListener { _, isChecked -> switchChecked(isChecked) }
    }


    fun switchChecked(isChecked: Boolean){
        if(isChecked){
            logAppend("Connection starts")
            request()
        }
        else{
            logAppend("Connection closed")
            webSocket.close(4321, "bye")
        }
    }

    fun request() {

        webSocket.connect("ws://jackys-windows:9544")
        webSocket.sendMessage("Hello")
    }


    fun logAppend(str: String) {
        val curTime = SimpleDateFormat("[HH:mm:ss:SSS] ", Locale.getDefault()).format(Date())
        logString = curTime + str + "\n" + logString
        runOnUiThread {
            logText.text = logString
        }
    }

    val gson = Gson()
    fun msgParser(msg: String) {
        val header = msg.slice(0..3)
        val body = msg.slice(6 until msg.length)
        when (header) {
            "Msag" -> logAppend(msg)
            "Data" -> parseData(body)
        }

    }

    fun parseData(strData: String) {
        val listData = gson.fromJson(strData, IntArray::class.java)
        logAppend(listData.size.toString() + " ints received")

        val worker = Worker(listData)
        val duration = measureTimeMillis { worker.work() }
        logAppend("Work done. Duration: " + duration + "ms")

        logAppend(worker.result.toString())


        webSocket.sendMessage(gson.toJson(worker.result))
    }


}

class Worker(var data: IntArray, var task: String = "SORT") {
    var result: List<Int>? = null
    fun work() {
        result = data.sorted()
    }
}