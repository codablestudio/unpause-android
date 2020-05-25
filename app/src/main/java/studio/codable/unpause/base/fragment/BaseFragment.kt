package studio.codable.unpause.base.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.layout_toolbar.*
import studio.codable.unpause.R
import studio.codable.unpause.app.App
import timber.log.Timber

abstract class BaseFragment(private val hasDefaultToolbar: Boolean) : Fragment() {

    private lateinit var loadingDialog: Dialog

    private val navController by lazy { findNavController() }

    private val component = App.instance.applicationComponent

    init {
        inject()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar()
    }

    private fun inject() {
//     component.let {
//            when (this) {
//            is HomeFragment -> it.plusFragment().inject(this)
//        }
//     }
    }

    protected open fun initToolbar() {
        if (hasDefaultToolbar) {
            toolbar.setupWithNavController(navController)
        } else {
            toolbar?.apply { visibility = View.GONE }
        }
    }

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