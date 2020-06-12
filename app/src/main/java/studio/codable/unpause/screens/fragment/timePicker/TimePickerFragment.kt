package studio.codable.unpause.view.timePicker

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import studio.codable.unpause.utilities.DoubleIntToUnitLambda
import java.util.*

private val calendar = Calendar.getInstance()

typealias TimePickerListener = DoubleIntToUnitLambda

class TimePickerFragment(
    private val hour: Int? = calendar.get(Calendar.HOUR_OF_DAY),
    private val minute: Int? = calendar.get(Calendar.MINUTE)
) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    private var timePickerListener: TimePickerListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return TimePickerDialog(activity, this,
            hour ?: calendar.get(Calendar.HOUR_OF_DAY),
            minute ?: calendar.get(Calendar.MINUTE),
            DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        timePickerListener?.invoke(hourOfDay, minute)
    }

    fun addListener(timePickerListener: TimePickerListener) {
        this.timePickerListener = timePickerListener
    }
}