package top.ilum.pea.ui.exchange

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_exchange.*
import org.jsoup.Jsoup
import top.ilum.pea.MainActivity
import top.ilum.pea.R
import top.ilum.pea.data.ExchangeElement
import java.text.DecimalFormat
import java.util.*
import kotlin.math.roundToLong

class ExchangeFragment : Fragment() {

    lateinit var secThread: Thread
    lateinit var runable: Runnable

    private val siteName = "https://www.cbr-xml-daily.ru/daily_utf8.xml"

    private var leftActive = 0
    private var rightActive = 0

    private val exchangeList = mutableListOf<ExchangeElement>()

    lateinit var leftElem: ExchangeElement
    lateinit var rightElem: ExchangeElement

    var handler: Handler = Handler(Looper.getMainLooper())

    var leftAdapter = ExchangeAdapter(exchangeList, leftActive, isLeft = true, exchangeParent = this)
    var rightAdapter = ExchangeAdapter(exchangeList, rightActive, isLeft = false, exchangeParent = this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_exchange, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadExchanges()

        buildAdapters()
    }

    private fun onSuccessLoad() {
        leftElem = exchangeList[leftActive]
        rightElem = exchangeList[rightActive]
        changeElems(leftActive, left = true)
        changeElems(rightActive, left = false)
        buildAdapters()
        exchanges__input.addTextChangedListener {
            changeValues()
        }
    }

    private fun buildAdapters() {
        leftAdapter = ExchangeAdapter(exchangeList, leftActive, isLeft = true, exchangeParent = this)
        rightAdapter = ExchangeAdapter(exchangeList, rightActive, isLeft = false, exchangeParent = this)

        exchanges__recyclerLeft.layoutManager = LinearLayoutManager(MainActivity())
        exchanges__recyclerLeft.adapter = leftAdapter

        exchanges__recyclerRight.layoutManager = LinearLayoutManager(MainActivity())
        exchanges__recyclerRight.adapter = rightAdapter
    }

    private fun getValueText(num: Double, pattern: String = "#.###"): String {
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
            leftElem = exchangeList[activeNum]
        } else {
            rightElem = exchangeList[activeNum]
        }
        val rate = leftElem.value / rightElem.value
        changeValues(rate)
        exchanges__rate.text = "1 ${leftElem.sign} = ${getValueText(rate, "#.######")} ${rightElem.sign}"
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
                "${getValueText(inputValue)} ${leftElem.sign} = ${getValueText(inputValue * rate)} ${rightElem.sign}"
        }
    }

    private fun loadExchanges() {
        runable = Runnable {
            fun runMe() {
                fun calcElemRate(element: org.jsoup.nodes.Element): Double {
                    var value = element.getElementsByTag("Value")[0].text()
                        .replace(",", ".").toDouble()
                    val nominal = element.getElementsByTag("Nominal")[0].text()
                        .replace(",", ".").toDouble()
                    value /= nominal
                    return value
                }
                Looper.prepare()
                val webDoc: org.jsoup.nodes.Document
                try {
                    webDoc = Jsoup.connect(siteName).get()
                } catch (e: Exception){
                    handler.post(Runnable {
                        run {
                            Toast.makeText(
                                this.context,
                                getText(R.string.exchange__badLoad),
                                Toast.LENGTH_SHORT).show() }
                    })
                    return
                }
                val allCurrency = webDoc.getElementsByTag("Valute")
                println(allCurrency[0])
                allCurrency.forEach {
                    val charCode = it.getElementsByTag("CharCode")[0].text().toUpperCase(Locale.ROOT)
                    val value = calcElemRate(it)
                    val name = it.getElementsByTag("Name")[0].text()
                    val sign = charCode
//                    var sign = ""
//                    if (signMap.containsKey(charCode)) {
//                        sign = signMap[charCode].toString()
//                    }
                    val exchangeElement = ExchangeElement(name, value, sign)
                    exchangeList.add(exchangeElement)
                }
                handler.post(Runnable {
                    run {
                        Log.d("Test", "thread go brr")
                        onSuccessLoad()
                        Toast.makeText(
                            this.context,
                            getText(R.string.exchange__successLoad),
                            Toast.LENGTH_SHORT).show()
                    }
                })
            }
            runMe()
        }
        secThread = Thread(runable)
        secThread.start()
    }

    private var signMap = mutableMapOf(
        "USD" to "$",
        "CAD" to "$",
        "EUR" to "€",
        "AED" to "د.إ.‏",
        "AFN" to "؋",
        "ALL" to "Lek",
        "AMD" to "դր.",
        "ARS" to "$",
        "AUD" to "$",
        "AZN" to "ман.",
        "BAM" to "KM",
        "BDT" to "৳",
        "BGN" to "лв.",
        "BHD" to "د.ب.‏",
        "BIF" to "FBu",
        "BND" to "$",
        "BOB" to "Bs",
        "BRL" to "R$",
        "BWP" to "P",
        "BYN" to "руб.",
        "BZD" to "$",
        "CDF" to "FrCD",
        "CHF" to "CHF",
        "CLP" to "$",
        "CNY" to "CN¥",
        "COP" to "$",
        "CRC" to "₡",
        "CVE" to "CV$",
        "CZK" to "Kč",
        "DJF" to "Fdj",
        "DKK" to "kr",
        "DOP" to "RD$",
        "DZD" to "د.ج.‏",
        "EEK" to "kr",
        "EGP" to "ج.م.‏",
        "ERN" to "Nfk",
        "ETB" to "Br",
        "GBP" to "£",
        "GEL" to "GEL",
        "GHS" to "GH₵",
        "GNF" to "FG",
        "GTQ" to "Q",
        "HKD" to "$",
        "HNL" to "L",
        "HRK" to "kn",
        "HUF" to "Ft",
        "IDR" to "Rp",
        "ILS" to "₪",
        "INR" to "টকা",
        "IQD" to "د.ع.‏",
        "IRR" to "﷼",
        "ISK" to "kr",
        "JMD" to "$",
        "JOD" to "د.أ.‏",
        "JPY" to "￥",
        "KES" to "Ksh",
        "KHR" to "៛",
        "KMF" to "FC",
        "KRW" to "₩",
        "KWD" to "د.ك.‏",
        "KZT" to "тңг.",
        "LBP" to "ل.ل.‏",
        "LKR" to "SL Re",
        "LTL" to "Lt",
        "LVL" to "Ls",
        "LYD" to "د.ل.‏",
        "MAD" to "د.م.‏",
        "MDL" to "MDL",
        "MGA" to "MGA",
        "MKD" to "MKD",
        "MMK" to "K",
        "MOP" to "MOP$",
        "MUR" to "MURs",
        "MXN" to "$",
        "MYR" to "RM",
        "MZN" to "MTn",
        "NAD" to "N$",
        "NGN" to "₦",
        "NIO" to "C$",
        "NOK" to "kr",
        "NPR" to "नेरू",
        "NZD" to "$",
        "OMR" to "ر.ع.‏",
        "PAB" to "B/.",
        "PEN" to "S/.",
        "PHP" to "₱",
        "PKR" to "₨",
        "PLN" to "zł",
        "PYG" to "₲",
        "QAR" to "ر.ق.‏",
        "RON" to "RON",
        "RSD" to "дин.",
        "RUB" to "₽.",
        "RWF" to "FR",
        "SAR" to "ر.س.‏",
        "SDG" to "SDG",
        "SEK" to "kr",
        "SGD" to "$",
        "SOS" to "Ssh",
        "SYP" to "ل.س.‏",
        "THB" to "฿",
        "TND" to "د.ت.‏",
        "TOP" to "T$",
        "TRY" to "TL",
        "TTD" to "$",
        "TWD" to "NT$",
        "TZS" to "TSh",
        "UAH" to "₴",
        "UGX" to "USh",
        "UYU" to "$",
        "UZS" to "UZS",
        "VEF" to "Bs.F.",
        "VND" to "₫",
        "XAF" to "FCFA",
        "XOF" to "CFA",
        "YER" to "ر.ي.‏",
        "ZAR" to "R",
        "ZMK" to "ZK",
        "ZWL" to "ZWL$"
    )
}
