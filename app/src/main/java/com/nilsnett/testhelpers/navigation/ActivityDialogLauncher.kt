package com.nilsnett.testhelpers.navigation

import android.content.Intent
import androidx.fragment.app.DialogFragment

interface ActivityDialogLauncher {
    /**
     * Launches an activity for result with the specified requestCode and asynchronously returns
     * the result as [ActivityResult] when the target activity finishes
     */
    suspend fun startForResult(intent: Intent, requestCode: Int): ActivityResult

    /**
     * Launches an activity with the parameters specified
     */
    fun start(intent: Intent)

    /**
     * Shows a dialog and returns when user has clicked an action button or dismissed the dialog
     */
    suspend fun showDialog(dialog: DialogFragment): ActivityResult
}