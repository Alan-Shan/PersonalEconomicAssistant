package top.ilum.pea.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Symbols::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun symbolsDao(): SymbolsDao

    companion object {
        private const val DATABASE_NAME = "database"

        @Volatile
        private var INSTANCE: top.ilum.pea.data.Database? = null

        fun getDatabase(context: Context): top.ilum.pea.data.Database {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    top.ilum.pea.data.Database::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
