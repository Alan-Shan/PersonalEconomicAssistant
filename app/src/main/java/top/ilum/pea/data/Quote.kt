package top.ilum.pea.data

import com.google.gson.annotations.SerializedName

data class Quote(

    @SerializedName("c") val currentPrice: Double,
    @SerializedName("h") val highPriceDay: Double,
    @SerializedName("l") val lowPriceDay: Double,
    @SerializedName("o") val openPrice: Double,
    @SerializedName("pc") val previousClosePrice: Double,
    @SerializedName("t") val timestamp: Int
)
