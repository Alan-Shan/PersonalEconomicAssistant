package top.ilum.pea.ui.calculators.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_credit.*
import kotlinx.android.synthetic.main.fragment_mortgage.*
import top.ilum.pea.R
import top.ilum.pea.ui.Format.Companion.formatNumber
import java.lang.Exception
import kotlin.math.pow

class MortgageFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        inflater.inflate(R.layout.fragment_mortgage, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        groupMortgage.visibility = Group.INVISIBLE
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.types,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        btnCalculateMortgage.setOnClickListener {
            try {
                val sum = txtSumMortgage.editText?.text.toString().toDouble()
                val percent = txtRateMortgage.editText?.text.toString().toDouble() / 100 / 12
                val period = txtPeriodMortgage.editText?.text.toString().toDouble() * 12
                when (spinner.selectedItemPosition) {
                    0 -> calculateDifferentiated(sum, percent, period)
                    1 -> calculateAnnuity(sum, percent, period)
                }
                groupMortgage.visibility = Group.VISIBLE
            } catch (e: Exception) {
                Toast.makeText(context, getString(R.string.fill_fields), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calculateAnnuity(
        sum: Double,
        percent: Double,
        period: Double
    ) {
        val value = (1 + percent).pow(period)
        val month = sum * (percent + percent / (value - 1))
        val totalPayment = month * period
        txtValMonthMortgage.text = formatNumber(month)
        txtValTotalMortgage.text = formatNumber(totalPayment)
        txtValOverMortgage.text = formatNumber(totalPayment - sum)
    }

    private fun calculateDifferentiated(
        sum: Double,
        percent: Double,
        period: Double
    ) {
        var months = period
        val value = sum / months
        var rest = sum
        val list = arrayListOf<Double>()
        while (months > 0) {
            val mp = value + (rest * percent)
            list.add(mp)
            rest -= value
            months--
        }
        val total = list.sum()
        txtValMonthMortgage.text = formatNumber(list[0]) + " ... " + formatNumber(list[list.size - 1])
        txtValTotalMortgage.text = formatNumber(total)
        txtValOverMortgage.text = formatNumber(total - sum)
    }
}
