package top.ilum.pea.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import top.ilum.pea.data.News
import top.ilum.pea.utils.Resource
import top.ilum.pea.utils.Status

class NewsViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private var savedState: Resource<News> = Resource.loading(data = null)

    fun getNews(): LiveData<Resource<News>> =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                if (savedState.status == Status.LOADING) {
                    savedState = Resource.success(data = mainRepository.getNews())
                }
                emit(savedState)
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }
}
