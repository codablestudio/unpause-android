package studio.codable.unpause.utilities.manager

import studio.codable.unpause.utilities.helperFunctions.createDate
import studio.codable.unpause.utilities.helperFunctions.minute
import studio.codable.unpause.utilities.helperFunctions.toPattern
import java.util.*
import java.util.concurrent.TimeUnit


class TimeManager(var arrivalTime: Date, var exitTime: Date) {

    companion object {
        /**
         * Used for displaying work hours in hh:mm format.
         */
        fun formatTime(time: Float): String {
            val hours = time.toInt()
            val minutes = ((time - hours) * 60).toInt()

            return String.format("%02d:%02d", hours, minutes)
        }

        /**
         * Returns all dates between two dates as a list
         */
        fun getDatesBetween(from: Date, to: Date): List<Date> {
            val dates = mutableListOf<Date>()

            val calendarFrom = Calendar.getInstance().apply { time = from }
            val calendarTo = Calendar.getInstance().apply { time = to }

            while (calendarFrom.compareTo(calendarTo) != 0) {
                dates.add(calendarFrom.time)
                calendarFrom.add(Calendar.DATE, 1)
            }

            return dates.apply { dates.add(calendarFrom.time) }
        }
    }

    /**
     * @return difference between exit time and arrival time in
     * the following format: [hours: Int, minutes: Int]
     */
    fun getWorkingHours(): HoursAndMinutes {
        val diff = TimeUnit.SECONDS.convert(exitTime.time - arrivalTime.time, TimeUnit.MILLISECONDS)
        val hours = diff / (60 * 60)
        val minutes = diff / 60 % 60
        return HoursAndMinutes(hours.toInt(), minutes.toInt())
    }

    /**
     * @return non-rounded difference between exit time and
     * arrival time in hours
     */
    fun getWorkingHoursDecimal(): Double {
        val diff = TimeUnit.SECONDS.convert(exitTime.time - arrivalTime.time, TimeUnit.MILLISECONDS)
        val hours = diff / (60 * 60)
        val minutes = diff / 60 % 60
        return hours + minutes / 60.0
    }

    fun changeArrivalTime(hour: Int, minute: Int) {

        require(hour in 0..23) { "Hour must be between 0 and 23" }
        require(minute in 0..59) { "Minute must be between 0 and 59" }

        val calendar = Calendar.getInstance()
        calendar.time = arrivalTime

        val newArrivalTime = createDate(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            hour,
            minute
        )

        arrivalTime = newArrivalTime
    }

    fun changeArrivalDate(year: Int, month: Int, day: Int) {

        require(month in 0..11) { "Month must be between 0 and 11" }
        require(day in 1..31) { "Day must be between 1 and 31" }

        val calendar = Calendar.getInstance()
        calendar.time = arrivalTime
        val newArrivalTime = createDate(
            year,
            month,
            day,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE)
        )

        arrivalTime = newArrivalTime
    }

    fun changeExitTime(hour: Int, minute: Int) {

        require(hour in 0..23) { "Hour must be between 0 and 23" }
        require(minute in 0..59) { "Minute must be between 0 and 59" }

        val calendar = Calendar.getInstance()
        calendar.time = exitTime
        val newExitTime = createDate(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            hour,
            minute
        )

        exitTime = newExitTime
    }

    fun changeExitDate(year: Int, month: Int, day: Int) {

        require(month in 0..11) { "Month must be between 0 and 11" }
        require(day in 1..31) { "Day must be between 1 and 31" }

        val calendar = Calendar.getInstance()
        calendar.time = exitTime

        val newExitTime = createDate(
            year,
            month,
            day,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE)
        )

        exitTime = newExitTime
    }

    /**
     * @return {"14:04", "12.03.2019"}
     */
    fun arrivalToArray(): List<String> {
        return arrayListOf(arrivalTime.toPattern("HH:mm"), arrivalTime.toPattern("dd.MM.yyyy"))
    }

    /**
     * @return {"14:04", "12.03.2019"}
     */
    fun exitToArray(): List<String> {
        return arrayListOf(exitTime.toPattern("HH:mm"), exitTime.toPattern("dd.MM.yyyy"))
    }

    data class HoursAndMinutes(val hours : Int, val minutes: Int)
}