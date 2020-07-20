package top.ilum.pea.ui.exchange

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item__exchange_elem.view.*
import kotlinx.android.synthetic.main.item__exchange_elem_active.view.*
import top.ilum.pea.R
import top.ilum.pea.data.ExchangeElement

class ExchangeAdapter(
    elemList0: List<ExchangeElement>,
    activeNum0: Int
) : RecyclerView.Adapter<ExchangeAdapter.ViewHolder>() {

    private var activeNum = activeNum0
    private val elemList = elemList0
    private val viewHoldersList = mutableListOf<ViewHolder>()
    var isActive = false

    private val layoutsList = listOf(
        R.layout.item__exchange_elem,
        R.layout.item__exchange_elem_active
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutNum = if (viewType == 1) { 1 } else { 0 }
        isActive = viewType == 1
        val root = LayoutInflater.from(parent.context)
            .inflate(layoutsList[layoutNum], parent, false)
        val viewHolder = ViewHolder(root)
        viewHolder.relativeLayout.setOnClickListener {
            isActive = !isActive
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
        val relativeLayout = root as RelativeLayout
        var elementName: TextView = if (isActive) { root.exchange_elem__nameA } else { root.exchange_elem__name }
        var elementSign: TextView = if (isActive) { root.exchange_elem__signA } else { root.exchange_elem__sign }
    }
}
