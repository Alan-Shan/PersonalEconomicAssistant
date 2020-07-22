package top.ilum.pea.ui.stock

import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.fragment_stock.*
import top.ilum.pea.R
import top.ilum.pea.utils.Status
import java.util.Calendar
import java.util.Date
import kotlin.collections.ArrayList

class StockFragment : Fragment(), StockDialog.StockChange {

    private lateinit var viewModel: StockViewModel
    fun getCandle(symbol: String, resolution: String, from: Long, to: Long) {

        viewModel.getCandle(symbol, resolution, from, to).observe(
            viewLifecycleOwner,
            Observer {
                when (it.status) {

                    Status.LOADING -> {
                    }
                    Status.SUCCESS -> {
                        val closePrices = it.data!!.closePrices
                        fun setData(count: Int, range: Float) {
                            val values: ArrayList<Entry> = ArrayList()
                            for (i in 0 until count) {
                                values.add(
                                    Entry(i.toFloat(), closePrices[i].toFloat())
                                )
                            }
                            val set1: LineDataSet
                            if (stock_chart.data != null &&
                                stock_chart.data.dataSetCount > 0
                            ) {
                                set1 = stock_chart.data.getDataSetByIndex(0) as LineDataSet
                                set1.values = values
                                set1.notifyDataSetChanged()
                                stock_chart.data.notifyDataChanged()
                                stock_chart.notifyDataSetChanged()
                            } else {
                                set1 = LineDataSet(values, "DataSet 1")
                                set1.setDrawIcons(false)

                                set1.enableDashedLine(10f, 5f, 0f)

                                set1.color = Color.BLACK
                                set1.setCircleColor(Color.BLACK)

                                set1.lineWidth = 1f
                                set1.circleRadius = 1f

                                set1.setDrawCircleHole(false)

                                set1.formLineWidth = 1f
                                set1.formLineDashEffect =
                                    DashPathEffect(floatArrayOf(10f, 5f), 0f)
                                set1.formSize = 10f

                                set1.valueTextSize = 9f

                                set1.enableDashedHighlightLine(10f, 5f, 0f)

                                set1.setDrawFilled(true)
                                set1.fillFormatter =
                                    IFillFormatter { _, _ -> stock_chart.axisLeft.axisMinimum }

                                val dataSets: ArrayList<ILineDataSet> = ArrayList()
                                dataSets.add(set1)

                                val data = LineData(dataSets)

                                stock_chart.data = data
                            }
                        }

                        stock_chart.setBackgroundColor(Color.WHITE)
                        stock_chart.description.isEnabled = false
                        stock_chart.setTouchEnabled(true)
                        stock_chart.setPinchZoom(true)

                        val xAxis = stock_chart.xAxis
                        xAxis.enableGridDashedLine(10f, 10f, 0f)
                        val yAxis = stock_chart.axisLeft
                        stock_chart.axisRight.isEnabled = false
                        yAxis.enableGridDashedLine(10f, 10f, 0f)

                        yAxis.axisMaximum = closePrices.max()!!.toFloat()  // вот здесь
                        yAxis.axisMinimum = closePrices.min()!!.toFloat()    // и вот здесь
                        setData(closePrices.size, 180f)   // а ещё вот здесь
                    }
                    Status.ERROR -> {
                        Log.e("ERROR!", it.message.toString())
                    }
                }
            }
        )
    }

    fun getQuote(symbol: String, name: String) {
        txt_stock.text = name
        viewModel.getQuote(symbol).observe(
            viewLifecycleOwner,
            Observer {
                when (it.status) {

                    Status.LOADING -> {
                    }
                    Status.SUCCESS -> {
                    }
                    Status.ERROR -> {
                        Log.e("ERROR!", it.message.toString())
                    }
                }
            }
        )
    }

    private fun dataChanged(symbol: String, name: String) {

        getQuote(symbol, name)
        val calendar = Calendar.getInstance()
        calendar.time = Date(System.currentTimeMillis())
        calendar.add(Calendar.DATE, -1)
        getCandle(symbol, "60", calendar.timeInMillis / 1000, System.currentTimeMillis() / 1000)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        viewModel = ViewModelProvider(
            this,
            StockViewModelFactory(StockApiHelper(RetrofitBuilder.apiService))
        )
            .get(StockViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_stock, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.dailyData.observe(
            viewLifecycleOwner,
            Observer {
                txt_stock_val.text = it.currentPrice.toString()
                val divideBy = if (it.currentPrice > it.previousClosePrice) {
                    it.currentPrice
                } else {
                    it.previousClosePrice
                }
                val percentage =
                    "%.2f".format((it.currentPrice - it.previousClosePrice) / divideBy * 100)
                val stockChange = "%.2f".format(it.currentPrice - it.previousClosePrice)
                val stockValDisplay = "$stockChange ($percentage%)"
                txt_stock_val_change.text = stockValDisplay
                if (stockChange.toDouble() <= 0)
                    txt_stock_val_change.setTextColor(Color.rgb(255, 63, 16))
                else
                    txt_stock_val_change.setTextColor(Color.rgb(3, 217, 44))
                txtValClosing.text = it.previousClosePrice.toString()
                txtValOpening.text = it.openPrice.toString()
                val range = "${it.lowPriceDay} - ${it.highPriceDay}"
                txtValRange.text = range
            }
        )

        fun getSymbols() {
            viewModel.getSymbols().observe(
                viewLifecycleOwner,
                Observer { it1 ->
                    when (it1.status) {

                        Status.LOADING -> {
                        }
                        Status.SUCCESS -> {
                        }
                        Status.ERROR -> {
                            Log.e("ERROR!", it1.message.toString())
                        }
                    }
                }
            )
        }

        dataChanged("AAPL", "Apple")
        btnWatchOther.setOnClickListener {
            val stockMenu = StockDialog()
            stockMenu.setTargetFragment(
                this,
                22
            )
            fragmentManager?.let { it1 -> stockMenu.show(it1, "Dialog") }
        }
    }

    override fun sendInput(input: String, name: String) {
        dataChanged(input, name)
    }
}
