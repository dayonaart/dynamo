package id.kumparan.dynamo.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CommunityListModel(
    val id: Int?,
    val name: String?,
    val description: String?,
    val background: String?,
    val avatar: String?,
    val categories: List<CommunityCategoriesList>?,
    val rules: String?,
    val userCreateId: String?,
    val userUpdateId: String?,
    val createdAt: String?,
    val updatedAt: String?,
    @SerializedName("userCreate.Username") val userCreate_Username:String?,
    @SerializedName("userUpdate.Username") val userUpdate_Username:String?,
    val noUsers:Int?,
    val noMods:Int?,
    val noThreads:Int?
) : Serializable

data class CommunityCategoriesList(val id: Int?, val categoryName: String?) : Serializable


class CommunityListModelFactory(private val communityListDataRepository: CommunityListModelRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CommunityListModelViewModel(communityListDataRepository) as T
    }
}


class CommunityListModelRepository private constructor(private val communityListDataTempData: CommunityListModelTempData) {
    fun updateData(listCommunityListModel: List<CommunityListModel>) {
        communityListDataTempData.updateData(listCommunityListModel)
    }

    fun getData() = communityListDataTempData.getData()

    companion object {
        @Volatile
        private var instance: CommunityListModelRepository? = null
        fun getInstance(communityListDataTempData: CommunityListModelTempData) =
            instance ?: synchronized(this) {
                instance ?: CommunityListModelRepository(communityListDataTempData).also {
                    instance = it
                }
            }
    }
}

class CommunityListModelTempData {
    private var newData =
        mutableListOf<CommunityListModel>()
    private val data = MutableLiveData<List<CommunityListModel>>()

    init {
        data.value = newData
    }

    fun updateData(listCommunityListModel: List<CommunityListModel>) {
        newData = listCommunityListModel.toMutableList()
        data.value = listCommunityListModel
    }

    fun getData() = data as LiveData<List<CommunityListModel>>
}

class CommunityListModelViewModel(private val communityListDataRepository: CommunityListModelRepository) :
    ViewModel() {
    fun getData() = communityListDataRepository.getData()
    fun updateData(listCommunityListModel: List<CommunityListModel>) =
        communityListDataRepository.updateData(listCommunityListModel)
}
