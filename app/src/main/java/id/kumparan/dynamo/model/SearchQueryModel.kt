package id.kumparan.dynamo.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchQueryModel : ViewModel() {
    private val communityListModel = MutableLiveData<List<CommunityListModel>>()
    private val threadListModel = MutableLiveData<List<CommunityListModel>>()
    private val userListModel = MutableLiveData<List<UserListModel>>()
    private val popularListModel = MutableLiveData<Map<String?, List<CommunityListModel>>>()

    fun onQuery(
        communityListModelQuery: List<CommunityListModel>,
        query: String?,
        userList: List<UserListModel>
    ) {
        communityListModel.value = communityListModelQuery.filter {
            (it.name?.contains(query!!)!!)
        }
        threadListModel.value = communityListModelQuery.filter {
            (it.description?.contains(query!!)!!)
        }
        userListModel.value = userList.filter {
            (it.username!!.contains(query!!))
        }.sortedBy {
            it.username
        }
    }

    fun onPopularQuery(data: Map<String?, List<CommunityListModel>>, query: String?) {
        popularListModel.value = data.filter {
            it.value.map { c -> c.name!!.contains(query!!) }.contains(true)
        }

    }

    fun getThread(): MutableLiveData<List<CommunityListModel>> {
        return threadListModel
    }

    fun getCommunity(): MutableLiveData<List<CommunityListModel>> {
        return communityListModel
    }

    fun getUser(): MutableLiveData<List<UserListModel>> {
        return userListModel
    }

    fun getPopular(): MutableLiveData<Map<String?, List<CommunityListModel>>> {
        return popularListModel
    }
}