package top.ilum.pea.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*
import top.ilum.pea.R
import top.ilum.pea.utils.Status

class HomeFragment : Fragment() {

    companion object {
        fun newInstance(): HomeFragment =
            HomeFragment()
    }

    private lateinit var viewModel: NewsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_home, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(NewsViewModel::class.java)

        viewModel.getNews().observe(
            viewLifecycleOwner,
            Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {

                            loading.visibility = View.GONE
                            news_recycler.visibility = View.VISIBLE
                            news_recycler.apply {
                                layoutManager = LinearLayoutManager(activity)
                                adapter = NewsAdapter(it.data!!.results)
                            }
                        }
                        Status.ERROR -> {
                            loading.visibility = View.GONE
                            ouch.visibility = View.VISIBLE
                        }
                        Status.LOADING -> {

                            loading.visibility = View.VISIBLE
                        }
                    }
                }
            }
        )
    }
}
