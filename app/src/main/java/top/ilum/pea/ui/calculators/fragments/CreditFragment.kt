package top.ilum.pea.ui.calculators.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_credit.*
import top.ilum.pea.R
import top.ilum.pea.ui.Format.Companion.formatNumber
import java.lang.Exception
import kotlin.math.pow

class CreditFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        inflater.inflate(R.layout.fragment_credit, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        group.visibility = Group.INVISIBLE
        btnCalculate.setOnClickListener {
            try {
                calculate()
                group.visibility = Group.VISIBLE
            } catch (e: Exception) {
                Toast.makeText(context, getString(R.string.fill_fields), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calculate() {
        val sum = txtSum.editText?.text.toString().toDouble()
        val percent = txtRate.editText?.text.toString().toDouble() / 100 / 12
        val period = txtPeriod.editText?.text.toString().toDouble() * 12
        val value = (1 + percent).pow(period)
        val month = sum * (percent + percent / (value - 1))
        val totalPayment = month * period
        txtValTotal.text = formatNumber(totalPayment)
        txtValOver.text = formatNumber(totalPayment - sum)
        txtValMonth.text = formatNumber(month)
    }
}
