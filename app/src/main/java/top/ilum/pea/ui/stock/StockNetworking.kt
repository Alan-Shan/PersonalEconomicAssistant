package top.ilum.pea.ui.stock

import android.util.Base64
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import top.ilum.pea.data.Candle
import top.ilum.pea.data.Quote
import top.ilum.pea.data.Symbols

interface ApiService {

    @GET("stock/symbol")
    suspend fun getSymbols(
        @Query("token") apiKey: String,
        @Query("exchange") exchange: String = "US"
    ): List<Symbols>

    @GET("quote")
    suspend fun getQuote(
        @Query("token")
        apiKey: String,
        @Query("symbol") symbol: String
    ): Quote

    @GET("stock/candle")
    suspend fun getCandle(
        @Query("token") apiKey: String,
        @Query("symbol") symbol: String,
        @Query("resolution") resolution: String,
        @Query("from") from: Int,
        @Query("to") to: Int
    ): Candle
}

object RetrofitBuilder {
    private const val BASE_URL = "https://finnhub.io/api/v1/"
    private fun getRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val apiService: ApiService = getRetrofit().create(ApiService::class.java)
}

class StockApiHelper(private val apiService: ApiService) {

    private val apiKey = String(Base64.decode("YnNhdWtmZnJoNXJhaXY5Y2JjMWc=", Base64.DEFAULT))

    suspend fun getSymbols(): List<Symbols> =
        apiService.getSymbols(apiKey)

    suspend fun getQuote(symbol: String): Quote =
        apiService.getQuote(apiKey, symbol)

    suspend fun getCandle(symbol: String, resolution: String, from: Int, to: Int): Candle =
        apiService.getCandle(apiKey, symbol, resolution, from, to)
}

class MainRepository(private val apiHelper: StockApiHelper) {

    suspend fun getSymbols(): List<Symbols> =
        apiHelper.getSymbols()

    suspend fun getQuote(symbol: String): Quote =
        apiHelper.getQuote(symbol)

    suspend fun getCandle(symbol: String, resolution: String, from: Int, to: Int): Candle =
        apiHelper.getCandle(symbol, resolution, from, to)
}
