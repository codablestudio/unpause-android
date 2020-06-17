package studio.codable.unpause.screens.fragment.datePicker

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

typealias DatePickerListener = (year: Int, month: Int, dayOfMonth: Int) -> Unit

private val calendar = Calendar.getInstance()

class DatePickerFragment(
    private val year: Int = calendar.get(Calendar.YEAR),
    private val month: Int = calendar.get(Calendar.MONTH),
    private val day: Int = calendar.get(Calendar.DAY_OF_MONTH)) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private var datePickerListener: DatePickerListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return DatePickerDialog(this.requireContext(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        datePickerListener?.invoke(year, month, day)
    }

    fun setListener(datePickerListener: DatePickerListener) {
        this.datePickerListener = datePickerListener
    }

}
