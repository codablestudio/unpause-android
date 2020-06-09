package studio.codable.unpause.utilities.adapter.userActivityRecyclerViewAdapter

import android.animation.ObjectAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.item_user_activity.view.*
import studio.codable.unpause.R
import studio.codable.unpause.model.Shift
import studio.codable.unpause.utilities.adapter.SwipeActions
import studio.codable.unpause.utilities.manager.TimeManager
import studio.codable.unpause.utils.adapters.userActivityRecyclerViewAdapter.ShiftsDiffCallback
import java.util.*


class UserActivityRecyclerViewAdapter constructor(
    private var context: Context,
    private val listener: UserActivityListener
) : RecyclerView.Adapter<UserActivityRecyclerViewAdapter.ViewHolder>(), SwipeActions {

    private val shifts = arrayListOf<Shift>()

    private var recentlyDeleted: Queue<Shift> = ArrayDeque()
    private var recentlyDeletedPosition: Queue<Int> = ArrayDeque()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val recyclerViewItem = inflater.inflate(R.layout.item_user_activity, parent, false)
        return ViewHolder(recyclerViewItem)
    }

    override fun getItemCount(): Int {
        return shifts.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val timeManager =
            TimeManager(shifts[position].arrivalTime!!, shifts[position].exitTime!!)

        holder.itemView.text_arrived_at_time.text = timeManager.arrivalToArray()[0]
        holder.itemView.text_arrived_at_date.text = timeManager.arrivalToArray()[1]

        holder.itemView.text_left_at_time.text = timeManager.exitToArray()[0]
        holder.itemView.text_left_at_date.text = timeManager.exitToArray()[1]

        holder.itemView.text_working_hours.text = context.getString(
            R.string.n_hours_m_minutes,
            timeManager.getWorkingHours()[0],
            timeManager.getWorkingHours()[1]
        )

        holder.itemView.text_job_description.text = shifts[position].description
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {

            val timeManager =
                TimeManager(shifts[position].arrivalTime!!, shifts[position].exitTime!!)

            payloads.forEach { list ->

                (list as List<Any?>).forEach {

                    if (it is ShiftsDiffCallback.ArrivalTimeChanged) {
                        crossFadeTextView(
                            holder.itemView.text_arrived_at_time,
                            timeManager.arrivalToArray()[0]
                        )

                        crossFadeTextView(
                            holder.itemView.text_working_hours, context.getString(
                                R.string.n_hours_m_minutes,
                                timeManager.getWorkingHours()[0],
                                timeManager.getWorkingHours()[1]
                            )
                        )
                    }

                    if (it is ShiftsDiffCallback.ArrivalDateChanged) {
                        crossFadeTextView(
                            holder.itemView.text_arrived_at_date,
                            timeManager.arrivalToArray()[1]
                        )

                        crossFadeTextView(
                            holder.itemView.text_working_hours, context.getString(
                                R.string.n_hours_m_minutes,
                                timeManager.getWorkingHours()[0],
                                timeManager.getWorkingHours()[1]
                            )
                        )
                    }

                    if (it is ShiftsDiffCallback.ExitTimeChanged) {
                        crossFadeTextView(
                            holder.itemView.text_left_at_time,
                            timeManager.exitToArray()[0]
                        )

                        crossFadeTextView(
                            holder.itemView.text_working_hours, context.getString(
                                R.string.n_hours_m_minutes,
                                timeManager.getWorkingHours()[0],
                                timeManager.getWorkingHours()[1]
                            )
                        )
                    }

                    if (it is ShiftsDiffCallback.ExitDateChanged) {
                        crossFadeTextView(
                            holder.itemView.text_left_at_date,
                            timeManager.exitToArray()[1]
                        )

                        crossFadeTextView(
                            holder.itemView.text_working_hours, context.getString(
                                R.string.n_hours_m_minutes,
                                timeManager.getWorkingHours()[0],
                                timeManager.getWorkingHours()[1]
                            )
                        )
                    }

                    if (it is ShiftsDiffCallback.DescriptionChanged) {
                        crossFadeTextView(
                            holder.itemView.text_job_description,
                            shifts[position].description
                        )
                    }
                }
            }
        }
    }

    override fun deleteItem(rootView: View, position: Int) {
        recentlyDeleted.add(shifts[position])
        recentlyDeletedPosition.add(position)
        shifts.removeAt(position)
        notifyItemRemoved(position)
        showUndoSnackbar(rootView)
    }

    private fun showUndoSnackbar(rv: View) {
        val snackbar =
            Snackbar.make(rv, context.getString(R.string.shift_removed), Snackbar.LENGTH_LONG)
                .apply {
                    anchorView = rv.findViewById(R.id.nav_host_home_activity)
                    setAction(context.getString(R.string.undo)) { undoDelete() }

                    addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            if (event == DISMISS_EVENT_TIMEOUT || event == DISMISS_EVENT_SWIPE || event == DISMISS_EVENT_CONSECUTIVE) {
                                while (recentlyDeleted.size > 0) {
                                    listener.onDelete(
                                        recentlyDeleted.remove(),
                                        recentlyDeletedPosition.remove()
                                    )
                                }
                            }
                        }
                    })
                }
        snackbar.show()
    }

    private fun undoDelete() {
        val recDelPos = recentlyDeletedPosition.remove()
        shifts.add(recDelPos, recentlyDeleted.remove())
        notifyItemInserted(recDelPos)
    }

    override fun editItem(position: Int) {
        listener.onEdit(shifts[position], position)
    }

    fun updateContent(newShifts: List<Shift>) {

        val sorted = newShifts.sortedByDescending { it.arrivalTime } // to be displayed

        shifts.clear()
        shifts.addAll(sorted)
        notifyDataSetChanged()
    }

    private fun crossFadeTextView(textView: TextView, newText: String?, duration: Long = 1000) {

        ObjectAnimator.ofFloat(textView, "alpha", textView.alpha, 0f)
            .apply {
                this.duration = duration
                start()
            }

        textView.text = newText

        ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f)
            .apply {
                this.duration = duration
                start()
            }

    }

    interface UserActivityListener {
        fun onDelete(shift: Shift, position: Int)
        fun onEdit(shift: Shift, position: Int)
    }

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView)
}