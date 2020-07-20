package top.ilum.pea.ui.stock

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
}
