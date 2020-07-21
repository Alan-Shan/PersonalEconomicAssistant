package top.ilum.pea.ui.calculators.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_deposit.*
import top.ilum.pea.R
import top.ilum.pea.ui.formatNumber
import java.lang.Exception

class DepositFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        inflater.inflate(R.layout.fragment_deposit, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        groupDeposit.visibility = Group.INVISIBLE
        btnCalculateDeposit.setOnClickListener {
            try {
                calculate()
                groupDeposit.visibility = Group.VISIBLE
            } catch (e: Exception) {
                Toast.makeText(context, getString(R.string.fill_fields), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calculate() {
        val sum = txtSizeContribution.editText?.text.toString().toDouble()
        val period = txtPeriodDeposit.editText?.text.toString().toDouble()
        val rate = txtRateDeposit.editText?.text.toString().toDouble()
        val profit = (sum * rate * period) / 100
        txtResultsValue.text = formatNumber(sum + profit)
    }
}
