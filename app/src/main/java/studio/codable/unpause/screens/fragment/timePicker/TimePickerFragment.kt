package studio.codable.unpause.view.timePicker

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePickerFragment(private val hour: Int?, private val minute: Int?) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    private var timePickerListener: TimePickerListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        return if (this.hour != null && this.minute != null){
            TimePickerDialog(activity, this, this.hour, this.minute, DateFormat.is24HourFormat(activity))
        }else{
            TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
        }
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        timePickerListener?.onTimeSet(hourOfDay, minute)
    }

    fun addListener(timePickerListener: TimePickerListener) {
        this.timePickerListener = timePickerListener
    }

    interface TimePickerListener {
        fun onTimeSet(hourOfDay: Int, minute: Int)
    }
}