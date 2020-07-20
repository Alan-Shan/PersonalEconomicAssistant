package top.ilum.pea.data

import com.google.gson.annotations.SerializedName

data class Symbols(

    @SerializedName("description") val description: String,
    @SerializedName("displaySymbol") val displaySymbol: String,
    @SerializedName("symbol") val symbol: String,
    @SerializedName("type") val type: String,
    @SerializedName("currency") val currency: String
)
