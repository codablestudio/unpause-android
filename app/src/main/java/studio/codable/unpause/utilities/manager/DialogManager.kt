package studio.codable.unpause.utilities.manager

import androidx.core.util.Pair
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.datepicker.MaterialDatePicker
import studio.codable.unpause.R
import studio.codable.unpause.base.activity.BaseActivity
import studio.codable.unpause.screens.fragment.confirmDialog.ConfirmDialogFragment
import studio.codable.unpause.screens.fragment.datePicker.DatePickerFragment
import studio.codable.unpause.screens.fragment.datePicker.DatePickerListener
import studio.codable.unpause.screens.fragment.descriptionDialog.DescriptionDialogFragment
import studio.codable.unpause.screens.fragment.workingTimeWarning.WorkingTimeWarningFragment
import studio.codable.unpause.utilities.LambdaDoubleIntToUnit
import studio.codable.unpause.utilities.LambdaNoArgumentsUnit
import studio.codable.unpause.utilities.LambdaStringToUnit
import studio.codable.unpause.screens.fragment.timePicker.TimePickerFragment
import java.util.*

class DialogManager(private val context: BaseActivity) {
    private lateinit var descriptionDialogFragment: DescriptionDialogFragment
    private lateinit var workingTimeWarningFragment: WorkingTimeWarningFragment
    private val datePickerFragment: DatePickerFragment by lazy { DatePickerFragment() }
    private lateinit var timePickerFragment: TimePickerFragment
    private val confirmDialogFragment: ConfirmDialogFragment by lazy { ConfirmDialogFragment() }

    fun openDescriptionDialog(desctiption: String?, dialogListenerOnSave: LambdaStringToUnit, dialogListenerOnCancel: LambdaNoArgumentsUnit) {
        descriptionDialogFragment = DescriptionDialogFragment(desctiption).apply {
            setOnSaveListener(dialogListenerOnSave)
            setOnCancelListener(dialogListenerOnCancel)
            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.full_screen_dialog)
        }
        showFullscreenDialog(descriptionDialogFragment)
    }

    fun openWorkingTimeDialog(
            arrivalTime: Date,
            exitTime: Date,
            isArrivalDateEditable: Boolean,
            dialogManager: DialogManager,
            dialogListener: WorkingTimeWarningFragment.DialogListener
    ) {
        workingTimeWarningFragment =
            WorkingTimeWarningFragment(arrivalTime, exitTime, isArrivalDateEditable, dialogManager)
        workingTimeWarningFragment.addListener(dialogListener)
        showFullscreenDialog(workingTimeWarningFragment)
    }

    fun openDatePickerDialog(datePickerListener: DatePickerListener) {
        datePickerFragment.setListener(datePickerListener)
        datePickerFragment.show(context.supportFragmentManager, "Date picker fragment")
    }

    fun openTimePickerDialog(hour: Int, minute: Int, timePickerListener: LambdaDoubleIntToUnit) {
        timePickerFragment = TimePickerFragment(hour, minute)
        timePickerFragment.setListener(timePickerListener)
        timePickerFragment.show(context.supportFragmentManager, "Time picker fragment")
    }

    fun openConfirmDialog(confirmDialogListener: LambdaNoArgumentsUnit) {
        confirmDialogFragment?.addListener(confirmDialogListener)
        confirmDialogFragment?.show(context.supportFragmentManager, "Confirm dialog fragment")
    }

    fun openDateRangePickerDialog(
        from: Long? = Calendar.getInstance().timeInMillis,
        to: Long? = Calendar.getInstance().timeInMillis,
        listener: (Pair<Long, Long>) -> Unit
    ) {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        builder.setSelection(Pair(from, to))
        builder.build().apply {
            addOnPositiveButtonClickListener { listener(it) }
            addOnNegativeButtonClickListener { dismiss() }
            addOnCancelListener { dismiss() }
        }.show(context.supportFragmentManager, "Date range picker fragment")
    }

    private fun showFullscreenDialog(dialogFragment: DialogFragment) {
        val transaction = context.supportFragmentManager.beginTransaction()
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction
                .add(android.R.id.content, dialogFragment)
                .addToBackStack(null)
                .commit()
    }

}