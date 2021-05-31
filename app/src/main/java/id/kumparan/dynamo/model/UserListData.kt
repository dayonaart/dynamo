package id.kumparan.dynamo.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.io.Serializable

data class UserListModel(
    val id: Int?,
    val username: String?,
    val email: String?,
    val photo: String?,
    val roleId: Int?,
    val token: String?,
    val lastLogin: String?,
    val isLogin:String?,
    val isRegisterUsing:String?,
    val createdAt:String?,
    val updatedAt:String?,
    val noCommunities:Int?,
    val noThreads:Int?,
    val noComments:Int?,
    val karma:Int?
):Serializable

class UserListModelFactory(private val userListDataRepository: UserListModelRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UserListModelViewModel(userListDataRepository) as T
    }
}


class UserListModelRepository private constructor(private val userListDataTempData: UserListModelTempData) {
    fun updateDataList(listCommunityData: List<UserListModel>?) {
        userListDataTempData.updateDataList(listCommunityData)
    }

    fun getData() = userListDataTempData.getData()

    companion object {
        @Volatile
        private var instance:UserListModelRepository? = null
        fun getInstance(userListDataTempData: UserListModelTempData) =
            instance ?: synchronized(this) {
                instance ?: UserListModelRepository(userListDataTempData).also { instance = it }
            }
    }
}

class UserListModelTempData {
    private var newData = mutableListOf<UserListModel>()
    private val data = MutableLiveData<List<UserListModel>>()

    init {
        data.value = newData
    }

    fun updateDataList(listCommunityData: List<UserListModel>?) {
        if (listCommunityData != null) {
            newData = listCommunityData.toMutableList()
        }
        data.value = listCommunityData!!
    }

    fun getData() = data as LiveData<List<UserListModel>>
}

class UserListModelViewModel(private val userListDataRepository: UserListModelRepository) : ViewModel() {
    fun getData() = userListDataRepository.getData()
    fun updateDataList(listCommunityData: List<UserListModel>?) =
        userListDataRepository.updateDataList(listCommunityData)
}
