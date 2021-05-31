package id.kumparan.dynamo.ui.community.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.kumparan.dynamo.CustomLoading
import id.kumparan.dynamo.CustomSnackBar
import id.kumparan.dynamo.R
import id.kumparan.dynamo.api.Api
import id.kumparan.dynamo.api.ApiUtility
import id.kumparan.dynamo.api.WrappedResponse
import id.kumparan.dynamo.model.*
import id.kumparan.dynamo.ui.community.data.PopularData
import id.kumparan.dynamo.ui.community.rv.PopularParentRvAdapter
import id.kumparan.dynamo.utility.ModelInjector
import kotlinx.android.synthetic.main.fragment_community_tab_popular.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class Popular : Fragment() {
    private val communityListFactory = ModelInjector.provideListCommunityViewModeFactory()
    private val myCommunityListFactory = ModelInjector.provideListMyCommunityViewModeFactory()
    private val myThreadFactory = ModelInjector.provideMyListThreadViewModelFactory()
    private val userFactory = ModelInjector.provideUserViewModelFactory()
    private lateinit var searchViewModel: SearchQueryModel
    private val loading = CustomLoading()
    private val snackBar = CustomSnackBar()
    lateinit var recyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_community_tab_popular, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        searchViewModel = ViewModelProvider(this).get(SearchQueryModel::class.java)
        initView()
        initSearchField()
        searchViewModel.getPopular().observe(viewLifecycleOwner, {
            it.values.forEach { p->
                p.forEach { n->
                    println(n.name)
                }
            }
            setRVPopular(it)
        })
    }

    private fun initSearchField() {
        searchField.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchViewModel.onPopularQuery(onPopularData()!!, newText)
                return false
            }
        })
    }

    private fun onPopularData(): Map<String?, List<CommunityListModel>>? {
        return communityModel().getData().value?.groupBy { datas ->
            if (datas.categories.isNullOrEmpty()) {
                datas.categories.let {
                    (1..1).map {
                        CommunityCategoriesList(100, "Others")
                    }
                }.groupBy { c -> c.categoryName }.keys.first()
            } else {
                datas.categories.groupBy { c -> c.categoryName }.keys.first()
            }
        }
    }

    private fun initView() {
        if (userModel()?.id == null) {
            haveNoCommunity.visibility = View.VISIBLE
        } else {
            haveNoCommunity.visibility = View.GONE
            communityModel().getData().observe(viewLifecycleOwner, {
                if (it.isEmpty()) {
                    loading.show(requireContext(), "Please Wait")
                    ApiUtility().getAllCommunity(communityModel())
                    loading.hide()
                } else {
                    val communityListData = it.groupBy { data ->
                        if (data.categories.isNullOrEmpty()) {
                            data.categories.let {
                                (1..1).map {
                                    CommunityCategoriesList(100, "Others")
                                }
                            }.groupBy { c -> c.categoryName }.keys.first()
                        } else {
                            data.categories.groupBy { c -> c.categoryName }.keys.first()
                        }
                    }
                    setRVPopular(communityListData)
                }

            })
        }

    }

    private fun setRVPopular(data: Map<String?, List<CommunityListModel>>) {
        recyclerView = rvPopular
        recyclerView.apply {
            layoutManager = LinearLayoutManager(
                context, LinearLayoutManager.VERTICAL, false
            )
            adapter = PopularParentRvAdapter(
                ParentDataFactory
                    .getParents(data)
            ) { id ->
                joinCommunity(id)
            }
        }
    }

    private fun communityModel(): CommunityListModelViewModel {
        return ViewModelProvider(
            this,
            communityListFactory
        ).get(CommunityListModelViewModel::class.java)
    }

    private fun myThreadModel(): MyListThreadModelViewModel {
        return ViewModelProvider(
            this,
            myThreadFactory
        ).get(MyListThreadModelViewModel::class.java)
    }

    private fun userModel(): UserModel? {
        val observer = ViewModelProvider(this, userFactory).get(UserViewModel::class.java)
        return observer.getData().value?.data
    }

    private fun myCommunityModel(): MyCommunityModelViewModel {
        return ViewModelProvider(
            this,
            myCommunityListFactory
        ).get(MyCommunityModelViewModel::class.java)
    }

    private fun joinCommunity(communityId: Int) {
        loading.show(requireContext(), "Please Wait")
        val payload = JoinCommunityPayload(communityId, userModel()?.id!!, false)
        Api.instance().joinCommunity(payload).enqueue(object :
            Callback<WrappedResponse<JoinCommunityResponseDataModel>> {
            override fun onResponse(
                call: Call<WrappedResponse<JoinCommunityResponseDataModel>>,
                response: Response<WrappedResponse<JoinCommunityResponseDataModel>>
            ) {
                val res = response.body()
                if (response.isSuccessful) {
                    if (res?.status == "Success") {
                        ApiUtility().getMyCommunity(myCommunityModel(), userModel()?.id!!)
                        ApiUtility().getAllCommunity(communityModel())
                        ApiUtility().getMyThread(myThreadModel(), userModel()?.id!!)
                        loading.hide()
                        snackBar.show(view!!, "Joined")
                    } else {
                        loading.hide()
                        snackBar.show(view!!, res?.message!!)
                    }
                } else {
                    loading.hide()
                    snackBar.show(view!!, res?.message!!)
                }
            }

            override fun onFailure(
                call: Call<WrappedResponse<JoinCommunityResponseDataModel>>,
                t: Throwable
            ) {
                loading.hide()
                snackBar.show(view!!, t.message!!)
            }
        })
    }

}

data class ParentModel(
    val title: String = "",
    val children: List<PopularData>
)

object ParentDataFactory {
    private fun children(data: List<CommunityListModel>): List<PopularData> {
        return ChildDataFactory.getChildren(data)
    }

    fun getParents(data: Map<String?, List<CommunityListModel>>): List<ParentModel> {
        val categoriesList = data.values.toList()
        val parents = mutableListOf<ParentModel>()
        repeat(data.keys.toList().size) {
            val parent = ParentModel("${data.keys.toList()[it]}", children(categoriesList[it]))
            parents.add(parent)
        }
        return parents
    }
}

object ChildDataFactory {
    fun getChildren(data: List<CommunityListModel>): List<PopularData> {
        val children = mutableListOf<PopularData>()
        repeat(data.size) {
            val child = PopularData(
                data[it].id,
                data[it].name, "${data[it].avatar}", data[it].noThreads, data[it].noUsers
            )
            children.add(child)
        }
        return children
    }

}

class JoinCommunityPayload(val communityId: Int, val userId: Int, val isModerator: Boolean) :
    Serializable

data class JoinCommunityResponseDataModel(
    val id: Int?,
    val communityId: Int?,
    val userId: Int?,
    val isModerator: Boolean?,
    val createdAt: String?,
    val updatedAt: String?
) : Serializable
