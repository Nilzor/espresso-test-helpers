package com.nilsnett.testhelpers.navigation

import android.content.Intent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AndroidActivityDialogLauncher : ActivityDialogLauncher, ActivityResultCallback<androidx.activity.result.ActivityResult> {
    companion object {
        private const val DIALOG_REQ_CODE = 431582
    }

    private lateinit var resultCaller: ActivityResultCaller
    private lateinit var fragmentManager: FragmentManager


    // State variable keeping reference to whatever single activity we're waiting for result for
    private var activityForResultContinuation: Pair<Int, Continuation<ActivityResult>>?  = null
    private var dialogForResultContinuation: Continuation<ActivityResult>? = null
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    fun onCreate(resultCaller: ActivityResultCaller, fragmentManager: FragmentManager) {
        this.resultCaller = resultCaller
        this.fragmentManager = fragmentManager
        resultLauncher = this.resultCaller.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            this
        )
    }

    override suspend fun startForResult(intent: Intent, requestCode: Int): ActivityResult {
        resultLauncher.launch(intent)
        return suspendCoroutine { cont ->
            this.activityForResultContinuation = Pair(requestCode, cont)
        }
    }

    override fun start(intent: Intent) {
        resultLauncher.launch(intent)
    }

    // denne trengs ja
    override fun onActivityResult(result: androidx.activity.result.ActivityResult?) {
        activityForResultContinuation?.let {
            result?.let { res ->
                val bundle = res.data?.getExtras()
                it.second.resume(ActivityResult(0, res.resultCode, bundle))
            }
            activityForResultContinuation = null
        }
    }

    override suspend fun showDialog(dialog: DialogFragment): ActivityResult {
        if (dialogForResultContinuation != null) {
            throw IllegalStateException("Dialog already showing")
        }
        return suspendCoroutine { cont ->
            dialogForResultContinuation = cont
            DialogUtils.showDialog(fragmentManager, dialog, DIALOG_REQ_CODE, true, logger)
        }
    }

    override fun onDialogResult(activityResult: ActivityResult) {
        if (activityResult.requestCode == DIALOG_REQ_CODE) {
            dialogForResultContinuation?.let { cont ->
                dialogForResultContinuation = null
                cont.resume(activityResult)
            }
        }
    }
}