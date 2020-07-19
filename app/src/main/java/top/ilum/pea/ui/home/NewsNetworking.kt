package top.ilum.pea.ui.home

import android.util.Base64
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import top.ilum.pea.data.News

interface ApiService {

    @GET("your money.json")
    suspend fun getNews(@Query("api-key") apiKey: String): News
}

object RetrofitBuilder {
    private const val BASE_URL = "https://api.nytimes.com/svc/news/v3/content/all/"

    private fun getRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val apiService: ApiService = getRetrofit().create(ApiService::class.java)
}

class ApiHelper(private val apiService: ApiService) {
    init {
        System.loadLibrary("keys")
    }

    private external fun getNYTKey(): String?
    private val apiKey: String = String(Base64.decode(getNYTKey(), Base64.DEFAULT))

    suspend fun getNews(): News =
        apiService.getNews(apiKey)
}

class MainRepository(private val apiHelper: ApiHelper) {

    suspend fun getNews(): News =
        apiHelper.getNews()
}
