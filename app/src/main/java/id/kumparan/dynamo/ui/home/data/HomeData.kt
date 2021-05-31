package id.kumparan.dynamo.ui.home.data
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.io.Serializable

data class HomeData(
    val communityName:String?,
    val userName:String?,
    val communityDate:String?,
    val albumId:Int?,
    val id:Int?,
    val title:String?,
    val url:String?,
    val thumbnailUrl:String?
): Serializable


class HomeModelFactory(private val homeRepository: HomeRepository)
    : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(homeRepository) as T
    }
}


class HomeRepository private constructor(private val homeTempData: HomeTempData) {
    fun updateDataList(listHomeData: List<HomeData>?) {
        homeTempData.updateDataList(listHomeData)
    }

    fun getData() = homeTempData.getData()

    companion object {
        @Volatile
        private var instance: HomeRepository? = null
        fun getInstance(homeTempData: HomeTempData) =
            instance ?: synchronized(this) {
                instance ?: HomeRepository(homeTempData).also { instance = it }
            }
    }
}

class HomeTempData {
    private var newData= mutableListOf<HomeData>()
    private val data= MutableLiveData<List<HomeData>>()
    init {
        data.value=newData
    }

    fun updateDataList(listHomeData: List<HomeData>?){
        if (listHomeData != null) {
            newData=listHomeData.toMutableList()
        }
        data.value=listHomeData!!
    }

    fun getData()=data as LiveData<List<HomeData>>
}
class HomeViewModel(private val homeRepository: HomeRepository)
    : ViewModel() {
    fun getData() = homeRepository.getData()
    fun updateDataList(listHomeData: List<HomeData>?) = homeRepository.updateDataList(listHomeData)
}
