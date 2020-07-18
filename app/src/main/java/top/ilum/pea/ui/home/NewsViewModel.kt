package top.ilum.pea.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import top.ilum.pea.data.News
import top.ilum.pea.utils.Resource

class NewsViewModel(private val mainRepository: MainRepository) : ViewModel() {

    fun getNews(): LiveData<Resource<News>> =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.getNews()))
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }
}
