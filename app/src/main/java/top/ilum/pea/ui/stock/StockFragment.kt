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


class StockFragment : Fragment() {

    private lateinit var viewModel: StockViewModel

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

        fun getCandle(symbol: String, resolution: String, from: Int, to: Int) {

            viewModel.getCandle(symbol, resolution, from, to).observe(
                viewLifecycleOwner,
                Observer {
                    when (it.status) {

                        Status.LOADING -> {
                        }
                        Status.SUCCESS -> {
                            Log.e("data", it.data.toString())
                        }
                        Status.ERROR -> {
                            Log.e("ERROR!", it.message.toString())
                        }
                    }
                }
            )
        }

        fun getQuote(symbol: String) {

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

        // // Chart Style // //

        // background color
        stock_chart.setBackgroundColor(Color.WHITE)

        // disable description text
        stock_chart.description.isEnabled = false

        // enable touch gestures
        stock_chart.setTouchEnabled(true)

        // force pinch zoom along both axis
        stock_chart.setPinchZoom(true)

        val xAxis = stock_chart.xAxis
        xAxis.enableGridDashedLine(10f, 10f, 0f)

        val yAxis = stock_chart.axisLeft

        // disable dual axis (only use LEFT axis)
        stock_chart.axisRight.isEnabled = false

        // horizontal grid lines
        yAxis.enableGridDashedLine(10f, 10f, 0f)

        // axis range
        yAxis.axisMaximum = 200f    //вот здесь
        yAxis.axisMinimum = -50f    //и вот здесь
        setData(10, 180f)   //а ещё вот здесь
    }

    private fun setData(count: Int, range: Float) {
        val values: ArrayList<Entry> = ArrayList()
        for (i in 0 until count) {
            val `val` = (Math.random() * range).toFloat() - 30 //ну и вот здесь
            values.add(
                Entry(i.toFloat(), `val`)
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
            // create a dataset and give it a type
            set1 = LineDataSet(values, "DataSet 1")
            set1.setDrawIcons(false)

            // draw dashed line
            set1.enableDashedLine(10f, 5f, 0f)

            // black lines and points
            set1.color = Color.BLACK
            set1.setCircleColor(Color.BLACK)

            // line thickness and point size
            set1.lineWidth = 1f
            set1.circleRadius = 1f

            // draw points as solid circles
            set1.setDrawCircleHole(false)

            // customize legend entry
            set1.formLineWidth = 1f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set1.formSize = 10f

            //text size of values
            set1.valueTextSize = 9f

            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f)

            // set the filled area
            set1.setDrawFilled(true)
            set1.fillFormatter =
                IFillFormatter { _, _ -> stock_chart.axisLeft.axisMinimum }

            val dataSets: ArrayList<ILineDataSet> = ArrayList()
            dataSets.add(set1) // add the data sets

            // create a data object with the data sets
            val data = LineData(dataSets)

            // set data
            stock_chart.data = data
        }
    }
}
