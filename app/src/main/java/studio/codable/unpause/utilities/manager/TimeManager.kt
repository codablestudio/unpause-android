package studio.codable.unpause.utilities.manager

import studio.codable.unpause.utilities.helperFunctions.createDate
import studio.codable.unpause.utilities.helperFunctions.minute
import studio.codable.unpause.utilities.helperFunctions.toPattern
import java.util.*
import java.util.concurrent.TimeUnit


class TimeManager(var arrivalTime: Date, var exitTime: Date) {

    /**
     * used for display, it is assumed that the values are valid
     * (exit time greater than arrival time)
     *
     * @return difference between exit time and arrival time in
     * the following format: [hours: Int, minutes: Int]
     */
    fun getWorkingHours(): IntArray {
        val diff = TimeUnit.SECONDS.convert(exitTime.time - arrivalTime.time, TimeUnit.MILLISECONDS)
        val hours = diff / (60 * 60)
        var minutes = exitTime.minute() - arrivalTime.minute()
        if (minutes < 0) minutes += 60
        return intArrayOf(hours.toInt(), minutes)
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
}