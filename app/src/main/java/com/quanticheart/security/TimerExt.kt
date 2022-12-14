package com.quanticheart.security

import android.os.Handler
import android.os.Looper
import java.util.*

//
// Created by Jonn Alves on 18/10/22.
//
private var timerSys: Timer? = null
private val timerHandler = Handler(Looper.getMainLooper())

fun log(callback: () -> Unit) {
    timerSys = Timer()
    timerSys?.schedule(object : TimerTask() {
        override fun run() {
            timerHandler.post {
                callback()
            }
        }
    }, 0, 5000)
}