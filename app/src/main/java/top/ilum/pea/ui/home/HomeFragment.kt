package top.ilum.pea.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import top.ilum.pea.R
import java.io.IOException

class HomeFragment : Fragment() {

    init {
        System.loadLibrary("keys")
    }
    private val okHttpClient = OkHttpClient()
    private external fun getNYTKey(): String?
    var apiKey: String = String(Base64.decode(getNYTKey(), Base64.DEFAULT))
    private val request: Request = Request.Builder().url("https://api.nytimes.com/svc/news/v3/content/all/your money.json?api-key=$apiKey").build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Snackbar.make(view, getString(R.string.network_unreacheable), Snackbar.LENGTH_LONG)
                    .show()
            }
            override fun onResponse(call: Call?, response: Response) {
                if (!response.isSuccessful) { Snackbar.make(view, getString(R.string.network_unreacheable), Snackbar.LENGTH_LONG).show() }
                val posts = JSONObject(response.body()?.string() as String).getJSONArray("results")
                val jsonPosts = Gson().fromJson(posts.toString(), Array<Results>::class.java).toList()
                Handler(Looper.getMainLooper()).post {
                    news_recycler.apply {
                        layoutManager = LinearLayoutManager(activity)
                        adapter = NewsAdapter(jsonPosts)
                    }
                }
            }
        })
    }
}
