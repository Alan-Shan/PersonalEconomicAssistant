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

class HomeFragment : Fragment(), DialogSelection.OnInputListener {

    companion object {
        fun newInstance(): HomeFragment =
            HomeFragment()
    }
    private lateinit var viewModel: NewsViewModel
    private val newsCategories = arrayOf("Your Money", "Business")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        viewModel =
            ViewModelProviders.of(
                this,
                ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
            ).get(NewsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getNews()
        selector.setOnClickListener {
            val dialog = DialogSelection()
            dialog.setTargetFragment(
                this,
                22
            )
            fragmentManager?.let { it1 -> dialog.show(it1, "Dialog") }
        }
    }

    private fun getNews(category: Int = 0) {
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
    override fun sendInput(input: Int) {
        getNews(input)
    }
}
