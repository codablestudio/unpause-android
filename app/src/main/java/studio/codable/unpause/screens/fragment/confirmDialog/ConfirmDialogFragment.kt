package studio.codable.unpause.screens.fragment.confirmDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.confirm_dialog.*
import studio.codable.unpause.R
import studio.codable.unpause.utilities.LambdaNoArgumentsUnit

class ConfirmDialogFragment : DialogFragment() {

    private lateinit var dialogListenerOnYes: LambdaNoArgumentsUnit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.confirm_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        yes_button.setOnClickListener {
            dialogListenerOnYes.invoke()
            dismiss()
        }

        no_button.setOnClickListener {
            dismiss()
        }
    }

    fun addListener(dialogListenerOnYes: LambdaNoArgumentsUnit) {
        this.dialogListenerOnYes = dialogListenerOnYes
    }
}