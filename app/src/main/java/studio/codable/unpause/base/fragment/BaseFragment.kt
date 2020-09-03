package studio.codable.unpause.base.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.layout_toolbar.*
import studio.codable.unpause.R
import studio.codable.unpause.app.App
import studio.codable.unpause.screens.SharedViewModel
import studio.codable.unpause.screens.fragment.HomeFragment
import studio.codable.unpause.screens.fragment.locations.MapFragment
import studio.codable.unpause.utilities.Event
import studio.codable.unpause.utilities.navigation.NavCommand
import timber.log.Timber

abstract class BaseFragment(private val hasDefaultToolbar: Boolean) : Fragment() {

    private lateinit var loadingDialog: Dialog

    private val navController by lazy { findNavController() }

    private val component = App.instance.applicationComponent

    protected val svm: SharedViewModel by activityViewModels()


    init {
        inject()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        svm.navCommand.observe(viewLifecycleOwner, Observer {
            val content: NavCommand? = it.getContentIfNotHandled()
            try {
                content?.let { command ->
                    when (command) {
                        is NavCommand.To -> {
                            Timber.tag(this::class.java.simpleName)
                                .i("Navigating from ${navController.currentDestination}")
                            Timber.tag(this::class.java.simpleName)
                                .i("with action: ${command.directions.actionId}")
                            Timber.tag(this::class.java.simpleName)
                                .i("with arguments: ${command.directions.arguments}")
                            navController.navigate(command.directions)
                            Timber.tag(this::class.java.simpleName)
                                .i("Navigated to: ${navController.currentDestination}")
                        }
                        is NavCommand.ToRoot -> TODO("not implemented")
                        is NavCommand.Up -> {
                            Timber.tag(this::class.java.simpleName)
                                .i("Navigating *UP* from ${navController.currentDestination}")
                            navController.navigateUp()
                            Timber.tag(this::class.java.simpleName)
                                .i("Navigated *UP* to ${navController.currentDestination}")
                        }
                        is NavCommand.UpTo -> TODO("not implemented")
                    }
                }
            } catch (exc: Exception) {
                Timber.e(exc, "Failed to navigate $content")
            }
        })

        initToolbar()
    }

    private fun inject() {
     component.let {
            when (this) {
            is HomeFragment -> it.plusFragment().inject(this)
            is MapFragment -> it.plusFragment().inject(this)
        }
     }
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

    open fun defaultHandleLoading(shouldShowLoading: Event<Boolean>) {
        shouldShowLoading.getContentIfNotHandled()?.let {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        }
    }

    protected open fun showSoftwareKeyboard(showKeyboard: Boolean) {
        val inputManager: InputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            requireActivity().currentFocus?.windowToken,
            if (showKeyboard) InputMethodManager.SHOW_FORCED else InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}