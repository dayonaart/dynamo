package id.kumparan.dynamo.pages

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import id.kumparan.dynamo.R
import id.kumparan.dynamo.api.Api
import id.kumparan.dynamo.api.ApiUtility
import id.kumparan.dynamo.api.WrappedListResponse
import id.kumparan.dynamo.model.CommunityListModelViewModel
import id.kumparan.dynamo.model.SearchQueryModel
import id.kumparan.dynamo.model.UserListModel
import id.kumparan.dynamo.model.UserListModelViewModel
import id.kumparan.dynamo.pages.adapter.SearchTabAdapter
import id.kumparan.dynamo.utility.ModelInjector
import kotlinx.android.synthetic.main.activity_search.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {
    private val fragmentTitleList = mutableListOf("Thread", "Komunitas", "Profil")
    private val userListFactory = ModelInjector.provideUserListViewModelFactory()
    private val communityListFactory = ModelInjector.provideListCommunityViewModeFactory()
    private lateinit var searchViewModel: SearchQueryModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        supportActionBar?.hide()
        searchViewModel = ViewModelProvider(this).get(SearchQueryModel::class.java)
        initUi()
        initSearchField()
        userListViewModel().getData().observe(this, {
            when {
                it.isEmpty() -> {
                    ApiUtility().getAllUser(userListViewModel())
                }
                communityViewModel().getData().value?.isEmpty()!! -> {
                    ApiUtility().getAllCommunity(communityViewModel())
                }
                else -> {
                    println("DONE")
                }
            }
        })
        popScreen.setOnClickListener {
            onBackPressed()
        }
    }

    private fun communityViewModel(): CommunityListModelViewModel {
        return ViewModelProvider(
            this,
            communityListFactory
        ).get(CommunityListModelViewModel::class.java)
    }

    private fun userListViewModel(): UserListModelViewModel {
        return ViewModelProvider(this, userListFactory).get(UserListModelViewModel::class.java)
    }

    private fun initUi() {
        val adapter = SearchTabAdapter(this)
        searchViewPager.adapter = adapter
        TabLayoutMediator(searchTab, searchViewPager) { tab, pos ->
            tab.text = fragmentTitleList[pos]
        }.attach()
    }

    private fun initSearchField() {
        searchField.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchViewModel.onQuery(
                    communityViewModel().getData().value!!,
                    newText,
                    userListViewModel().getData().value!!
                )
                return false
            }
        })
    }

}