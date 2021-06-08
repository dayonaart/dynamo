package id.kumparan.dynamo.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ListThreadModel(
    val id: Int?,
    val title: String?,
    val content: String?,
    val communityId: Int?,
    val userCreateId: Int?,
    val userUpdateId: Int?,
    val createdAt: String?,
    val updatedAt: String?,
    @SerializedName("userCreate.username") val username: String?,
    @SerializedName("community.name") val communityName: String?,
    val upVote: Int?,
    val downVote: Int?,
    val noComments: Int?,
    val noReports: Int?,
    val score: Int?,
    val voteUserList:List<UserVoteId>?
) : Serializable

class ListThreadModelFactory(private val listThreadModelRepository: ListThreadModelRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ListThreadModelViewModel(listThreadModelRepository) as T
    }
}


class ListThreadModelRepository private constructor(private val listThreadModelRepository: ListThreadModelTempData) {
    fun updateDataList(listThreadModel: List<ListThreadModel>?) {
        listThreadModelRepository.updateDataList(listThreadModel)
    }

    fun getData() = listThreadModelRepository.getData()

    companion object {
        @Volatile
        private var instance:ListThreadModelRepository? = null
        fun getInstance(listThreadModelRepository: ListThreadModelTempData) =
            instance ?: synchronized(this) {
                instance ?: ListThreadModelRepository(listThreadModelRepository).also { instance = it }
            }
    }
}

class ListThreadModelTempData {
    private var newData = mutableListOf<ListThreadModel>()
    private val data = MutableLiveData<List<ListThreadModel>>()

    init {
        data.value = newData
    }

    fun updateDataList(listThreadModel: List<ListThreadModel>?) {
        if (listThreadModel != null) {
            newData = listThreadModel.toMutableList()
        }
        data.value = listThreadModel!!
    }

    fun getData() = data as LiveData<List<ListThreadModel>>
}

class ListThreadModelViewModel(private val listThreadModelRepository: ListThreadModelRepository) : ViewModel() {
    fun getData() = listThreadModelRepository.getData()
    fun updateDataList(listThreadModel: List<ListThreadModel>?) =
        listThreadModelRepository.updateDataList(listThreadModel)
}
