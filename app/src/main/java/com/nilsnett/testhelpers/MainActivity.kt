package com.nilsnett.testhelpers

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.nilsnett.testhelpers.databinding.ActivityMainBinding
import com.nilsnett.testhelpers.navigation.ActivityDialogLauncher
import com.nilsnett.testhelpers.navigation.AndroidActivityDialogLauncher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.*
import kotlin.coroutines.resumeWithException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var activityDialogLauncher: ActivityDialogLauncher  = AndroidActivityDialogLauncher()

    lateinit var rotationId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rotationId = savedInstanceState?.getString(AndroidActivityDialogLauncher.ROTATION_ID_KEY, null)
            ?: UUID.randomUUID().toString()
        (activityDialogLauncher as AndroidActivityDialogLauncher).onCreate(this, rotationId)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(AndroidActivityDialogLauncher.ROTATION_ID_KEY, rotationId)
        super.onSaveInstanceState(outState)
    }

    val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // Handle result from navigateTo()
    }

    fun navigateTo(view: View) {
        lifecycleScope.launch { navigateTo() }
    }

    suspend fun navigateTo() {
        val result = activityDialogLauncher.startForResult(Intent(this, SecondActivity::class.java) ,0)
        Log.i("MainActivity", "Got result: ${result.resultCode}")
        val textRes = if (result.resultCode == Activity.RESULT_OK) "OK" else "Cancel"
        Toast.makeText(this, "Got result: $textRes", Toast.LENGTH_SHORT).show()
    }

    fun startaa(view: View) {
        CoroutineScope(Dispatchers.Main).launch {
            val str = doStuff()
            Log.d("TAG", "Stuff: $str")
        }
    }

    fun start(view: View) {
        val scope = CoroutineScope(Dispatchers.Main)
        val job = scope.launch {
            try {
                val str = doStuff()
                Log.d("TAG", "Stuff: $str")
            } catch (ex: Exception) {
                Log.w("TAG", "eeeeh. Uncool stuff happened: $ex")
            }
        }
        scope.launch {
            for (i in 1..100) {
                Log.i("TAG", "working... $i")
                delay (1000)
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            delay(100)
            Log.d("TAG", "Coroutine state: isActive=${job.isActive}, isCancelled=${job.isCancelled}, isCompleteds=${job.isCompleted}\nScope state:${scope.isActive}")
        }

    }

    private suspend fun doStuff(): String {
        return suspendCancellableCoroutine {
            it.resumeWithException(IllegalStateException("Resuming with exc"))
            //throw (IllegalStateException("Im throwing stuff"))
        }
    }


    fun loadStuff(view: View) {
        reset()
        binding.progressbar.visibility = View.VISIBLE
        lifecycleScope.launch {
            delay (2000)
            binding.progressbar.visibility = View.GONE
            binding.contentTextView.visibility = View.VISIBLE
            binding.contentTextView.text = "Content"
            binding.loadingDoneIndicator.visibility = View.VISIBLE
        }
    }

    fun reset() {
        binding.progressbar.visibility = View.GONE
        binding.contentTextView.visibility = View.GONE
        binding.loadingDoneIndicator.visibility = View.GONE
        binding.contentTextView.text = ""
    }
}