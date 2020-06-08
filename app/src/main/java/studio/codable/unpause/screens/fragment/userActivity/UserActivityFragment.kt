package studio.codable.unpause.screens.fragment.userActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.leinardi.android.speeddial.SpeedDialView
import kotlinx.android.synthetic.main.fragment_user_activity.*

import studio.codable.unpause.R
import studio.codable.unpause.base.activity.BaseActivity
import studio.codable.unpause.model.Shift
import studio.codable.unpause.model.User
import studio.codable.unpause.screens.UserViewModel
import studio.codable.unpause.utils.adapters.userActivityRecyclerViewAdapter.UserActivityRecyclerViewAdapter

class UserActivityFragment : Fragment() {

    //TODO: make it work
    private val userVm: UserViewModel by activityViewModels()

    private var user: User? = null
    private var listener: UserActivityRecyclerViewAdapter.UserActivityListener? = null
//    private var mRecyclerView: RecyclerView? = null
//    private var mDialogManager: DialogManager? = null
//    private lateinit var timeManager: TimeManager
    private var speedDialView: SpeedDialView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        initUI()
    }

//    private fun initUI() {
//
//        activity?.let {
//
//            intTimeManager()
//
//            initSpeedDialView()
//
//            from_date_text_view.text = timeManager.arrivalToArray()[1]
//            to_date_text_view.text = timeManager.exitToArray()[1]
//
//            mDialogManager = DialogManager(activity as BaseActivity)
//
//            edit_from_date.setOnClickListener {
//                mDialogManager?.openDatePickerDialog((object :
//                    DatePickerFragment.DatePickerListener {
//                    override fun onDateSet(year: Int, month: Int, day: Int) {
//                        timeManager.changeArrivalDate(year, month, day)
//                        updateFromDate(timeManager.arrivalToArray()[1])
//                        updateRecyclerView()
//                    }
//                }))
//            }
//
//            edit_to_date.setOnClickListener {
//                mDialogManager?.openDatePickerDialog((object :
//                    DatePickerFragment.DatePickerListener {
//                    override fun onDateSet(year: Int, month: Int, day: Int) {
//                        timeManager.changeExitDate(year, month, day)
//                        updateToDate(timeManager.exitToArray()[1])
//                        updateRecyclerView()
//                    }
//                }))
//            }
//
//        }
//
//        initRecyclerView(requireActivity())
//    }

//    private fun initRecyclerView(activity: FragmentActivity) {
//        mRecyclerView = user_activity_recycler_view
//        mRecyclerView?.isNestedScrollingEnabled = false
//        mRecyclerView?.layoutManager = LinearLayoutManager(context)
//        val recyclerViewAdapter = UserActivityRecyclerViewAdapter(
//            context!!,
//            object : UserActivityRecyclerViewAdapter.UserActivityListener {
//
//                override fun onDelete(shift: Shift, position: Int) {
//                    deleteShift(shift)
//                }
//
//                override fun onEdit(shift: Shift, position: Int) {
//                    editShift(shift)
//                }
//            }
//        )
//        mRecyclerView?.adapter = recyclerViewAdapter
//
//        updateRecyclerView()
//
//        val itemTouchHelper =
//            ItemTouchHelper(
//                SwipeActionCallback(
//                    recyclerViewAdapter,
//                    activity.window.decorView.findViewById(R.id.layout_home_activity)
//                )
//            )
//        itemTouchHelper.attachToRecyclerView(mRecyclerView)
//    }

}
