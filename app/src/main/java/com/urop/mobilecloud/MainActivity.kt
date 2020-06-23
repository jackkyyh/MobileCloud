package com.urop.mobilecloud

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.measureTimeMillis


class MainActivity : AppCompatActivity() {
//
//    val logger = MutableLiveData<String>()
//
//    init {
//        logger.value = ""
//    }
private var logString: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logAppend("Welcome to Mobile Cloud!")
        logAppend("Tap Connect to join us ... ")

        netSwitcher.setOnCheckedChangeListener {_, isChecked -> switchChecked(isChecked) }
    }


    fun switchChecked(isChecked: Boolean){
        if(isChecked){
            logAppend("Connection starts")
            request()
        }
        else{
            logAppend("Connection closed")
        }
    }

    fun request(){
        MarsApi.retrofitService.getProperties().enqueue(
            object: Callback<List<Int>> {
                override fun onFailure(call: Call<List<Int>>, t: Throwable) {
                    logAppend("Request failed:" + t.message)
                }

                override fun onResponse(call: Call<List<Int>>, response: Response<List<Int>>) {
                    logAppend("Request succeeded. Calculating... ")

                    val l = response.body()!!
                    logAppend("number of elements: " + l.size)
                    var sorted: List<Int> = emptyList()
                    val duration = measureTimeMillis {sorted = l.sorted()}
                    logAppend("Finished. Duration: " + duration + "ms")

                    submit(sorted)

                }
            })
    }

    fun submit(l: List<Int>){
        MarsApi.retrofitService.postProperties(l).enqueue(
            object: Callback<Int>{
                override fun onFailure(call: Call<Int>, t: Throwable) {
                    logAppend("Submission failed: " + t.message)
                }

                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                    logAppend("Submitted.")
                }

            }
        )
    }


    fun logAppend(str: String) {
        val curTime = SimpleDateFormat("[HH:mm:ss:SSS] ", Locale.getDefault()).format(Date())
        logString = curTime + str + "\n" + logString
        logText.text = logString
    }
}