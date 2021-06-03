package id.kumparan.dynamo.api
import com.google.gson.annotations.SerializedName
import id.kumparan.dynamo.*
import id.kumparan.dynamo.model.*
import id.kumparan.dynamo.pages.AddThreadModelPayload
import id.kumparan.dynamo.pages.AddThreadResponseModel
import id.kumparan.dynamo.pages.detail_community_tabs.ModeratorResponseModel
import id.kumparan.dynamo.ui.community.tabs.JoinCommunityPayload
import id.kumparan.dynamo.ui.community.tabs.JoinCommunityResponseDataModel
import retrofit2.Call
import retrofit2.http.*

interface ApiServices {
    companion object {
        const val baseUrl = "http://147.139.192.20:9991/api/v1"
    }

    @Headers("Content-Type: application/json")
    @POST("${baseUrl}/user/login")
    fun login(@Body payload: LoginPayload): @JvmSuppressWildcards Call<User>

    @Headers("Content-Type: application/json")
    @POST("${baseUrl}/user/register")
    fun register(@Body registerPayload: RegisterPayload): @JvmSuppressWildcards Call<User>

    @Headers("Content-Type: application/json")
    @POST("${baseUrl}/user/update/{id}")
    fun updateUser(
        @Body payload: UpdateUserPayload,
        @Path("id") userId: Int
    ): @JvmSuppressWildcards Call<User>

    @Headers("Content-Type: application/json")
    @POST("${baseUrl}/report/AddReportToThread")
    fun reportThread(@Body payload: ReportThreadPayload): @JvmSuppressWildcards Call<ReportThreadResponse>

    @Headers("Content-Type: application/json")
    @POST("${baseUrl}/community/addMember")
    fun joinCommunity(@Body payload: JoinCommunityPayload): @JvmSuppressWildcards Call<WrappedResponse<JoinCommunityResponseDataModel>>

    @Headers("Content-Type: application/json")
    @POST("${baseUrl}/thread/add")
    fun addThread(@Body payload: AddThreadModelPayload): @JvmSuppressWildcards Call<WrappedListResponse<AddThreadResponseModel>>

    @Headers("Content-Type: application/json")
    @POST("${baseUrl}/comment/addCommentToThread")
    fun addCommentToThread(@Body payload: CommentToThreadPayload): @JvmSuppressWildcards Call<WrappedResponse<CommentToThreadResponse>>

    @Headers("Content-Type: application/json")
    @GET("${baseUrl}/user")
    fun getAllUser(
    ): @JvmSuppressWildcards Call<WrappedListResponse<UserListModel>>

    @Headers("Content-Type: application/json")
    @GET("${baseUrl}/comment/getAll")
    fun getAllComment(
    ): @JvmSuppressWildcards Call<WrappedListResponse<AllCommentListModel>>

    @Headers("Content-Type: application/json")
    @GET("${baseUrl}/comment/getByThread/{threadId}/")
    fun getAllCommentByThreadId(
        @Path("id") threadId: Int
    ): @JvmSuppressWildcards Call<WrappedListResponse<Any>>

    @Headers("Content-Type: application/json")
    @GET("${baseUrl}/community")
    fun getAllCommunity(
    ): @JvmSuppressWildcards Call<WrappedListResponse<CommunityListModel>>

    @Headers("Content-Type: application/json")
    @GET("${baseUrl}/thread/user/{id}")
    fun getAllThreadByUser(
        @Path("id")userId: Int
    ): @JvmSuppressWildcards Call<WrappedListResponse<MyListThreadModel>>

    @Headers("Content-Type: application/json")
    @GET("${baseUrl}/thread/")
    fun getAllThread(
    ): @JvmSuppressWildcards Call<WrappedListResponse<ListThreadModel>>


    @Headers("Content-Type: application/json")
    @GET("${baseUrl}/community/getByUser/{id}")
    fun getMyCommunity(
        @Path("id") userId: Int
    ): @JvmSuppressWildcards Call<WrappedListResponse<MyCommunityModel>>

    @Headers("Content-Type: application/json")
    @GET("${baseUrl}/community/{id}/member")
    fun getCommunityMember(
        @Path("id") communityId: Int
    ): @JvmSuppressWildcards Call<WrappedListResponse<ModeratorResponseModel>>

    @Headers("Content-Type: application/json")
    @GET("${baseUrl}/user/resend-verification")
    fun resendEmailVerification(
       @Query("email") email:String
    ): @JvmSuppressWildcards Call<User>

}

data class WrappedResponse<T>(
    @SerializedName("message") var message: String?,
    @SerializedName("status") var status: String?,
    @SerializedName("data") var data: T? = null,
)

data class User(
    @SerializedName("message") var message: String?,
    @SerializedName("status") var status: String?,
    @SerializedName("data") var data: UserModel? = null,
    @SerializedName("verification_code") var verificationCode: VerifyCodeModel?
)

data class WrappedListResponse<T>(
    @SerializedName("message") var message: String?,
    @SerializedName("status") var status: String?,
    @SerializedName("data") var data: List<T>? = null
)

