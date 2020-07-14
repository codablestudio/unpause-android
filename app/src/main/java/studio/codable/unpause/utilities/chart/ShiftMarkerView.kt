package studio.codable.unpause.utilities.chart

import android.content.Context
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.shift_marker_view_layout.view.*
import studio.codable.unpause.R
import studio.codable.unpause.model.Shift
import studio.codable.unpause.utilities.helperFunctions.toPattern
import studio.codable.unpause.utilities.manager.TimeManager
import java.util.*

class ShiftMarkerView(context: Context, layoutResource: Int = R.layout.shift_marker_view_layout) :
    MarkerView(context, layoutResource) {

    // callbacks every time the MarkerView is redrawn, can be used to update the content (UI)
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        val date = e?.data as Date
        text_arrival_time.text = date.toPattern("dd.MM.yyyy")
        text_working_hours.text = TimeManager.formatTime(e.y)
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        //this will display marker-view centered horizontally and above selected value
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }
}