package top.ilum.pea.ui.stock

import android.content.Context
import android.os.Bundle
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
import top.ilum.pea.data.Symbols
import top.ilum.pea.utils.Status

class StockDialog : DialogFragment() {

    interface StockChange {
        fun sendInput(symbols: Symbols)
    }

    private lateinit var viewModel: StockViewModel

    private var stockChange: StockChange? = null

    private lateinit var stillNotLoaded: List<Symbols>

    private var onTheGo: MutableList<Symbols> = mutableListOf()

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
            onTheGo.addAll(stillNotLoaded.take(15))
            stillNotLoaded = stillNotLoaded.drop(15)
            (stockselectionrecycler.adapter as StockAdapter).apply { list = onTheGo }.notifyDataSetChanged()
        }

        fun loadEm() {
            var isLoading: Boolean = false

            stockselectionrecycler.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = StockAdapter {
                    stockChange!!.sendInput(it)
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

        viewModel.getSymbolsDb().observe(
            this,
            Observer {
                if (it.status == Status.SUCCESS) {
                    stillNotLoaded = it.data!!
                    onTheGo.addAll(stillNotLoaded.take(15))
                    stillNotLoaded = stillNotLoaded.drop(15)
                    loadEm()
                }
            }
        )
    }
}
