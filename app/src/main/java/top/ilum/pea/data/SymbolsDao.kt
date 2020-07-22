package top.ilum.pea.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SymbolsDao {
    @Query("SELECT * FROM Symbols")
    fun getAll(): List<Symbols>

    @Insert
    fun insertAll(symbols: List<Symbols>)
}
