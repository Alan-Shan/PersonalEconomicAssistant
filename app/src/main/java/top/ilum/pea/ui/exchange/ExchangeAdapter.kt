package top.ilum.pea.ui.exchange

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item__exchange_elem.view.*
import top.ilum.pea.R
import top.ilum.pea.data.ExchangeElement

class ExchangeAdapter(
    private val elemList: List<ExchangeElement>,
    var activeNum: Int,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<ExchangeAdapter.ViewHolder>() {

    private val viewHoldersList = mutableListOf<ViewHolder>()
    private var isActive = false

    private val layoutsList = listOf(
        R.layout.item__exchange_elem,
        R.layout.item__exchange_elem_active
    )

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutNum = if (viewType == 1) { 1 } else { 0 }
        isActive = viewType == 1
        val root = LayoutInflater.from(parent.context)
            .inflate(layoutsList[0], parent, false)
        val viewHolder = ViewHolder(root)
        if (viewHolder.isHolderActive) {
            viewHolder.relativeLayout.setBackgroundResource(R.drawable.exchange__active_item)
        }
        viewHoldersList.add(viewHolder)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == activeNum) {
            holder.relativeLayout.setBackgroundResource(R.drawable.exchange__active_item)
            holder.isHolderActive = true
        } else {
            holder.relativeLayout.setBackgroundResource(R.drawable.exchange__item)
            holder.isHolderActive = false
        }
        holder.relativeLayout.setOnClickListener {
            if (!holder.isHolderActive) {
                for (view in viewHoldersList) {
                    if (view.isHolderActive) {
                        view.relativeLayout.setBackgroundResource(R.drawable.exchange__item)
                        view.isHolderActive = false
                        break
                    }
                }
                it.setBackgroundResource(R.drawable.exchange__active_item)
                holder.isHolderActive = true
                activeNum = position
                onClick(activeNum)
            }
        }
        holder.elementName.text = elemList[position].name
        holder.elementSign.text = elemList[position].sign
    }

    override fun getItemCount(): Int =
        elemList.size

    override fun getItemViewType(position: Int): Int =
        if (position == activeNum) {
            1
        } else {
            0
        }

    inner class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val relativeLayout = root as ConstraintLayout
        var isHolderActive = isActive
        var elementName: TextView = root.exchange_elem__name
        var elementSign: TextView = root.exchange_elem__sign
    }
}
