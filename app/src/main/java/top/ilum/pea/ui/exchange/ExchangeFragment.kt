package top.ilum.pea.ui.exchange

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_exchange.*
import org.jsoup.Jsoup
import top.ilum.pea.MainActivity
import top.ilum.pea.R
import top.ilum.pea.data.ExchangeElement
import java.text.DecimalFormat
import kotlin.math.roundToLong

class ExchangeFragment : Fragment() {

    private lateinit var secThread: Thread
    private lateinit var runable: Runnable

    private val siteName = "https://www.cbr-xml-daily.ru/daily_utf8.xml"

    private var leftActive = 0
    private var rightActive = 0

    private val exchangeList = mutableListOf<ExchangeElement>()

    private lateinit var leftElem: ExchangeElement
    private lateinit var rightElem: ExchangeElement

    private var handler: Handler = Handler(Looper.getMainLooper())

//    private lateinit var leftAdapter: RecyclerView.Adapter<ExchangeAdapter.ViewHolder>
//    private lateinit var rightAdapter: RecyclerView.Adapter<ExchangeAdapter.ViewHolder>

    private var leftAdapter =
        ExchangeAdapter(exchangeList, leftActive, isLeft = true, exchangeParent = this)
    private var rightAdapter =
        ExchangeAdapter(exchangeList, rightActive, isLeft = false, exchangeParent = this)

    private lateinit var leftLayoutManager: LinearLayoutManager
    private lateinit var rightLayoutManager: LinearLayoutManager

    private var wasExchangesLoaded = false

//    private var firstRebuild = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_exchange, container, false)

    override fun onPause() {
        super.onPause()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val intentFilter: IntentFilter = IntentFilter()
//        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//        registerReceiver(networkChangeReceiver, intentFilter);

        loadExchanges()

        buildAdapters()
    }

    private fun onSuccessLoad() {
        exchanges__noConnection.text = ""
//        if (!firstRebuild) {
//            leftElem = exchangeList[leftActive]
//            rightElem = exchangeList[rightActive]
//        } else {
//            firstRebuild = false
//        }
        leftElem = exchangeList[leftActive]
        rightElem = exchangeList[rightActive]
        changeElems(leftActive, left = true)
        changeElems(rightActive, left = false)
        rebuildAdapters()
        exchanges__input.addTextChangedListener {
            changeValues()
        }
    }

    private fun buildAdapters() {
        leftLayoutManager = LinearLayoutManager(MainActivity())
        rightLayoutManager = LinearLayoutManager(MainActivity())

        leftAdapter = ExchangeAdapter(exchangeList, leftActive, isLeft = true, exchangeParent = this)
        rightAdapter = ExchangeAdapter(exchangeList, rightActive, isLeft = false, exchangeParent = this)

        exchanges__recyclerLeft.layoutManager = leftLayoutManager
        exchanges__recyclerLeft.adapter = leftAdapter

        exchanges__recyclerRight.layoutManager = rightLayoutManager
        exchanges__recyclerRight.adapter = rightAdapter
    }

    private fun rebuildAdapters() {
        leftAdapter = ExchangeAdapter(exchangeList, leftActive, isLeft = true, exchangeParent = this)
        rightAdapter = ExchangeAdapter(exchangeList, rightActive, isLeft = false, exchangeParent = this)

        exchanges__recyclerLeft.adapter = leftAdapter
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
                if (!wasExchangesLoaded) {
                    handler.post {
                        run {
                            exchanges__loading.visibility = View.VISIBLE
                        }
                    }
                }
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
                    wasExchangesLoaded = true
                } catch (e: Exception) {
                    if (!wasExchangesLoaded) {
                        handler.post {
                            run {
                                exchanges__noConnection.text = getText(R.string.exchange__badLoad)
                            }
                        }
                    }
                    return
                }
                val allCurrency = webDoc.getElementsByTag("Valute")
                println(allCurrency[0])
                allCurrency.forEach {
                    val charCode = it.getElementsByTag("CharCode")[0].text().toUpperCase()
                    val value = calcElemRate(it)
                    val name = it.getElementsByTag("Name")[0].text()
//                    val sign = charCode
//                    var sign = ""
//                    if (signMap.containsKey(charCode)) {
//                        sign = signMap[charCode].toString()
//                    }
                    val exchangeElement = ExchangeElement(name, value, charCode)
                    exchangeList.add(exchangeElement)
                }
                handler.post {
                    run {
                        exchanges__loading.visibility = View.GONE
                    }
                }
                handler.post {
                    run {
                        Log.d("Test", "thread go brr")
                        onSuccessLoad()
                    }
                }
            }
            runMe()
        }
        secThread = Thread(runable)
        secThread.start()
    }

//    private val networkChangeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            loadExchanges()
//        }
//    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("wasLoaded", wasExchangesLoaded)
        outState.putInt("leftActive", leftAdapter.activeNum)
        outState.putInt("rightActive", rightAdapter.activeNum)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            wasExchangesLoaded = savedInstanceState.getBoolean("wasLoaded")
            leftActive = savedInstanceState.getInt("leftActive")
            rightActive = savedInstanceState.getInt("rightActive")
        }
        super.onViewStateRestored(savedInstanceState)
    }
}
