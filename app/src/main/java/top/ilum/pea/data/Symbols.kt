package top.ilum.pea.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Symbols(

    @ColumnInfo(name = "description") @SerializedName("description") val description: String,
    @ColumnInfo(name = "displaySymbol")@SerializedName("displaySymbol") val displaySymbol: String,
    @PrimaryKey @ColumnInfo(name = "symbol")@SerializedName("symbol") val symbol: String,
    @ColumnInfo(name = "type")@SerializedName("type") val type: String,
    @ColumnInfo(name = "currency")@SerializedName("currency") val currency: String
)
