package top.ilum.pea.data

import com.google.gson.annotations.SerializedName

data class Candle(

    @SerializedName("c") val closePrices: List<Double>,
    @SerializedName("h") val highPrices: List<Double>,
    @SerializedName("l") val lowPrices: List<Double>,
    @SerializedName("o") val openPrices: List<Double>,
    @SerializedName("s") val status: String,
    @SerializedName("t") val timestamp: List<Int>,
    @SerializedName("v") val volumeData: List<Int>
)
