package top.ilum.pea.ui.exchange

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_exchange.*
import top.ilum.pea.MainActivity
import top.ilum.pea.R
import top.ilum.pea.data.ExchangeElement
import java.lang.Exception
import java.text.DecimalFormat
import kotlin.math.roundToLong

class ExchangeFragment : Fragment() {

    private val siteName = "https://www.cbr-xml-daily.ru/daily_utf8.xml"

    private var leftActive = 0
    private var rightActive = 3

    private val testExchangeList = listOf(
        ExchangeElement("Российский Рубль", 0.014, "₽"),
        ExchangeElement("Евро", 1.14, "€"),
        ExchangeElement("Американский Доллар", 1.0, "\$"),
        ExchangeElement("Японская Иена", 0.0093, "¥"),
        ExchangeElement("Британский фунт", 1.27, "£"),
        ExchangeElement("Евро", 1.14, "€"),
        ExchangeElement("Американский Доллар", 1.0, "\$"),
        ExchangeElement("Японская Иена", 0.0093, "¥"),
        ExchangeElement("Британский фунт", 1.27, "£"),
        ExchangeElement("Евро", 1.14, "€"),
        ExchangeElement("Американский Доллар", 1.0, "\$"),
        ExchangeElement("Японская Иена", 0.0093, "¥"),
        ExchangeElement("Британский фунт", 1.27, "£"),
        ExchangeElement("Евро", 1.14, "€"),
        ExchangeElement("Американский Доллар", 1.0, "\$"),
        ExchangeElement("Японская Иена", 0.0093, "¥"),
        ExchangeElement("Британский фунт", 1.27, "£"),
        ExchangeElement("Евро", 1.14, "€"),
        ExchangeElement("Американский Доллар", 1.0, "\$"),
        ExchangeElement("Японская Иена", 0.0093, "¥"),
        ExchangeElement("Британский фунт", 1.27, "£"),
        ExchangeElement("Евро", 1.14, "€"),
        ExchangeElement("Американский Доллар", 1.0, "\$"),
        ExchangeElement("Японская Иена", 0.0093, "¥"),
        ExchangeElement("Британский фунт", 1.27, "£"),
        ExchangeElement("Евро", 1.14, "€"),
        ExchangeElement("Американский Доллар", 1.0, "\$"),
        ExchangeElement("Японская Иена", 0.0093, "¥"),
        ExchangeElement("Британский фунт", 1.27, "£"),
        ExchangeElement("Евро", 1.14, "€"),
        ExchangeElement("Американский Доллар", 1.0, "\$"),
        ExchangeElement("Японская Иена", 0.0093, "¥"),
        ExchangeElement("Британский фунт", 1.27, "£"),
        ExchangeElement("Швейцарский франк", 1.07, "₣")
    )

    var leftElem = testExchangeList[leftActive]
    var rightElem = testExchangeList[rightActive]

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_exchange, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val leftAdapter = ExchangeAdapter(testExchangeList, leftActive, isLeft = true, exchangeParent = this)
        val rightAdapter = ExchangeAdapter(testExchangeList, rightActive, isLeft = false, exchangeParent = this)

        exchanges__recyclerLeft.layoutManager = LinearLayoutManager(MainActivity())
        exchanges__recyclerLeft.adapter = leftAdapter

        exchanges__recyclerRight.layoutManager = LinearLayoutManager(MainActivity())
        exchanges__recyclerRight.adapter = rightAdapter

        exchanges__input.addTextChangedListener {
            changeValues()
        }
    }

    fun getValueText(num: Double, pattern: String = "#.###"): String {
        val df = DecimalFormat(pattern)
        return if (num == num.roundToLong().toDouble()) {
            num.roundToLong().toString()
        } else {
            df.format(num).toString()
        }
    }

    @SuppressLint("SetTextI18n")
    fun changeElems(activeNum: Int, left: Boolean = true) {
        if (left) {
            leftElem = testExchangeList[activeNum]
        } else {
            rightElem = testExchangeList[activeNum]
        }
        val rate = leftElem.value / rightElem.value
        changeValues(rate)
        exchanges__rate.text = "1${leftElem.sign} = ${getValueText(rate, "#.######")}${rightElem.sign}"
    }

    @SuppressLint("SetTextI18n")
    fun changeValues(setRate: Double? = null) {
        var inputValue = 1.0
        val inputText = exchanges__input.text.toString()
        var badText = false
        if (inputText != "") {
            try {
                inputValue = inputText.toDouble()
            } catch (e: Exception) {
                badText = true
            }
        }
        if (!badText) {
            val rate = setRate ?: leftElem.value / rightElem.value
            exchanges__finalValue.text =
                "${getValueText(inputValue)}${leftElem.sign} = ${getValueText(inputValue * rate)}${rightElem.sign}"
        }
    }
}
