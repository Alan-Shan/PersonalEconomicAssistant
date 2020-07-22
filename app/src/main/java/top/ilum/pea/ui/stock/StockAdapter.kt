package top.ilum.pea.ui.stock

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.stock_item.view.*
import top.ilum.pea.R
import top.ilum.pea.data.Symbols

class StockAdapter(private val onClick: (data: Symbols) -> Unit) :
    RecyclerView.Adapter<StockAdapter.ViewHolder>() {
    var list: List<Symbols> = listOf()

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.stock_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.stockName.text = if (list[position].description.isNotEmpty()) {
            list[position].description
        } else {
            list[position].symbol
        }
        holder.card.setOnClickListener {
            onClick(list[position])
        }
    }

    inner class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {

        var stockName: TextView = root.stockname
        var card: CardView = root.findViewById(R.id.stockcard)
    }
}
