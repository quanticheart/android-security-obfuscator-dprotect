package com.quanticheart.security

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var count = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //0
        preferences.apply {
            putString("SENHA", "SENHA DO USUARIO@123456")
            putModel("user", User())
        }

        //1
        database.executeInsert(
            TABLE_LOGIN,
            LOG to "scoot",
            ID_USER to "scoot@gmail.com",
        )

        //2
        log {
            Log.e("LOG", "MEU LOG $count")
            count++
        }

        //3
        val myHardCodeToken = "ThisTokenIsVerySecret"

        //4
        val gradleString1 = getString(R.string.MY_PROVIDER)
        val gradleString2 = BuildConfig.URL
        val gradleString3 = BuildConfig.TOKEN2

        log("$myHardCodeToken : $gradleString1: $gradleString2: $gradleString3")
        //5
        findViewById<TextView>(R.id.textHello)?.apply {
            text = CppKeys.user()
        }
    }

    fun log(msg: String) {
        if (BuildConfig.DEBUG)
            Log.e("test", msg)
    }
}