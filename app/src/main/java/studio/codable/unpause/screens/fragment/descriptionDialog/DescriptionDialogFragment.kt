package studio.codable.unpause.screens.fragment.descriptionDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.save_dialog.*
import studio.codable.unpause.R
import studio.codable.unpause.utilities.LambdaStringToUnit
import studio.codable.unpause.utilities.LambdaNoArgumentsUnit

class DescriptionDialogFragment(private val description: String?) : DialogFragment() {

    private var dialogListenerOnSave: LambdaStringToUnit? = null
    private var dialogListenerOnCancel: LambdaNoArgumentsUnit? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.save_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        descriptionEditText.setText(description.orEmpty())

        saveDescriptionButton.setOnClickListener {
            dialogListenerOnSave?.invoke(descriptionEditText.text.toString())
            dismiss()
        }

        cancelSaveDialogButton.setOnClickListener {
            dialogListenerOnCancel?.invoke()
            dismiss()
        }
    }

    fun setOnSaveListener(dialogListenerOnSave: LambdaStringToUnit) {
        this.dialogListenerOnSave = dialogListenerOnSave
    }

    fun setOnCancelListener(dialogListenerOnCancel: LambdaNoArgumentsUnit) {
        this.dialogListenerOnCancel = dialogListenerOnCancel
    }
}