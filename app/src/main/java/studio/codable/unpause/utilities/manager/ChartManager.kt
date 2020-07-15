package studio.codable.unpause.utilities.manager

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import studio.codable.unpause.R
import studio.codable.unpause.model.Shift
import studio.codable.unpause.utilities.Constants
import studio.codable.unpause.utilities.helperFunctions.date
import studio.codable.unpause.utilities.helperFunctions.dayOfWeek
import java.util.*

class ChartManager() {
    companion object {
        @SuppressLint("ResourceType")
        fun getLineChartDataset(
            shifts: List<Shift>,
            arrivalDate: Date,
            exitDate: Date,
            context: Context
        ): LineData {
            val lineDataSet = LineDataSet(
                getLineChartData(shifts, arrivalDate, exitDate),
                context.getString(R.string.working_hours)
            ).apply {
                color = Color.parseColor(context.getString(R.color.primary))
                setDrawFilled(true)
                setDrawCircleHole(false)
                setDrawHighlightIndicators(false)
            }

            return LineData().apply { addDataSet(lineDataSet) }
        }

        fun getLineChartData(
            shifts: List<Shift>,
            arrivalDate: Date,
            exitDate: Date
        ): MutableList<Entry> {
            val returnList: MutableList<Entry> = mutableListOf()
            val workingTimes = shifts
                .groupBy { shift -> shift.arrivalTime!!.date() }
                .toSortedMap()
                .mapValues {
                    var sum = 0.000
                    it.value.forEach {
                        if (it.exitTime != null) {
                            sum += TimeManager(
                                it.arrivalTime!!,
                                it.exitTime!!
                            ).getWorkingHoursDecimal()
                        }
                    }
                    sum
                }
            var index = 0
            TimeManager.getDatesBetween(arrivalDate, exitDate)
                .forEach {
                    if (workingTimes.containsKey(it)) {
                        returnList.add(
                            Entry(
                                (index++).toFloat(),
                                workingTimes[it]!!.toFloat()
                            ).apply {
                                data = it
                            })
                    } else {
                        returnList.add(Entry((index++).toFloat(), 0f).apply {
                            data = it
                        })
                    }
                }
            return returnList
        }

        fun initLineChart(lineChart: LineChart, lineData: LineData, markerView: MarkerView) {
            lineChart.xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawLabels(false)
            }

            lineChart.apply {
                data = lineData
                description.isEnabled = false
                setDrawGridBackground(false)
                axisLeft.isEnabled = false
                axisRight.isEnabled = false
                extraBottomOffset = 3f
                animateY(600)
                marker = markerView
                setPinchZoom(false)
                isNestedScrollingEnabled = false
                isHorizontalScrollBarEnabled = false
                isDoubleTapToZoomEnabled = false
                legend.isEnabled = false
            }
        }

        private fun getBarChartData(shifts: List<Shift>): MutableList<BarEntry> {
            val workingHours = shifts
                .groupBy({ shift -> shift.arrivalTime!!.date() },
                    {
                        if (it.exitTime == null) {
                            0.000
                        } else {
                            TimeManager(it.arrivalTime!!, it.exitTime!!).getWorkingHoursDecimal()
                        }
                    })
                .mapValues { entry ->
                    var sum = 0.00
                    entry.value.forEach { sum += it }
                    sum
                }
                .mapKeys {
                    it.key.dayOfWeek()!!
                }

            val returnList: MutableList<BarEntry> = mutableListOf()
            for (i in 0..6) {
                if (workingHours.containsKey(i)) {
                    returnList.add(BarEntry(i.toFloat(), workingHours.getValue(i).toFloat()))
                } else {
                    returnList.add(BarEntry(i.toFloat(), 0f))
                }
            }

            return returnList
        }

        @SuppressLint("ResourceType")
        fun getBarChartDataset(shifts: List<Shift>, context: Context): BarData {
            val barDataSet = BarDataSet(
                getBarChartData(shifts),
                context.getString(R.string.working_hours)
            ).apply {
                color = Color.parseColor(context.getString(R.color.primary))
            }

            return BarData().apply { addDataSet(barDataSet) }
        }

        fun initBarChart(barChart: BarChart, barData: BarData) {
            val xAxisFormatter: ValueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return Constants.Chart.dayLabels[value.toInt()]
                }
            }
            barChart.xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                labelCount = 7
                valueFormatter = xAxisFormatter
            }

            barChart.apply {
                data = barData
                setDrawBarShadow(false)
                setDrawValueAboveBar(true)
                description.isEnabled = false
                setTouchEnabled(false)
                setDrawGridBackground(false)
                axisLeft.isEnabled = false
                axisRight.isEnabled = false
                extraBottomOffset = 7f
                animateY(600)
            }
        }
    }
}