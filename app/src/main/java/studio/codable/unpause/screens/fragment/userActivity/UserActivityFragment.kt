package studio.codable.unpause.screens.fragment.userActivity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.leinardi.android.speeddial.SpeedDialView
import kotlinx.android.synthetic.main.fragment_user_activity.*
import studio.codable.unpause.BuildConfig
import studio.codable.unpause.R
import studio.codable.unpause.base.activity.BaseActivity
import studio.codable.unpause.base.fragment.BaseFragment
import studio.codable.unpause.model.Shift
import studio.codable.unpause.model.User
import studio.codable.unpause.screens.UserViewModel
import studio.codable.unpause.screens.fragment.workingTimeWarning.WorkingTimeWarningFragment
import studio.codable.unpause.utilities.adapter.userActivityRecyclerViewAdapter.UserActivityRecyclerViewAdapter
import studio.codable.unpause.utilities.manager.DialogManager
import studio.codable.unpause.utilities.manager.TimeManager
import studio.codable.unpause.utils.adapters.userActivityRecyclerViewAdapter.SwipeActionCallback
import studio.codable.unpause.utils.openCSV.OpenCSVWriter
import java.io.File
import java.util.*

class UserActivityFragment : BaseFragment(false) {

    private val userVm: UserViewModel by activityViewModels()

    private lateinit var mDialogManager: DialogManager
    private lateinit var timeManager: TimeManager
    private lateinit var speedDialView: SpeedDialView
    private lateinit var user : User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = userVm.user.value!!
        initUI()

    }

    private fun initUI() {

        activity?.let {

            intTimeManager()

            initSpeedDialView()

            from_date_text_view.text = timeManager.arrivalToArray()[1]
            to_date_text_view.text = timeManager.exitToArray()[1]

            mDialogManager = DialogManager(activity as BaseActivity)

            edit_from_img.setOnClickListener {
                mDialogManager?.openDatePickerDialog { year, month, dayOfMonth ->
                    timeManager.changeArrivalDate(year, month, dayOfMonth)
                    updateFromDate(timeManager.arrivalToArray()[1])
                    updateRecyclerView() }
            }

            edit_to_img.setOnClickListener {
                mDialogManager?.openDatePickerDialog { year, month, dayOfMonth ->
                    timeManager.changeExitDate(year, month, dayOfMonth)
                    updateToDate(timeManager.exitToArray()[1])
                    updateRecyclerView()
                }
            }

        }

        initRecyclerView(requireActivity())
    }

    private fun initRecyclerView(activity: FragmentActivity) {
        val recyclerViewAdapter = UserActivityRecyclerViewAdapter(
            requireContext(),
            { shift, _ -> userVm.deleteShift(shift) },
            { shift, _ -> editShift(shift) }
        )


        user_activity_recycler_view?.apply {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerViewAdapter
        }

        updateRecyclerView()

        val itemTouchHelper =
            ItemTouchHelper(
                SwipeActionCallback(
                    recyclerViewAdapter,
                    activity.window.decorView.findViewById(R.id.layout_home_activity)
                )
            )
        itemTouchHelper.attachToRecyclerView(user_activity_recycler_view)
    }

    private fun sendCSV() {

//        if (userVm.user.value?.company == null) {
//            val intent = Intent(context, BossInfoActivity::class.java)
//            startActivityForResult(intent, 1234)
//        } else {
            val file = OpenCSVWriter.writeShiftsToCSV(
                context,
                user.getUserActivity(
                    timeManager.arrivalTime,
                    timeManager.exitTime
                ),
                getString(R.string.csv_file_name, user.firstName, user.lastName)
            )
            startEmailActivity(file)
//        }
    }

    private fun startEmailActivity(file: File?) {
        val emailIntent = prepareEmail(file)
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)))
    }

    private fun prepareEmail(file: File?): Intent {
        val uri =
            FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID, file!!)
        val to = arrayOf(user.company?.email.toString())

        return Intent(Intent.ACTION_SEND).apply {
            type = "text/html"
            putExtra(Intent.EXTRA_EMAIL, to)
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.email_subject)
            )
            putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.email_body)
            ) }

    }

    private fun openCSV(file: File?) {
        val csvIntent = Intent(Intent.ACTION_VIEW)

        val uri =
            FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID, file!!)

        csvIntent.setDataAndType(uri, "text/csv")
        csvIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        csvIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        requireActivity().startActivity(csvIntent)
    }

    private fun initSpeedDialView() {
        speedDialView = speed_dial_view

        speedDialView?.inflate(R.menu.menu_speed_dial)

        speedDialView?.setOnActionSelectedListener { speedDialActionItem ->
            when (speedDialActionItem.id) {

                R.id.open_csv_button -> {
                    val file = OpenCSVWriter.writeShiftsToCSV(
                        context,
                        user.getUserActivity(
                            timeManager.arrivalTime,
                            timeManager.exitTime
                        ),
                        getString(R.string.csv_file_name, user.firstName, user.lastName)
                    )
                    openCSV(file)
                    false
                }

                R.id.send_as_email_button -> {

                    sendCSV()
                    false
                }

                R.id.add_custom_shift_button -> {
                    if (user.shifts?.last()?.exitTime == null) {
                        showError(getString(R.string.custom_shift_adding_outside_of_working_time_warning))
                        true
                    } else {
                        mDialogManager?.openWorkingTimeDialog(
                            timeManager.arrivalTime,
                            timeManager.exitTime,
                            true,
                            mDialogManager!!,
                            object : WorkingTimeWarningFragment.DialogListener {
                                override fun onContinue(
                                    arrivalTime: Date,
                                    exitTime: Date
                                ) {
                                    mDialogManager?.openDescriptionDialog(
                                        null, { description ->
                                            val newShift =
                                                Shift(
                                                    arrivalTime,
                                                    exitTime,
                                                    description
                                                )
                                            userVm.addCustomShift(newShift)
                                            updateRecyclerView()
                                            showMessage(getString(R.string.shift_added))
                                        },
                                        {
                                            //no action
                                        })
                                }
                            })
                        false
                    }
                }
                else -> false
            }
        }
    }

    private fun intTimeManager() {
        val cal1 = Calendar.getInstance()
        cal1.apply {
            add(Calendar.MONTH, -1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val cal2 = Calendar.getInstance()
        cal2.apply {
            time = Date()
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }

        timeManager = TimeManager(cal1.time, cal2.time)
    }

    fun updateFromDate(activeDate: String) {
        from_date_text_view.text = activeDate
    }

    fun updateToDate(activeDate: String) {
        to_date_text_view.text = activeDate
    }

    fun editShift(shift: Shift) {
        mDialogManager?.openWorkingTimeDialog(
            shift.arrivalTime!!,
            shift.exitTime!!,
            false,
            mDialogManager!!,
            object : WorkingTimeWarningFragment.DialogListener {
                override fun onContinue(arrivalTime: Date, exitTime: Date) {
                    mDialogManager?.openDescriptionDialog(
                        shift.description, { description: String ->

                            val newShift = Shift(arrivalTime, exitTime, description)
                            userVm.editShift(newShift)
                            updateRecyclerView()
                            showMessage(getString(R.string.shift_edited))

                        },
                        {
                            //no action
                        })

                }
            })
    }

    private fun updateRecyclerView() {
        (user_activity_recycler_view?.adapter as UserActivityRecyclerViewAdapter)
            .updateContent(
                user.getUserActivity(
                    timeManager.arrivalTime,
                    timeManager.exitTime
                )
            )
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (data == null) return
//
//        if (requestCode == 1234 && resultCode == 1) {
//            user?.boss = data.getSerializableExtra(BOSS_PARAM) as Boss
//            mViewModel?.updateUserInDatabase(user!!)
//            sendCSV()
//        }
//    }

}
