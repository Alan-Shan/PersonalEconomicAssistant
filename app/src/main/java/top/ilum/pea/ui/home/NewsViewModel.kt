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
    private var savedCategory: Int = 0

    fun getNews(category: Int): LiveData<Resource<News>> =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                if (savedState.status == Status.LOADING || savedCategory != category) {
                    savedState = Resource.success(data = mainRepository.getNews(category))
                    savedCategory = category
                }
                emit(savedState)
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }
}
