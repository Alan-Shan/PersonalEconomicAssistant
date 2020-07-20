package top.ilum.pea.ui.stock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class StockViewModelFactory(private val apiHelper: StockApiHelper) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StockViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StockViewModel(MainRepository(apiHelper)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}
