package id.kumparan.dynamo.ui.home.tabs

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import id.kumparan.dynamo.R
import id.kumparan.dynamo.api.Api
import id.kumparan.dynamo.api.ApiUtility
import id.kumparan.dynamo.api.WrappedListResponse
import id.kumparan.dynamo.model.*
import id.kumparan.dynamo.ui.home.rv.PopularCommunityRVAdapter
import id.kumparan.dynamo.utility.ModelInjector
import kotlinx.android.synthetic.main.fragment_community_tab_popular.rvPopular
import kotlinx.android.synthetic.main.fragment_home_tab_popular.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Popular : Fragment() {
    private val factory = ModelInjector.provideListThreadViewModelFactory()
    private val userFactory = ModelInjector.provideUserViewModelFactory()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_tab_popular, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        listThreadModel().getData().observe(viewLifecycleOwner, Observer {
            val communityAdapter = PopularCommunityRVAdapter(it)
            rvPopular.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = communityAdapter
            }
            when {
                it.isNotEmpty() -> {
                    pbProgress.visibility = View.GONE
                    haveNoThread.visibility = View.GONE
                }
                userModel()?.id == null -> {
                    pbProgress.visibility = View.GONE
                    haveNoThread.visibility = View.VISIBLE
                }
                else -> {
                    getData()
                }
            }
        })
    }

    private fun listThreadModel(): ListThreadModelViewModel {
        return ViewModelProvider(this, factory).get(ListThreadModelViewModel::class.java)
    }

    private fun getData() {
        pbProgress.visibility = View.VISIBLE
        ApiUtility().getAllThread(listThreadModel())
    }

    private fun userModel(): UserModel? {
        val observer = ViewModelProvider(this, userFactory).get(UserViewModel::class.java)
        return observer.getData().value?.data
    }
}