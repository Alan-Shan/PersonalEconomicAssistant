package top.ilum.pea.data

import com.google.gson.annotations.SerializedName

data class RelatedUrls(

    @SerializedName("suggested_link_text") val suggestion: String,
    @SerializedName("url") val url: String
)
