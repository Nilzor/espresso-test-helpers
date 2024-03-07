package com.nilsnett.testhelpers.navigation

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import java.lang.ref.WeakReference
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * A coroutine wrapper for launching activities and dialogs.
 * Supports device rotation and nested calls
 * LIMITATIONS:
 * 1: [startForResult] must be called from a coroutine that outlives an activity/fragment rotation.
 * 2: Only one simultaneous [startForResult] per activity class
 * 3: Only one simulatenous dialog through [showDialog]
 */
class AndroidActivityDialogLauncher : ActivityDialogLauncher,
    ActivityResultCallback<androidx.activity.result.ActivityResult> {

    private class RequestKeyContinuationPair(
        val requestCode: Int?,
        val continuation: Continuation<ActivityResult>
    )

    companion object {
        private var DIALOG_REQ_CODE = 1 // Increase by 1 each time a new dialog is shown

        // Reference to active dialog continuation. Needs to be global for device rotation support
        private var dialogContMap = mutableMapOf<Int, RequestKeyContinuationPair>()

        // State variable keeping reference to whatever the activities we're waiting for result for
        // If we have a chain of activities starting for results, there might be more than one
        private var activityForResultContinuation = mutableMapOf<String, RequestKeyContinuationPair>()

    }

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private lateinit var fragmentManager: WeakReference<FragmentManager>
    private lateinit var launcherId: String

    /**
     * @param activityId : An ID that survives configuration change (rotation) but differ between two
     * instances of the same class in the task backstack.
     */
    fun onCreate(activity: AppCompatActivity, activityId: String) {
        this.fragmentManager = WeakReference(activity.supportFragmentManager)
        launcherId = activityId
        resultLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            this
        )
    }

    override suspend fun startForResult(intent: Intent, requestCode: Int): ActivityResult {
        resultLauncher.launch(intent)
        return suspendCoroutine { cont ->
            activityForResultContinuation[launcherId] = RequestKeyContinuationPair(requestCode, cont)
        }
    }

    override fun start(intent: Intent) {
        resultLauncher.launch(intent)
    }

    override fun onActivityResult(result: androidx.activity.result.ActivityResult?) {
        val call = activityForResultContinuation[launcherId] ?: run {
            return@onActivityResult
        }
        activityForResultContinuation.remove(launcherId)
        result?.let { res ->
            val bundle = res.data?.getExtras()
            val actResult = ActivityResult(0, res.resultCode, bundle)
            call.continuation.resume(actResult)
        }
    }

    override suspend fun showDialog(dialog: DialogFragment): ActivityResult {
        val requestCodeForThisDialog = DIALOG_REQ_CODE
        DIALOG_REQ_CODE++

        val fragManager = fragmentManager.get() ?: throw IllegalStateException("No fragment manager")
        getReplacingDialogTransaction(fragManager, "DIALOG_TAG")
            .add(dialog, "DIALOG_TAG")
            .commitAllowingStateLoss()

        return suspendCoroutine { cont ->
            dialogContMap[requestCodeForThisDialog] = RequestKeyContinuationPair(requestCodeForThisDialog, cont)
        }
    }

    fun onDialogResult(activityResult: ActivityResult) {
        dialogContMap[activityResult.requestCode]?.continuation?.resume(activityResult)
    }

    private fun getReplacingDialogTransaction(
        fragmentManager: FragmentManager,
        dialogTag: String
    ): FragmentTransaction {
        var ft = fragmentManager.beginTransaction()
        ft.setReorderingAllowed(true)
        val prev = fragmentManager.findFragmentByTag(dialogTag)
        if (prev != null) {
            if (prev is DialogFragment) {
                // Need to dismiss it ot avoid invisible dialog on the backstack
                prev.dismiss()
            } else {
                ft.remove(prev)
                ft.commitAllowingStateLoss()
                ft = fragmentManager.beginTransaction()
            }
        }
        ft.addToBackStack(null)
        return ft
    }
}
