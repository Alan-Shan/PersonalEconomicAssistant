package top.ilum.pea.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_home.*
import top.ilum.pea.R
import top.ilum.pea.utils.Status

class HomeFragment : Fragment(), DialogSelection.OnInputListener {

    private lateinit var viewModel: NewsViewModel
    private var savedCategory = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        )
            .get(NewsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getNews(savedCategory)
        selector.setOnClickListener {
            val dialogArgs: Bundle = Bundle()
            dialogArgs.putInt("category", savedCategory)
            val dialog = DialogSelection()
            dialog.setTargetFragment(
                this,
                22
            )
            dialog.arguments = dialogArgs
            dialog.show(parentFragmentManager, "Dialog")

        }
    }

    private fun getNews(category: Int) {
        savedCategory = category
        viewModel.getNews(category).observe(
            viewLifecycleOwner,
            Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {

                            loading.visibility = View.GONE
                            selector.visibility = View.VISIBLE
                            news_recycler.visibility = View.VISIBLE
                            news_recycler.apply {
                                layoutManager = LinearLayoutManager(activity)
                                adapter = NewsAdapter(it.data!!.results)
                                (adapter as NewsAdapter).stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                            }
                        }
                        Status.ERROR -> {
                            news_recycler.visibility = View.GONE
                            loading.visibility = View.GONE
                            ouch.visibility = View.VISIBLE
                            Log.e("error", it.message.toString())
                        }
                        Status.LOADING -> {

                            loading.visibility = View.VISIBLE
                        }
                    }
                }
            }
        )
    }
    override fun sendInput(input: Int) {
        getNews(input)
    }
}
