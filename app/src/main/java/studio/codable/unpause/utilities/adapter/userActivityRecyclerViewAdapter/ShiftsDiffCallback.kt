package studio.codable.unpause.utils.adapters.userActivityRecyclerViewAdapter

import androidx.recyclerview.widget.DiffUtil
import studio.codable.unpause.model.Shift

class ShiftsDiffCallback(oldList: List<Shift>, newList: List<Shift>) :
    DiffUtil.Callback() {

    //TODO: make it work
    private val old = arrayListOf<Shift>()
    private val new = arrayListOf<Shift>()

    init {
        old.addAll(oldList)
        new.addAll(newList)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition].arrivalTime == new[newItemPosition].arrivalTime
                && old[oldItemPosition].exitTime == new[newItemPosition].exitTime
                && old[oldItemPosition].description == new[newItemPosition].description
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition].arrivalTime == new[newItemPosition].arrivalTime
                || old[oldItemPosition].exitTime == new[newItemPosition].exitTime
                || old[oldItemPosition].description == new[newItemPosition].description
    }

    override fun getNewListSize(): Int {
        return new.size
    }

    override fun getOldListSize(): Int {
        return old.size
    }

//    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
//        val timeManagerOld =
//            TimeManager(old[oldItemPosition].arrivalTime!!, old[oldItemPosition].exitTime!!)
//        val timeManagerNew =
//            TimeManager(new[newItemPosition].arrivalTime!!, new[newItemPosition].exitTime!!)
//
//        val changesList = arrayListOf<Any>()
//
//        if (!(old[oldItemPosition].arrivalTime == new[newItemPosition].arrivalTime)) {
//            if (timeManagerOld.arrivalToArray()[0] != timeManagerNew.arrivalToArray()[0]) {
//                changesList.add(ArrivalTimeChanged())
//            }
//            if (timeManagerOld.arrivalToArray()[1] != timeManagerNew.arrivalToArray()[1]) {
//                changesList.add(ArrivalDateChanged())
//            }
//        }
//
//        if (!(old[oldItemPosition].exitTime == new[newItemPosition].exitTime)) {
//            if (timeManagerOld.exitToArray()[0] != timeManagerNew.exitToArray()[0]) {
//                changesList.add(ExitTimeChanged())
//            }
//            if (timeManagerOld.exitToArray()[1] != timeManagerNew.exitToArray()[1]) {
//                changesList.add(ExitDateChanged())
//            }
//        }
//
//        if (old[oldItemPosition].description != new[newItemPosition].description) {
//            changesList.add(DescriptionChanged())
//        }
//
//        return changesList
//    }


    class ArrivalTimeChanged
    class ArrivalDateChanged
    class ExitTimeChanged
    class ExitDateChanged
    class DescriptionChanged
}