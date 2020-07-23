package top.ilum.pea.data

import com.google.gson.annotations.SerializedName

data class Results(

    @SerializedName("slug_name") val slug_name: String,
    @SerializedName("section") val section: String,
    @SerializedName("subsection") val subsection: String,
    @SerializedName("title") val title: String,
    @SerializedName("abstract") val abstract: String,
    @SerializedName("url") val url: String,
    @SerializedName("byline") val byline: String,
    @SerializedName("thumbnail_standard") val thumbnail_standard: String,
    @SerializedName("item_type") val item_type: String,
    @SerializedName("source") val source: String,
    @SerializedName("updated_date") val updated_date: String,
    @SerializedName("created_date") val created_date: String,
    @SerializedName("published_date") val published_date: String,
    @SerializedName("first_published_date") val first_published_date: String,
    @SerializedName("material_type_facet") val material_type_facet: String,
    @SerializedName("kicker") val kicker: String,
    @SerializedName("subheadline") val subheadline: String,
    @SerializedName("des_facet") val des_facet: List<String>,
    @SerializedName("org_facet") val org_facet: List<String>,
    @SerializedName("per_facet") val per_facet: List<String>,
    @SerializedName("geo_facet") val geo_facet: List<String>,
    @SerializedName("related_urls") val related_urls: List<RelatedUrls>,
    @SerializedName("multimedia") val multimedia: List<Multimedia>
)
