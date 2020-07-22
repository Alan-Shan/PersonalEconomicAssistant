package top.ilum.pea.ui.stock

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.stock_dialog_layout.*
import top.ilum.pea.R
import top.ilum.pea.data.StockAdapterData
import top.ilum.pea.data.Symbols
import top.ilum.pea.utils.Status

class StockDialog : DialogFragment() {

    interface StockChange {
        fun sendInput(input: String, name: String)
    }

    private lateinit var viewModel: StockViewModel

    private var stockChange: StockChange? = null

    private lateinit var stillNotLoaded: List<Symbols>

    private var onTheGo: MutableList<StockAdapterData> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.stock_dialog_layout, container, false)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        stockChange = targetFragment as StockChange
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            StockViewModelFactory(StockApiHelper(RetrofitBuilder.apiService))
        )
            .get(StockViewModel::class.java)

        fun loadMore() {
            val temp: List<Symbols> = stillNotLoaded.take(15)
            stillNotLoaded = stillNotLoaded.drop(15)
            temp.forEach { it1 ->
                viewModel.getQuote(it1.symbol).observe(
                    viewLifecycleOwner,
                    Observer {
                        when (it.status) {

                            Status.LOADING -> {
                            }
                            Status.SUCCESS -> {
                                val name = if (it1.description.isNotEmpty()) { it1.description } else { it1.symbol }
                                onTheGo.add(StockAdapterData(name, it.data!!.currentPrice.toString(), "%.2f".format(it.data.currentPrice - it.data.previousClosePrice), it1.symbol))
                                if (onTheGo.size.rem(15) == 0) {
                                    (stockselectionrecycler.adapter as StockAdapter).apply { list = onTheGo }.notifyDataSetChanged()
                                }
                            }
                            Status.ERROR -> {
                                Log.e("ERROR!", it.message.toString())
                            }
                        }
                    }
                )
            }
        }

        fun loadEm() {
            var isLoading: Boolean = false

            stockselectionrecycler.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = StockAdapter {
                    stockChange!!.sendInput(it[0], it[1])
                    dismiss()
                }.apply {
                    list = onTheGo
                }

                this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)

                        if ((layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition() == onTheGo.size - 1) {
                            loadMore()
                            isLoading = true
                        }
                    }
                })
            }
        }

        viewModel.getSymbols().observe(
            this,
            Observer { it2 ->
                when (it2.status) {
                    Status.SUCCESS -> {
                        stillNotLoaded = it2.data!!
                        val temp = stillNotLoaded.take(15)
                        stillNotLoaded = stillNotLoaded.drop(15)
                        temp.forEach { it1 ->
                            viewModel.getQuote(it1.symbol).observe(
                                viewLifecycleOwner,
                                Observer {
                                    when (it.status) {

                                        Status.LOADING -> {
                                        }
                                        Status.SUCCESS -> {
                                            onTheGo.add(StockAdapterData(it1.description, it.data!!.currentPrice.toString(), "%.2f".format(it.data.currentPrice - it.data.previousClosePrice), it1.symbol))
                                            if (onTheGo.size == 15) {
                                                loadEm()
                                            }
                                        }
                                        Status.ERROR -> {
                                            Log.e("ERROR!", it.message.toString())
                                        }
                                    }
                                }
                            )
                        }
                    }
                    else -> {
                    }
                }
            }
        )
    }
}
