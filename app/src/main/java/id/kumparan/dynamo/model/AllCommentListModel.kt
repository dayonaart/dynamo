package id.kumparan.dynamo.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AllCommentListModel(
    val id: Int?,
    val threadId: Int?,
    val userCreateId: Int?,
    val content: String?,
    val isReply: Boolean?,
    val isDeleted: Boolean?,
    val deletedOn: Any,
    val createdAt: String?,
    val updatedAt: String?,
    @SerializedName("User.username") val username: String?,
    @SerializedName("Thread.title") val threadTitle: String?,
    val voteUserList: List<UserVotingId>?,
    val upvotes: Int?,
    val downvotes: Int?,
    val totalVotes: Int?,
    val noReplies: Int?,
    val noReports: Int?
):Serializable

data class UserVotingId(val userCreateId: Int?) : Serializable

class AllCommentListModelFactory(private val allCommentListModelRepository: AllCommentListModelRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AllCommentListModelViewModel(allCommentListModelRepository) as T
    }
}


class AllCommentListModelRepository private constructor(private val allCommentListModelTempData: AllCommentListModelTempData) {
    fun updateData(listAllCommentListModel: List<AllCommentListModel>) {
        allCommentListModelTempData.updateData(listAllCommentListModel)
    }

    fun getData() = allCommentListModelTempData.getData()

    companion object {
        @Volatile
        private var instance: AllCommentListModelRepository? = null
        fun getInstance(allCommentListModelTempData: AllCommentListModelTempData) =
            instance ?: synchronized(this) {
                instance ?: AllCommentListModelRepository(allCommentListModelTempData).also {
                    instance = it
                }
            }
    }
}

class AllCommentListModelTempData {
    private var newData =
        mutableListOf<AllCommentListModel>()
    private val data = MutableLiveData<List<AllCommentListModel>>()

    init {
        data.value = newData
    }

    fun updateData(listAllCommentListModel: List<AllCommentListModel>) {
        newData = listAllCommentListModel.toMutableList()
        data.value = listAllCommentListModel
    }

    fun getData() = data as LiveData<List<AllCommentListModel>>
}

class AllCommentListModelViewModel(private val allCommentListModelRepository: AllCommentListModelRepository) :
    ViewModel() {
    fun getData() = allCommentListModelRepository.getData()
    fun updateData(listAllCommentListModel: List<AllCommentListModel>) =
        allCommentListModelRepository.updateData(listAllCommentListModel)
}
