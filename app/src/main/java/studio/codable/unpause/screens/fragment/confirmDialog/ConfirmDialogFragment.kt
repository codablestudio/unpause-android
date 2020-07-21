package studio.codable.unpause.screens.fragment.confirmDialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import studio.codable.unpause.R
import studio.codable.unpause.utilities.LambdaNoArgumentsUnit

class ConfirmDialogFragment(private val message : String) : DialogFragment() {

    private lateinit var dialogListenerOnYes: LambdaNoArgumentsUnit

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(message)
                .setPositiveButton(R.string.yes
                ) { _, _ ->
                    dialogListenerOnYes.invoke()
                    dismiss()
                }
                .setNegativeButton(R.string.no
                ) { _, _ ->
                    dismiss()
                }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun addListener(dialogListenerOnYes: LambdaNoArgumentsUnit) {
        this.dialogListenerOnYes = dialogListenerOnYes
    }
}