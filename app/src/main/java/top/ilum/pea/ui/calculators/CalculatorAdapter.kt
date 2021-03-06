package top.ilum.pea.ui.calculators

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.calculator_view.view.*
import top.ilum.pea.R

class CalculatorAdapter(
    private var items: List<String>,
    private var descriptions: List<String>
) :
    RecyclerView.Adapter<CalculatorAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
        Holder(LayoutInflater.from(parent.context).inflate(R.layout.calculator_view, parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.onBind(items[position], descriptions[position])
        holder.view.setOnClickListener {
            it
                .findNavController()
                .navigate(
                    when (position) {
                        0 -> R.id.action_calcScreen_to_creditCalcScreen
                        1 -> R.id.action_calcScreen_to_depositCalcScreen
                        else -> R.id.action_calcScreen_to_mortgageCalcScreen
                    }
                )
        }
    }

    inner class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        fun onBind(string: String, description: String) {
            view.apply {
                txtCalc.text = string
                txtCalcDescription.text = description
            }
        }
    }
}
