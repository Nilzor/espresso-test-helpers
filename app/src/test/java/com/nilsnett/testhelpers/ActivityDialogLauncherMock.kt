package com.nilsnett.testhelpers

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.nilsnett.testhelpers.navigation.ActivityDialogLauncher
import com.nilsnett.testhelpers.navigation.ActivityResult

/**
 * Mock ActivityDialogLauncher.
 */
class ActivityDialogLauncherMock : ActivityDialogLauncher {
    var lastStartForResult: StartArguments? = null
    var lastStartIntent: Intent? = null
    var lastDialogFragment: Fragment? = null
    var nextDialogResult: ActivityResult = ActivityResult(0, 0, Bundle())
    var nextActivityResult: ActivityResult = ActivityResult(0, 0, Bundle())

    override suspend fun startForResult(intent: Intent, requestCode: Int): ActivityResult {
        lastStartForResult = StartArguments(intent, requestCode)
        return nextActivityResult
    }

    override fun start(intent: Intent) {
        lastStartIntent = intent
    }

    override suspend fun showDialog(dialog: DialogFragment): ActivityResult {
        lastDialogFragment = dialog
        return nextDialogResult
    }

    override fun onDialogResult(activityResult: ActivityResult) {
        // noop
    }

    class StartArguments(val intent: Intent, val requestCode: Int)
}