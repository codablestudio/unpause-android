package studio.codable.unpause.utilities.manager

import android.app.Dialog
import androidx.fragment.app.DialogFragment
import com.google.firebase.Timestamp
import studio.codable.unpause.R
import studio.codable.unpause.base.activity.BaseActivity
import studio.codable.unpause.screens.fragment.datePicker.DatePickerFragment
import studio.codable.unpause.screens.fragment.descriptionDialog.DescriptionDialogFragment
import studio.codable.unpause.screens.fragment.workingTimeWarning.WorkingTimeWarningFragment
import studio.codable.unpause.view.timePicker.TimePickerFragment
import java.util.*

class DialogManager(private val context: BaseActivity) {
    private lateinit var descriptionDialogFragment: DescriptionDialogFragment
    private lateinit var workingTimeWarningFragment: WorkingTimeWarningFragment
    private val datePickerFragment: DatePickerFragment by lazy { DatePickerFragment() }
    private lateinit var timePickerFragment: TimePickerFragment
//    private var confirmDialogFragment: ConfirmDialogFragment? = null
//    private var shiftSchedulerDialog: ShiftSchedulerDialog? = null

    fun openDescriptionDialog(desctiption: String?, dialogListener: DescriptionDialogFragment.DialogListener) {
        descriptionDialogFragment = DescriptionDialogFragment(desctiption)
        descriptionDialogFragment?.addListener(dialogListener)
        descriptionDialogFragment?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.full_screen_dialog)
        descriptionDialogFragment?.show(context.supportFragmentManager, "Save dialog fragment")
    }

    fun openWorkingTimeDialog(
        arrivalTime: Date,
        exitTime: Date,
        editArrivalDate: Boolean,
        dialogManager: DialogManager,
        dialogListener: WorkingTimeWarningFragment.DialogListener
    ) {
        workingTimeWarningFragment =
            WorkingTimeWarningFragment(arrivalTime, exitTime, editArrivalDate, dialogManager)
        workingTimeWarningFragment?.addListener(dialogListener)
        workingTimeWarningFragment?.show(context.supportFragmentManager, "Working time warning fragment")
    }

    fun openDatePickerDialog(datePickerListener: DatePickerFragment.DatePickerListener) {
        datePickerFragment?.addListener(datePickerListener)
        datePickerFragment?.show(context.supportFragmentManager, "Date picker fragment")
    }

    fun openTimePickerDialog(hour: Int?, minute: Int?, timePickerListener: TimePickerFragment.TimePickerListener) {
        timePickerFragment = TimePickerFragment(hour, minute)
        timePickerFragment?.addListener(timePickerListener)
        timePickerFragment?.show(context.supportFragmentManager, "Time picker fragment")
    }
//
//    fun openConfirmDialog(confirmDialogListener: ConfirmDialogFragment.DialogListener) {
//        confirmDialogFragment = ConfirmDialogFragment()
//        confirmDialogFragment?.addListener(confirmDialogListener)
//        confirmDialogFragment?.show(context.supportFragmentManager, "Confirm dialog fragment")
//    }
//
//    fun openShiftScheduleDialog(
//        startHour: Int,
//        startMinute: Int,
//        endHour: Int,
//        endMinute: Int,
//        dialogManager: DialogManager,
//        shiftSchedulerListener: ShiftSchedulerDialog.DialogListener
//    ) {
//        shiftSchedulerDialog = ShiftSchedulerDialog(startHour, startMinute, endHour, endMinute, dialogManager)
//        shiftSchedulerDialog?.addListener(shiftSchedulerListener)
//        shiftSchedulerDialog?.show(context.supportFragmentManager, "ShiftScheduler dialog fragment")
//    }
//
//    fun openOptionPickerDialog(option1: String, option2: String, optionPickerListener: OptionPickerListener) {
//        val dialog = Dialog(context)
//        dialog.setContentView(R.layout.option_picker)
//
//        dialog.option1TextView.text = option1
//        dialog.option2TextView.text = option2
//
//        dialog.option1Button.setOnClickListener {
//            optionPickerListener.onOption1()
//            dialog.dismiss()
//        }
//
//        dialog.option2Button.setOnClickListener {
//            optionPickerListener.onOption2()
//            dialog.dismiss()
//        }
//        dialog.show()
//    }

    interface OptionPickerListener {
        fun onOption1()
        fun onOption2()
    }

}