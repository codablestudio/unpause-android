package studio.codable.unpause.screens.fragment.descriptionDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.save_dialog.*
import studio.codable.unpause.R

class DescriptionDialogFragment(private val desctiption: String?) : DialogFragment() {

    private lateinit var dialogListener: DialogListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.save_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (desctiption != null){
            descriptionEditText.setText(desctiption)
        }

        saveDescriptionButton.setOnClickListener {
            dialogListener.onSave(descriptionEditText.text.toString())
            dismiss()
        }

        cancelSaveDialogButton.setOnClickListener {
            dialogListener.onCancel()
            dismiss()
        }
    }

    fun addListener(dialogListener: DialogListener) {
        this.dialogListener = dialogListener
    }

    interface DialogListener {
        fun onSave(description: String)
        fun onCancel()
    }
}