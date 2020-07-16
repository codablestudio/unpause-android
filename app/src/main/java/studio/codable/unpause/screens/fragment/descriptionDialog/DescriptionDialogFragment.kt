package studio.codable.unpause.screens.fragment.descriptionDialog

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.save_dialog.*
import studio.codable.unpause.R
import studio.codable.unpause.utilities.LambdaNoArgumentsUnit
import studio.codable.unpause.utilities.LambdaStringToUnit


class DescriptionDialogFragment(private val description: String?) : DialogFragment() {

    private var dialogListenerOnSave: LambdaStringToUnit? = null
    private var dialogListenerOnCancel: LambdaNoArgumentsUnit? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.save_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        descriptionEditText.setText(description.orEmpty())

        saveDescriptionButton.setOnClickListener {
            dialogListenerOnSave?.invoke(descriptionEditText.text.toString())
            hideKeyboard()
            dismiss()
        }

        if (dialogListenerOnCancel == null) {
            cancelSaveDialogButton.visibility = View.GONE
            guideline_save_dialog.setGuidelinePercent(0f)
        } else {
            cancelSaveDialogButton.setOnClickListener {
                dialogListenerOnCancel?.invoke()
                dismiss()
            }
        }
    }

    fun setOnSaveListener(dialogListenerOnSave: LambdaStringToUnit) {
        this.dialogListenerOnSave = dialogListenerOnSave
    }

    fun setOnCancelListener(dialogListenerOnCancel: LambdaNoArgumentsUnit) {
        this.dialogListenerOnCancel = dialogListenerOnCancel
    }

    private fun hideKeyboard() {
        val inputManager: InputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            requireActivity().currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}