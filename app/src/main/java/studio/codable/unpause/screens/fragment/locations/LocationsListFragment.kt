package studio.codable.unpause.screens.fragment.locations

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_locations_list.*
import studio.codable.unpause.R
import studio.codable.unpause.base.fragment.BaseFragment
import studio.codable.unpause.model.Location
import studio.codable.unpause.screens.UserViewModel
import studio.codable.unpause.utilities.adapter.locationsRecyclerViewAdapter.LocationsRecyclerViewAdapter

class LocationsListFragment : BaseFragment(false) {

    private val userVm: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_locations_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        initObservers()
    }

    private fun initObservers() {
        userVm.locations.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            refreshRecyclerView(it)
        })
    }

    private fun refreshRecyclerView(it: List<Location>) {
        (locations_recycler_view.adapter as LocationsRecyclerViewAdapter).updateContent(it)
        (locations_recycler_view.adapter as LocationsRecyclerViewAdapter).notifyDataSetChanged()
    }

    private fun initUI() {
        locations_recycler_view.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter =
                LocationsRecyclerViewAdapter(
                    if (userVm.locations.value != null) ArrayList(userVm.locations.value!!) else arrayListOf()
                )
        }

        btn_add_new_location.setOnClickListener {
            svm.navigate(LocationsListFragmentDirections.actionLocationsListFragmentToMapFragment())
        }
    }

    override fun onResume() {
        super.onResume()
        userVm.getLocations()
    }
}