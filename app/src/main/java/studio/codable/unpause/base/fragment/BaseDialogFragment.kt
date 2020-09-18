package studio.codable.unpause.base.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import studio.codable.unpause.R
import timber.log.Timber

abstract class BaseDialogFragment(private val isFullScreen: Boolean) : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isFullScreen) {
            setStyle(STYLE_NO_TITLE, R.style.full_screen_dialog)
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        //custom animations
        if (isFullScreen) {
            requireDialog().window?.setWindowAnimations(R.style.DialogAnimation)
        }
    }

    open fun showMessage(message: String?) {
        Timber.tag(this::class.java.simpleName).d(message)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}