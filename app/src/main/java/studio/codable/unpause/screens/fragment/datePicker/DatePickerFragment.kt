package studio.codable.unpause.screens.fragment.datePicker

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private var datePickerListener: DatePickerListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(this.requireContext(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        datePickerListener?.onDateSet(year, month, day)
    }

    fun addListener(datePickerListener: DatePickerListener) {
        this.datePickerListener = datePickerListener
    }

    interface DatePickerListener {
        fun onDateSet(year: Int, month: Int, day: Int)
    }
}
