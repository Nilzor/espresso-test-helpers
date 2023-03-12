package com.nilsnett.testhelpers

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.nilsnett.testhelpers.databinding.ActivityMainBinding
import com.nilsnett.testhelpers.navigation.ActivityDialogLauncher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var activityDialogLauncher: ActivityDialogLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // Handle result from navigateTo()
    }

    fun navigateTo() {
        activityLauncher.launch(Intent(this, SecondActivity::class.java))
    }


    suspend fun navigateTo() {
        val result = activityDialogLauncher.startForResult(Intent(this, SecondActivity::class.java))
        // Handle result
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