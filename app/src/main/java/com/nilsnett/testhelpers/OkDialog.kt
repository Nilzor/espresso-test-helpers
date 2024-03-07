package com.nilsnett.testhelpers

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class OkDialog : DialogFragment() {
    companion object {
        private const val TITLE = "title"
        private const val MESSAGE = "message"

        fun newInstance(
            title: String,
            message: String,
        ): OkDialog {
            val fragment = OkDialog()
            fragment.requireArguments().also {
                it.putString(MESSAGE, message)
                it.putString(TITLE, title)
            }

            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val arguments = requireArguments()
        val dialogBuilder = AlertDialog.Builder(requireActivity())
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(arguments.getInt(TITLE))

        val message = arguments.getString(MESSAGE)
        dialogBuilder.setMessage(message)

        dialogBuilder.setNeutralButton("OK") { _, _ ->
           // sendDialogResult(Activity.RESULT_OK) // todo
        }
        return dialogBuilder.create()
    }
}