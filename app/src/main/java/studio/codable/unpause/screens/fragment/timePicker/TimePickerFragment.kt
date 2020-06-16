package studio.codable.unpause.screens.fragment.timePicker

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import studio.codable.unpause.utilities.LambdaDoubleIntToUnit

class TimePickerFragment(
    private val hour: Int,
    private val minute: Int
) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    private var timePickerListener: LambdaDoubleIntToUnit? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return TimePickerDialog(activity, this, hour, minute,
            DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        timePickerListener?.invoke(hourOfDay, minute)
    }

    fun setListener(timePickerListener: LambdaDoubleIntToUnit) {
        this.timePickerListener = timePickerListener
    }
}