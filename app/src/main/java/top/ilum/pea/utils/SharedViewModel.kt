package top.ilum.pea.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    private var _connectivityStatus = MutableLiveData<Boolean>()
    val connectivityStatus: LiveData<Boolean>
        get() = _connectivityStatus

    fun setConnectivityStatus(status: Boolean) {
        _connectivityStatus.postValue(status)
    }
}
