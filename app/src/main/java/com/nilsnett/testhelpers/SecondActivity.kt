package com.nilsnett.testhelpers

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        Log.i("SecondActivity", "oncreate")
    }

    fun onOk(view: View) {
        this.setResult(Activity.RESULT_OK) // -1
        finish()
    }

    fun onCancel(view: View) {
        this.setResult(Activity.RESULT_CANCELED) // 0
        finish()
    }
}