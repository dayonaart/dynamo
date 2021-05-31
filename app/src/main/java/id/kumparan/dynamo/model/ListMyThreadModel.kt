package id.kumparan.dynamo.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class MyListThreadModel(
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
    val score: Int?
) : Serializable

class MyListThreadModelFactory(private val myListThreadModelRepository: MyListThreadModelRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MyListThreadModelViewModel(myListThreadModelRepository) as T
    }
}


class MyListThreadModelRepository private constructor(private val myListThreadModelRepository: MyListThreadModelTempData) {
    fun updateDataList(myListThreadModel: List<MyListThreadModel>?) {
        myListThreadModelRepository.updateDataList(myListThreadModel)
    }

    fun getData() = myListThreadModelRepository.getData()

    companion object {
        @Volatile
        private var instance:MyListThreadModelRepository? = null
        fun getInstance(myListThreadModelRepository: MyListThreadModelTempData) =
            instance ?: synchronized(this) {
                instance ?: MyListThreadModelRepository(myListThreadModelRepository).also { instance = it }
            }
    }
}

class MyListThreadModelTempData {
    private var newData = mutableListOf<MyListThreadModel>()
    private val data = MutableLiveData<List<MyListThreadModel>>()

    init {
        data.value = newData
    }

    fun updateDataList(myListThreadModel: List<MyListThreadModel>?) {
        if (myListThreadModel != null) {
            newData = myListThreadModel.toMutableList()
        }
        data.value = myListThreadModel!!
    }

    fun getData() = data as LiveData<List<MyListThreadModel>>
}

class MyListThreadModelViewModel(private val myListThreadModelRepository: MyListThreadModelRepository) : ViewModel() {
    fun getData() = myListThreadModelRepository.getData()
    fun updateDataList(myListThreadModel: List<MyListThreadModel>?) =
        myListThreadModelRepository.updateDataList(myListThreadModel)
}
