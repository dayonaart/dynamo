package id.kumparan.dynamo.model
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class MyCommunityModel(
    val id: Int?,
    val name: String?,
    val description: String?,
    val isModerator: Boolean?,
    val background:String?,
    val avatar:String?,
    val createdAt: String?,
    val updatedAt: String?,
    val categories: List<MyCommunityCategory>?,
    val rules:String?,
    val noUsers:Int?,
    val noMods:Int?,
    val noThreads:Int?,
) : Serializable

data class MyCommunityCategory(val id: Int?, val categoryName: String?) : Serializable


class MyCommunityListModelFactory(private val myCommunityModel: MyCommunityListModelRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MyCommunityModelViewModel(myCommunityModel) as T
    }
}


class MyCommunityListModelRepository private constructor(private val myCommunityListDataTempData: MyCommunityListModelTempData) {
    fun updateData(listMyCommunityModel: List<MyCommunityModel>) {
        myCommunityListDataTempData.updateData(listMyCommunityModel)
    }

    fun getData() = myCommunityListDataTempData.getData()

    companion object {
        @Volatile
        private var instance: MyCommunityListModelRepository? = null
        fun getInstance(myCommunityListDataTempData: MyCommunityListModelTempData) =
            instance ?: synchronized(this) {
                instance ?: MyCommunityListModelRepository(myCommunityListDataTempData).also {
                    instance = it
                }
            }
    }
}

class MyCommunityListModelTempData {
    private var newData =
        mutableListOf<MyCommunityModel>()
    private val data = MutableLiveData<List<MyCommunityModel>>()

    init {
        data.value = newData
    }

    fun updateData(listMyCommunityModel: List<MyCommunityModel>) {
        newData = listMyCommunityModel.toMutableList()
        data.value = listMyCommunityModel
    }

    fun getData() = data as LiveData<List<MyCommunityModel>>
}

class MyCommunityModelViewModel(private val myCommunityModel: MyCommunityListModelRepository) :
    ViewModel() {
    fun getData() = myCommunityModel.getData()
    fun updateData(listMyCommunityModel: List<MyCommunityModel>) =
        myCommunityModel.updateData(listMyCommunityModel)
}
