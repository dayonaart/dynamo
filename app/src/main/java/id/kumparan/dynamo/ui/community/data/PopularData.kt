package id.kumparan.dynamo.ui.community.data
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.Serializable

data class PopularData(
    val id:Int?,
    val title:String?,
    val imgUrl:String?,
    val threadCount:Int?,
    val memberCount:Int?
): Serializable

class PopularDataTempData{
    private var newData= mutableListOf<PopularData>()
    private val data= MutableLiveData<List<PopularData>>()
    init {
        data.value=newData
    }
    fun updateDataList(listCommunityData: List<PopularData>?){
        if (listCommunityData != null) {
            newData=listCommunityData.toMutableList()
        }
        data.value=listCommunityData!!
    }

    fun getData()=data as LiveData<List<PopularData>>

}
class PopularDataRepository private constructor(private val popularTempData: PopularDataTempData){
    fun updateDataList(listCommunityData: List<PopularData>?) {
        popularTempData.updateDataList(listCommunityData)
    }

    fun getData() = popularTempData.getData()

    companion object {
        @Volatile
        private var instance:  PopularDataRepository? = null
        fun getInstance(popularTempData:  PopularDataTempData) =
            instance ?: synchronized(this) {
                instance ?: PopularDataRepository(popularTempData).also { instance = it }
            }
    }
}
class PopularDataViewModel(private val communityRepository:  PopularDataRepository):ViewModel(){
    fun getData() = communityRepository.getData()
    fun updateDataList(listCommunityData: List<PopularData>?) = communityRepository.updateDataList(listCommunityData)
}