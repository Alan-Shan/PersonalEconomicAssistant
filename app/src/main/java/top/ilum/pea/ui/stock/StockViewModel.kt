package top.ilum.pea.ui.stock

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import top.ilum.pea.data.Candle
import top.ilum.pea.data.Database
import top.ilum.pea.data.Quote
import top.ilum.pea.data.Symbols
import top.ilum.pea.data.SymbolsDao
import top.ilum.pea.utils.Resource
import top.ilum.pea.utils.Status

class StockViewModel(private val mainRepository: MainRepository, application: Application) : AndroidViewModel(application) {

    private var _dailyData = MutableLiveData<Quote>()
    private var personDao: SymbolsDao = Database.getDatabase(application).symbolsDao()
    private var cachedCandle: Resource<Candle> = Resource.loading(null)
    private var cachedSymbol: String = "foobar"
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
                personDao.insertAll(this.latestValue?.data!!)
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    fun getQuote(symbol: String): LiveData<Resource<Quote>> =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                if (dailyData.value == null || dailyData.value !== this.latestValue?.data) {
                    emit(Resource.success(data = mainRepository.getQuote(symbol)))
                    _dailyData.postValue(this.latestValue?.data!!)
                } else { emit(Resource.success(dailyData.value!!)) }
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    fun getCandle(symbol: String, resolution: String, from: Long, to: Long): LiveData<Resource<Candle>> =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                if (cachedSymbol !== symbol || cachedCandle.status == Status.LOADING) {
                    cachedCandle =  Resource.success(data = mainRepository.getCandle(symbol, resolution, from, to))
                    cachedSymbol = symbol
                }

                emit(cachedCandle)
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    fun getSymbolsDb(): LiveData<Resource<List<Symbols>>> =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                savedStateSymbols = Resource.success(data = personDao.getAll())
                emit(savedStateSymbols)
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }
    fun populateDb() {
    }
}
