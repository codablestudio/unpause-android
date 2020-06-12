package studio.codable.unpause.model

import java.util.*

data class User(
    val id: String,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    var shifts: ArrayList<Shift> = arrayListOf(),
    var company: Company? = null,
    val companyId : String? = null
) {

    fun getUserActivity(from: Date, to: Date): ArrayList<Shift> {
        val returnList: ArrayList<Shift> = arrayListOf()
        for (shift in shifts) {
            if (shift.exitTime != null && shift.arrivalTime!! >= from && shift.exitTime!! <= to) {
                returnList.add(shift)
            }
        }
        return returnList
    }
}