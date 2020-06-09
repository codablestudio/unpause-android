package studio.codable.unpause.screens.fragment.workingTimeWarning

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.working_time_warning_dialog.*
import studio.codable.unpause.R
import studio.codable.unpause.screens.fragment.datePicker.DatePickerFragment
import studio.codable.unpause.utilities.manager.DialogManager
import studio.codable.unpause.utilities.manager.TimeManager
import studio.codable.unpause.view.timePicker.TimePickerFragment
import java.util.*

class WorkingTimeWarningFragment(
    private var arrivalTime: Date,
    private var exitTime: Date,
    private var editArrivalDate: Boolean,
    private var dialogManager: DialogManager
) : DialogFragment() {

    companion object {
        const val WORKING_TIME_THRESHOLD = 8
    }

    private lateinit var dialogListener: DialogListener
    private lateinit var timeManager: TimeManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.working_time_warning_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (editArrivalDate) {
            edit_arrived_at_date_icon.visibility = View.VISIBLE
        } else {
            edit_arrived_at_date_icon.visibility = View.INVISIBLE
        }

        timeManager = TimeManager(arrivalTime, exitTime)

        arrivedAtDateTextView.text =
            timeManager.arrivalToArray()[1]
        arrivedAtTimeTextView.text =
            timeManager.arrivalToArray()[0]

        updateFragmentExitTime(timeManager.exitToArray()[0])
        updateFragmentExitDate(timeManager.exitToArray()[1])
        updateMessage()

        changeArrivedAtDateButton.setOnClickListener {
            if (editArrivalDate) {
                dialogManager.openDatePickerDialog((object : DatePickerFragment.DatePickerListener {
                    override fun onDateSet(year: Int, month: Int, day: Int) {
                        timeManager.changeArrivalDate(year, month, day)
                        updateFragmentArrivedAtDate(timeManager.arrivalToArray()[1])
                        updateMessage()
                    }
                }))
            }
        }

        changeArrivedAtTimeButton.setOnClickListener {
            dialogManager.openTimePickerDialog(
                null,
                null,
                (object : TimePickerFragment.TimePickerListener {
                    override fun onTimeSet(hourOfDay: Int, minute: Int) {
                        timeManager.changeArrivalTime(hourOfDay, minute)
                        updateFragmentArrivedAtTime(timeManager.arrivalToArray()[0])
                        updateMessage()
                    }
                })
            )
        }

        changeExitDateButton.setOnClickListener {
            dialogManager.openDatePickerDialog((object : DatePickerFragment.DatePickerListener {
                override fun onDateSet(year: Int, month: Int, day: Int) {
                    timeManager.changeExitDate(year, month, day)
                    updateFragmentExitDate(timeManager.exitToArray()[1])
                    updateMessage()
                }
            }))
        }

        changeExitTimeButton.setOnClickListener {
            dialogManager.openTimePickerDialog(
                null,
                null,
                (object : TimePickerFragment.TimePickerListener {
                    override fun onTimeSet(hourOfDay: Int, minute: Int) {
                        timeManager.changeExitTime(hourOfDay, minute)
                        updateFragmentExitTime(timeManager.exitToArray()[0])
                        updateMessage()
                    }
                })
            )
        }

        continueButton.setOnClickListener {
            if (timeManager.getWorkingHoursDecimal() < 0) {
                showError(getString(R.string.working_hours_must_be_positive))
            } else {
                dialogListener.onContinue(
                    timeManager.arrivalTime,
                    timeManager.exitTime
                )
                dismiss()
            }
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    private fun updateMessage() {
        updateFragmentWorkingTimeMessage(
            getString(
                R.string.total_working_hours_message,
                timeManager.getWorkingHours()[0].toString(),
                timeManager.getWorkingHours()[1].toString()
            )
        )
        changeMessageColor()
    }

    private fun changeMessageColor() {
        if (timeManager.getWorkingHours()[0] >= WORKING_TIME_THRESHOLD) {
            workingTimeEditText.setTextColor(Color.RED)
        } else {
            workingTimeEditText.setTextColor(Color.BLACK)
        }
    }

    fun addListener(dialogListener: DialogListener) {
        this.dialogListener = dialogListener
    }

    fun updateFragmentExitTime(activeTime: String) {
        activeExitTimeTextView.text = activeTime
    }

    fun updateFragmentExitDate(activeDate: String) {
        activeExitDateTextView.text = activeDate
    }

    fun updateFragmentArrivedAtTime(activeTime: String) {
        arrivedAtTimeTextView.text = activeTime
    }

    fun updateFragmentArrivedAtDate(activeDate: String) {
        arrivedAtDateTextView.text = activeDate
    }

    fun updateFragmentWorkingTimeMessage(message: String) {
        workingTimeEditText.text = message
    }

    interface DialogListener {
        fun onContinue(arrivalTime: Date, exitTime: Date)
    }

    fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}