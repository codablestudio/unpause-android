package studio.codable.unpause.utils.adapters.userActivityRecyclerViewAdapter

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import studio.codable.unpause.R
import studio.codable.unpause.utilities.adapter.userActivityRecyclerViewAdapter.UserActivityRecyclerViewAdapter


class SwipeActionCallback(
    private val adapter: UserActivityRecyclerViewAdapter,
    private var rootView: View
) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {

    private val backgroundDelete: Drawable? =
        ContextCompat.getDrawable(rootView.context, R.drawable.background_delete)

    private val backgroundEdit: Drawable? =
        ContextCompat.getDrawable(rootView.context, R.drawable.background_edit)

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (direction == ItemTouchHelper.RIGHT) {
            adapter.deleteItem(rootView, viewHolder.adapterPosition)
        } else {
            adapter.editItem(viewHolder.adapterPosition)
            adapter.notifyItemChanged(viewHolder.adapterPosition)
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val itemView = viewHolder.itemView

        val iconDelete = IconDelete(itemView, rootView)
        val iconEdit = IconEdit(itemView, rootView)

        val backgroundCornerOffset = 20

        when {
            dX > 0 -> { // Swiping to the right
                iconDelete.setBounds()

                backgroundDelete?.setBounds(
                    itemView.left,
                    itemView.top,
                    itemView.left + dX.toInt() + backgroundCornerOffset,
                    itemView.bottom
                )
            }
            dX < 0 -> { // swiping left
                iconEdit.setBounds()

                backgroundEdit?.setBounds(
                    itemView.right + dX.toInt() - backgroundCornerOffset,
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )

            }
            else -> {
                backgroundDelete?.setBounds(0, 0, 0, 0)
                backgroundEdit?.setBounds(0, 0, 0, 0)
                iconDelete.remove()
                iconEdit.remove()
            }
        }

        backgroundDelete?.draw(c)
        iconDelete.draw(c)

        backgroundEdit?.draw(c)
        iconEdit.draw(c)
    }

    private data class IconDelete(private val itemView: View, private val rootView: View) {
        private val icon =
            ContextCompat.getDrawable(rootView.context, R.drawable.ic_delete_white_24dp)!!

        private val itemHeight = itemView.bottom - itemView.top
        private val inHeight = icon.intrinsicHeight
        private val inWidth = icon.intrinsicWidth

        private val iconTop = itemView.top + (itemHeight - inHeight) / 2
        private val iconMargin = (itemHeight - inHeight) / 2

        private val iconLeft = itemView.left + iconMargin
        private val iconRight = itemView.left + iconMargin + inWidth
        private val iconBottom = iconTop + inHeight

        fun setBounds() {
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
        }

        fun draw(c: Canvas) {
            icon.draw(c)
        }

        fun remove() {
            icon.setBounds(0, 0, 0, 0)
        }
    }

    private data class IconEdit(private val itemView: View, private val rootView: View) {
        val icon = ContextCompat.getDrawable(itemView.context, R.drawable.ic_edit_white_24dp)!!

        private val itemHeight = itemView.bottom - itemView.top
        private val inHeight = icon.intrinsicHeight
        private val inWidth = icon.intrinsicWidth

        private val iconTop = itemView.top + (itemHeight - inHeight) / 2
        private val iconMargin = (itemHeight - inHeight) / 2

        private val iconLeft = itemView.right - iconMargin - inWidth
        private val iconRight = itemView.right - iconMargin
        private val iconBottom = iconTop + inHeight

        fun setBounds() {
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
        }

        fun draw(c: Canvas) {
            icon.draw(c)
        }

        fun remove() {
            icon.setBounds(0, 0, 0, 0)
        }
    }
}