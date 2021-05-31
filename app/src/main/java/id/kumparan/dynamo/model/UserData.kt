package id.kumparan.dynamo.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.kumparan.dynamo.api.User
import id.kumparan.dynamo.api.WrappedResponse
import java.io.Serializable

data class UserModel(
    val id: Int?,
    val username: String?,
    val email: String?,
    val photo: String?,
    val token: String?,
    val isRegisterUsing: String?,
    val token2: String?,
    val role: String?,
) : Serializable

data class VerifyCodeModel(
    val info: InfoModel?, val verification_code: String?
) : Serializable

data class InfoModel(
    val accepted: List<String>?,
    val rejected: Any?,
    val response: String?,
    val envelope: Envelope?,
    val message: String?,
    val verification_code: String?
) : Serializable

data class Envelope(val from: String?, val to: List<String>?) : Serializable

class UserModelModelFactory(private val userDataRepository: UserModelRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UserViewModel(userDataRepository) as T
    }
}


class UserModelRepository private constructor(private val userDataTempData: UserModelTempData) {
    fun updateData(user:User) {
        userDataTempData.updateData(user)
    }

    fun getData() = userDataTempData.getData()

    companion object {
        @Volatile
        private var instance: UserModelRepository? = null
        fun getInstance(userDataTempData: UserModelTempData) =
            instance ?: synchronized(this) {
                instance ?: UserModelRepository(userDataTempData).also { instance = it }
            }
    }
}

class UserModelTempData {
    private var newData =User(null,null,null,null)
    private val data = MutableLiveData<User>()

    init {
        data.value = newData
    }

    fun updateData(user:User) {
        newData = user
        data.value = newData
    }

    fun getData() = data as LiveData<User>
}

class UserViewModel(private val userDataRepository: UserModelRepository) : ViewModel() {
    fun getData() = userDataRepository.getData()
    fun updateData(user:User) = userDataRepository.updateData(user)
}
