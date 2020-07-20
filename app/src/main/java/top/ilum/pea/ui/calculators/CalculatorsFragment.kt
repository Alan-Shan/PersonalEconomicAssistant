package top.ilum.pea.ui.calculators

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_calculators.view.*
import top.ilum.pea.R
import top.ilum.pea.ui.calculators.fragments.CreditFragment
import top.ilum.pea.ui.calculators.fragments.DepositFragment
import top.ilum.pea.ui.calculators.fragments.MortgageFragment

class CalculatorsFragment : Fragment() {

    private lateinit var adapter: CalculatorAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calculators, container, false)
        adapter = CalculatorAdapter(
            listOf(
                getString(R.string.credit),
                getString(R.string.deposit),
                getString(R.string.mortgage)
            ),
            listOf(
                getString(R.string.descriptionCredit),
                getString(R.string.descriptionDeposit),
                getString(R.string.descriptionMortgage)
            )
        )
        view.recyclerCalculator.layoutManager = LinearLayoutManager(context)
        view.recyclerCalculator.adapter = adapter
        return view.recyclerCalculator
    }
}
