package id.kumparan.dynamo.ui.home.tabs

import android.content.Context
import android.content.Intent
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
import id.kumparan.dynamo.RegisterActivity
import id.kumparan.dynamo.api.Api
import id.kumparan.dynamo.api.ApiUtility
import id.kumparan.dynamo.api.WrappedListResponse
import id.kumparan.dynamo.localstorage.LocalStorage
import id.kumparan.dynamo.model.*
import id.kumparan.dynamo.ui.home.rv.MyCommunityRVAdapter
import id.kumparan.dynamo.utility.ModelInjector
import kotlinx.android.synthetic.main.fragment_home_tab_my_community.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyCommunity : Fragment() {
    private val myListThreadFactory = ModelInjector.provideMyListThreadViewModelFactory()
    private val userFactory = ModelInjector.provideUserViewModelFactory()
    private val myCommunityFactory = ModelInjector.provideListMyCommunityViewModeFactory()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_tab_my_community, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val localUser = LocalStorage.readLocalToModel(requireContext())
        if (localUser?.data?.email == null) {
            val registerActivity = Intent(requireContext(), RegisterActivity::class.java)
            signUpBtn.setOnClickListener {
                startActivity(registerActivity)
            }
            notRegisterLayout.visibility = View.VISIBLE
        } else {
            signUpBtn.visibility = View.GONE
            myThreadModel().getData().observe(viewLifecycleOwner, Observer {
                val communityAdapter = MyCommunityRVAdapter(it)
                rvMyCommunity.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = communityAdapter
                }
                if (it.isNotEmpty()) {
                    notRegisterLayout.visibility = View.GONE
                    pbProgress.visibility = View.GONE
                } else {
//                    GlobalScope.launch{
//                        delay(10000L)
                        ApiUtility().getMyThread(
                            myThreadModel(), userModel()?.id
                            !!
                        )
//                    }
                    pbProgress.visibility = View.GONE
                }
            })
        }
    }

    private fun userModel(): UserModel? {
        val observer = ViewModelProvider(this, userFactory).get(UserViewModel::class.java)
        return observer.getData().value?.data
    }
    private fun myCommunityModel(): List<MyCommunityModel>? {
        val observer = ViewModelProvider(this, userFactory).get(MyCommunityModelViewModel::class.java)
        return observer.getData().value
    }
    private fun myThreadModel(): MyListThreadModelViewModel {
        return ViewModelProvider(this, myListThreadFactory).get(MyListThreadModelViewModel::class.java)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

}

