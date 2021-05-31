package id.kumparan.dynamo.api

import android.annotation.SuppressLint
import android.util.Log
import id.kumparan.dynamo.model.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiUtility {
    fun getAllCommunity(communityListModelViewModel: CommunityListModelViewModel) {
        Api.instance().getAllCommunity()
            .enqueue(object : Callback<WrappedListResponse<CommunityListModel>> {
                override fun onFailure(
                    call: Call<WrappedListResponse<CommunityListModel>>,
                    t: Throwable
                ) {
                    Log.d("ERROR", "THIS MESSAGE $t")
                }

                override fun onResponse(
                    call: Call<WrappedListResponse<CommunityListModel>>,
                    response: Response<WrappedListResponse<CommunityListModel>>
                ) {
                    if (response.isSuccessful) {
                        val res = response.body()
                        communityListModelViewModel.updateData(res?.data!!)
                    } else {
                        val error = JSONObject(response.errorBody()!!.string())
                        Log.d("ERROR", "$error")
                    }
                }
            })
    }
    fun getMyCommunity(myCommunityModelViewModel: MyCommunityModelViewModel, userId: Int) {
        Api.instance().getMyCommunity(userId)
            .enqueue(object : Callback<WrappedListResponse<MyCommunityModel>> {
                override fun onFailure(
                    call: Call<WrappedListResponse<MyCommunityModel>>,
                    t: Throwable
                ) {
                    Log.d("ERROR", "THIS MESSAGE $t")
                }

                @SuppressLint("SimpleDateFormat")
                override fun onResponse(
                    call: Call<WrappedListResponse<MyCommunityModel>>,
                    response: Response<WrappedListResponse<MyCommunityModel>>
                ) {
                    if (response.isSuccessful) {
                        val res = response.body()
                        myCommunityModelViewModel.updateData(res?.data!!)
                    } else {
                        val error = JSONObject(response.errorBody()!!.string())
                        Log.d("ERROR", "$error")
                    }
                }
            })
    }

     fun getMyThread(listThreadModelViewModel: MyListThreadModelViewModel,userId:Int) {
        Api.instance().getAllThreadByUser(userId)
            .enqueue(object : Callback<WrappedListResponse<MyListThreadModel>> {
                override fun onFailure(
                    call: Call<WrappedListResponse<MyListThreadModel>>,
                    t: Throwable
                ) {
                    Log.d("ERROR", "THIS MESSAGE $t")
                }

                override fun onResponse(
                    call: Call<WrappedListResponse<MyListThreadModel>>,
                    response: Response<WrappedListResponse<MyListThreadModel>>
                ) {
                    if (response.isSuccessful) {
                        val res = response.body()
                        listThreadModelViewModel.updateDataList(res?.data!!)
                        println("GETTING MY THREAD SUCCESS")

                    } else {
                        val error = JSONObject(response.errorBody()!!.string())
                        Log.d("ERROR", "$error")
                    }
                }
            })
    }

     fun getAllUser(userListModelViewModel:UserListModelViewModel) {
        Api.instance().getAllUser()
            .enqueue(object : Callback<WrappedListResponse<UserListModel>> {
                override fun onFailure(
                    call: Call<WrappedListResponse<UserListModel>>,
                    t: Throwable
                ) {

                }

                @SuppressLint("SimpleDateFormat")
                override fun onResponse(
                    call: Call<WrappedListResponse<UserListModel>>,
                    response: Response<WrappedListResponse<UserListModel>>
                ) {
                    if (response.isSuccessful) {
                        val res = response.body()
                        userListModelViewModel.updateDataList(res?.data)

                    } else {
                        val error = JSONObject(response.errorBody()!!.string())
                        Log.d("ERROR", "$error")
                    }
                }
            })
    }
}

