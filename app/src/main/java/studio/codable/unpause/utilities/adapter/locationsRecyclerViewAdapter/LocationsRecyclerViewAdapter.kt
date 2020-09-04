package studio.codable.unpause.utilities.adapter.locationsRecyclerViewAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_location.view.*
import studio.codable.unpause.R
import studio.codable.unpause.model.Location

class LocationsRecyclerViewAdapter(private val locations: ArrayList<Location>) : RecyclerView.Adapter<LocationsRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val recyclerViewItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_location, parent, false)
        return ViewHolder(recyclerViewItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.apply {
            text_location_name.text = locations[position].name ?: resources.getString(R.string.unnamed_location)
            text_location_coordinates.text = resources.getString(
                R.string.lat_long_format,
                locations[position].position.latitude,
                locations[position].position.longitude
            )
        }
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    fun updateContent(newLocations: List<Location>) {
        locations.clear()
        locations.addAll(newLocations)
        notifyDataSetChanged()
    }

}