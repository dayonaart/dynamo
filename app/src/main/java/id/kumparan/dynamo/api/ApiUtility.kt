package id.kumparan.dynamo.api

import android.annotation.SuppressLint
import android.util.Log
import id.kumparan.dynamo.CommentToThreadPayload
import id.kumparan.dynamo.CommentToThreadResponse
import id.kumparan.dynamo.ReportThreadPayload
import id.kumparan.dynamo.ReportThreadResponse
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

    fun getMyCommunity(
        myCommunityModelViewModel: MyCommunityModelViewModel,
        userId: Int,
        message: (message: String) -> Unit?
    ) {
        Api.instance().getMyCommunity(userId)
            .enqueue(object : Callback<WrappedListResponse<MyCommunityModel>> {
                override fun onFailure(
                    call: Call<WrappedListResponse<MyCommunityModel>>,
                    t: Throwable
                ) {
                    message(t.message!!)
                }

                @SuppressLint("SimpleDateFormat")
                override fun onResponse(
                    call: Call<WrappedListResponse<MyCommunityModel>>,
                    response: Response<WrappedListResponse<MyCommunityModel>>
                ) {
                    val res = response.body()
                    if (response.isSuccessful) {
                        if (res?.message == "Success") {
                            myCommunityModelViewModel.updateData(res.data!!)
                        } else {
                            message(res?.message!!)
                        }
                    } else {
                        message(response.message())
                    }
                }
            })
    }

    fun getMyThread(listThreadModelViewModel: MyListThreadModelViewModel, userId: Int) {
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

    fun getAllUser(userListModelViewModel: UserListModelViewModel) {
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

    fun reportThread(reportThreadPayload: ReportThreadPayload, message: (message: String) -> Unit) {
        Api.instance().reportThread(reportThreadPayload)
            .enqueue(object : Callback<ReportThreadResponse> {
                override fun onResponse(
                    call: Call<ReportThreadResponse>,
                    response: Response<ReportThreadResponse>
                ) {
                    val res = response.body()
                    if (response.isSuccessful) {
                        message(res?.status!!)
                    } else {
                        message(res?.status!!)
                    }
                }

                override fun onFailure(call: Call<ReportThreadResponse>, t: Throwable) {
                    println("FRONTEND ${t.message}")

                }

            })
    }

    fun getAllComment(allCommentListModelViewModel: AllCommentListModelViewModel) {
        Api.instance().getAllComment()
            .enqueue(object : Callback<WrappedListResponse<AllCommentListModel>> {
                override fun onResponse(
                    call: Call<WrappedListResponse<AllCommentListModel>>,
                    response: Response<WrappedListResponse<AllCommentListModel>>
                ) {
                    val res = response.body()
                    if (response.isSuccessful) {
                        allCommentListModelViewModel.updateData(res?.data!!)
                    } else {
                        println("ALL COM ERR ${res?.message}")

                    }
                }

                override fun onFailure(
                    call: Call<WrappedListResponse<AllCommentListModel>>,
                    t: Throwable
                ) {
                    println("ALL COM TR ${t.message}")

                }

            })
    }

    fun getAllThread(listThreadModelViewModel: ListThreadModelViewModel) {
        Api.instance().getAllThread()
            .enqueue(object : Callback<WrappedListResponse<ListThreadModel>> {
                override fun onFailure(
                    call: Call<WrappedListResponse<ListThreadModel>>,
                    t: Throwable
                ) {
                    Log.d("ERROR", "THIS MESSAGE $t")
                }

                @SuppressLint("SimpleDateFormat")
                override fun onResponse(
                    call: Call<WrappedListResponse<ListThreadModel>>,
                    response: Response<WrappedListResponse<ListThreadModel>>
                ) {
                    if (response.isSuccessful) {
                        val res = response.body()
                        listThreadModelViewModel.updateDataList(res?.data!!)

                    } else {
                        val error = JSONObject(response.errorBody()!!.string())
                        Log.d("ERROR", "$error")
                    }
                }
            })
    }

    fun addCommentToThread(payload: CommentToThreadPayload, message: (message: String) -> Unit) {
        Api.instance().addCommentToThread(payload)
            .enqueue(object : Callback<WrappedResponse<CommentToThreadResponse>> {
                override fun onResponse(
                    call: Call<WrappedResponse<CommentToThreadResponse>>,
                    response: Response<WrappedResponse<CommentToThreadResponse>>
                ) {
                    val res = response.body()
                    if (response.isSuccessful) {
                        message(res?.status!!)
                    } else {
                        message(res?.status!!)
                    }
                }

                override fun onFailure(
                    call: Call<WrappedResponse<CommentToThreadResponse>>,
                    t: Throwable
                ) {
                    message(t.message!!)

                }

            })
    }

    fun resendVerificationCode(userViewModel: UserViewModel, message: (message: String) -> Unit) {
        Api.instance().resendEmailVerification(userViewModel.getData().value?.data?.email!!)
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    val res = response.body()
                    if (response.isSuccessful) {
                        if (res?.message == "Success") {
                            userViewModel.updateData(res)
                            message(res.message!!)
                        } else {
                            message(res?.message!!)
                        }
                    } else {
                        message(response.message())
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    message(t.message!!)
                }

            })
    }

    fun upVoteThread(threadId: Int, userId: Int, message: (message: String) -> Unit) {
        Api.instance().upVoteThread(threadId, userId)
            .enqueue(object : Callback<WrappedResponse<Any>> {
                override fun onResponse(
                    call: Call<WrappedResponse<Any>>,
                    response: Response<WrappedResponse<Any>>
                ) {
                    val res = response.body()
                    if (response.isSuccessful) {
                        message(res?.message ?: res?.status ?: "message not found")
                    } else {
                        message(response.message()?:"${response.code()}")
                    }
                }

                override fun onFailure(call: Call<WrappedResponse<Any>>, t: Throwable) {
                    message(t.message!!)
                }

            })
    }
}

