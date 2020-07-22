package top.ilum.pea.ui.stock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import top.ilum.pea.data.Candle
import top.ilum.pea.data.Quote
import top.ilum.pea.data.Symbols
import top.ilum.pea.utils.Resource
import top.ilum.pea.utils.Status

class StockViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private var _dailyData = MutableLiveData<Quote>()
    val dailyData: LiveData<Quote>
        get() = _dailyData

    private var savedStateSymbols: Resource<List<Symbols>> = Resource.loading(data = null)
    fun getSymbols(): LiveData<Resource<List<Symbols>>> =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                if (savedStateSymbols.status == Status.LOADING) {
                    savedStateSymbols = Resource.success(data = mainRepository.getSymbols())
                }
                emit(savedStateSymbols)
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    fun getQuote(symbol: String): LiveData<Resource<Quote>> =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.getQuote(symbol)))
                _dailyData.postValue(this.latestValue?.data ?: Quote(2.22, 2.22, 2.22, 2.22, 2.22, 22))
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    fun getCandle(symbol: String, resolution: String, from: Long, to: Long): LiveData<Resource<Candle>> =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {

                emit(Resource.success(data = mainRepository.getCandle(symbol, resolution, from, to)))
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }
}
