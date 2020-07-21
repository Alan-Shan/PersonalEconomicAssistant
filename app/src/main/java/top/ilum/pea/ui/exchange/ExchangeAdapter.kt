package top.ilum.pea.ui.exchange

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item__exchange_elem.view.*
import top.ilum.pea.R
import top.ilum.pea.data.ExchangeElement

class ExchangeAdapter(
    private val elemList: List<ExchangeElement>,
    private var activeNum: Int
): RecyclerView.Adapter<ExchangeAdapter.ViewHolder>() {

    private val viewHoldersList = mutableListOf<ViewHolder>()
    private var isActive = false

    private val layoutsList = listOf(
        R.layout.item__exchange_elem,
        R.layout.item__exchange_elem_active
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutNum = if (viewType == 1) { 1 } else { 0 }
        isActive = viewType == 1
        val root = LayoutInflater.from(parent.context)
            .inflate(layoutsList[0], parent, false)
        val viewHolder = ViewHolder(root)
        if (viewHolder.isHolderActive) {
            viewHolder.relativeLayout.setBackgroundResource(R.drawable.exchange__active_item)
        }
        viewHolder.relativeLayout.setOnClickListener {
            if (viewHolder.isHolderActive) {
                it.setBackgroundResource(R.drawable.exchange__active_item)
            } else {
                it.setBackgroundResource(R.drawable.exchange__item)
            }
            viewHolder.isHolderActive = !viewHolder.isHolderActive
        }
        viewHoldersList.add(viewHolder)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
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

    inner class ViewHolder(private val root: View) : RecyclerView.ViewHolder(root) {
        val relativeLayout = root as ConstraintLayout
        var isHolderActive = isActive
        var elementName: TextView = root.exchange_elem__name
        var elementSign: TextView = root.exchange_elem__sign
    }
}
