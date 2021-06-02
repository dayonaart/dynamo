package id.kumparan.dynamo.pages.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.io.Serializable

data class ThreadChatData(
    val username: String?,
    val content: String?,
    val likeCount: Int?,
    val replyCount:Int?
) : Serializable

class ThreadChatModelFactory(private val threadChatRepository: ThreadChatRepository)
    : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(threadChatRepository) as T
    }
}


class ThreadChatRepository private constructor(private val threadChatTempData: ThreadChatTempData) {
    fun updateDataList(listThreadChatData: List<ThreadChatData>?) {
        threadChatTempData.updateDataList(listThreadChatData)
    }

    fun getData() = threadChatTempData.getData()

    companion object {
        @Volatile
        private var instance: ThreadChatRepository? = null
        fun getInstance(threadChatTempData: ThreadChatTempData
        ) =
            instance ?: synchronized(this) {
                instance ?: ThreadChatRepository(threadChatTempData).also { instance = it }
            }
    }
}

class ThreadChatTempData {
    private var newData= mutableListOf<ThreadChatData>()
    private val data= MutableLiveData<List<ThreadChatData>>()
    init {
        data.value=newData
    }

    fun updateDataList(listHomeData: List<ThreadChatData>?){
        if (listHomeData != null) {
            newData=listHomeData.toMutableList()
        }
        data.value=listHomeData!!
    }

    fun getData()=data as LiveData<List<ThreadChatData>>
}
class HomeViewModel(private val threadChatRepository: ThreadChatRepository)
    : ViewModel() {
    fun getData() = threadChatRepository.getData()
    fun updateDataList(listHomeData: List<ThreadChatData>?) = threadChatRepository.updateDataList(listHomeData)
}
