package studio.codable.unpause.base

import android.app.Dialog
import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import studio.codable.unpause.R
import timber.log.Timber

abstract class BaseFragment : Fragment() {

    private lateinit var loadingDialog: Dialog

    open fun showError(message: String?) {
        Timber.tag(this::class.java.simpleName).e(message)
        AlertDialog.Builder(requireContext()).setTitle("Error").setMessage(message).create().show()
    }

    open fun showMessage(message: String?) {
        Timber.tag(this::class.java.simpleName).d(message)
        Toast.makeText(
            context,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    open fun showLoading() {
        if (!::loadingDialog.isInitialized) {
            val builder = AlertDialog.Builder(context as Context)
            builder.setView(R.layout.dialog_loading)
            loadingDialog = builder.create()
            loadingDialog.setCancelable(false)
            loadingDialog.setCanceledOnTouchOutside(false)
        }
        loadingDialog.show()
    }

    open fun hideLoading() {
        if (::loadingDialog.isInitialized) {
            loadingDialog.dismiss()
        }
    }
}