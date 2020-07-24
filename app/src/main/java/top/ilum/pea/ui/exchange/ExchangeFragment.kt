package top.ilum.pea.ui.exchange

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_exchange.*
import org.jsoup.Jsoup
import top.ilum.pea.MainActivity
import top.ilum.pea.R
import top.ilum.pea.data.ExchangeElement
import top.ilum.pea.ui.home.NewsAdapter
import top.ilum.pea.utils.SharedViewModel
import java.text.DecimalFormat
import java.util.Locale
import kotlin.math.roundToLong
import kotlin.properties.Delegates

class ExchangeFragment : Fragment() {

    private lateinit var secThread: Thread
    private lateinit var runable: Runnable
    private lateinit var sharedViewModel: SharedViewModel

    private val siteName = "https://www.cbr-xml-daily.ru/daily_utf8.xml"

    private var leftActive = 0
    private var rightActive = 0

    private var cachedLeft = 99999

    private var cachedRight = 99999

    private val exchangeList = mutableListOf<ExchangeElement>()

    private lateinit var leftElem: ExchangeElement
    private lateinit var rightElem: ExchangeElement

    private var handler: Handler = Handler(Looper.getMainLooper())

    private var leftAdapter =
        ExchangeAdapter(exchangeList, leftActive, isLeft = true, exchangeParent = this)
    private var rightAdapter =
        ExchangeAdapter(exchangeList, rightActive, isLeft = false, exchangeParent = this)

    private lateinit var leftLayoutManager: LinearLayoutManager
    private lateinit var rightLayoutManager: LinearLayoutManager

    private var wasExchangesLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (rightAdapter).stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        (leftAdapter).stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_exchange, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        loadExchanges()

        buildAdapters()
    }

    private fun onSuccessLoad() {
        exchanges__noConnection.visibility = View.GONE

        leftElem = exchangeList[leftActive]
        rightElem = exchangeList[rightActive]
        if (cachedLeft != 99999 && cachedRight != 99999) {
            leftActive = cachedRight
            rightActive = cachedLeft
        }
        changeElems(leftActive, left = true)
        changeElems(rightActive, left = false)
        cachedLeft = 99999
        cachedRight = 99999
        rebuildAdapters()
        exchanges__input.addTextChangedListener {
            changeValues()
        }
        if (sharedViewModel.connectivityStatus.hasObservers()) {
            sharedViewModel.connectivityStatus.removeObservers(viewLifecycleOwner)
        }
    }

    private fun onUnsuccessfulLoad() {
        sharedViewModel.connectivityStatus.observe(
            viewLifecycleOwner,
            Observer { itStat ->
                if (itStat) {
                    exchanges__noConnection.visibility = View.GONE
                    loadExchanges()
                }
            }
        )
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
            cachedLeft = activeNum
        } else {
            rightElem = exchangeList[activeNum]
            cachedRight = activeNum
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
                                exchanges__noConnection.visibility = View.VISIBLE
                                exchanges__loading.visibility = View.GONE
                                onUnsuccessfulLoad()
                            }
                        }
                    }
                    return
                }
                val allCurrency = webDoc.getElementsByTag("Valute")
                allCurrency.forEach {
                    val charCode = it.getElementsByTag("CharCode")[0].text().toUpperCase(Locale.ROOT)
                    val value = calcElemRate(it)
                    val name = it.getElementsByTag("Name")[0].text()
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
                        onSuccessLoad()
                    }
                }
            }
            runMe()
        }
        secThread = Thread(runable)
        secThread.start()
    }

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
