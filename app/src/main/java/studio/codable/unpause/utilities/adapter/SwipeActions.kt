package studio.codable.unpause.utilities.adapter

import android.view.View

interface SwipeActions {

    fun deleteItem(rootView: View, position: Int)

    fun editItem(position: Int)
}