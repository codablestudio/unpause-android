package studio.codable.unpause.utilities.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import studio.codable.unpause.utilities.BaseClickListener

abstract class BaseAdapter<T>(
    @LayoutRes private val itemResourceId: Int,
    private val itemList: List<T>,
    private val onClick: BaseClickListener<T>?
) : RecyclerView.Adapter<BaseAdapter.BaseViewHolder<T>>() {

    override fun getItemCount(): Int = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val item = inflater.inflate(itemResourceId, parent, false)
        return BaseViewHolder(
            item
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        onClick?.let {
            holder.bind(itemList[position], it)
        }
    }

    open class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(model: T, onClick: BaseClickListener<T>) {
            itemView.setOnClickListener { onClick.invoke(model) }
        }

        fun unbind() {
            itemView.setOnClickListener(null)
        }
    }
}