package id.kumparan.dynamo.pages.detail_community_tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.annotations.SerializedName
import id.kumparan.dynamo.CustomSnackBar
import id.kumparan.dynamo.R
import id.kumparan.dynamo.api.Api
import id.kumparan.dynamo.api.WrappedListResponse
import id.kumparan.dynamo.pages.rv.ModeratorRvAdapter
import kotlinx.android.synthetic.main.rv_moderator_detail_community.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class Moderator(private val communityId: Int?) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.rv_moderator_detail_community, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getModerator()
    }

    private fun getModerator() {
        Api.instance().getCommunityMember(communityId!!)
            .enqueue(object : Callback<WrappedListResponse<ModeratorResponseModel>> {
                override fun onResponse(
                    call: Call<WrappedListResponse<ModeratorResponseModel>>,
                    response: Response<WrappedListResponse<ModeratorResponseModel>>
                ) {
                    val res = response.body()
                    if (response.isSuccessful) {
                        val isModerator = res?.data?.filter { it.isModerator == true }
                        if (isModerator?.size == 0) {
                            haveNoModerator.visibility = View.VISIBLE
                        } else {
                            haveNoModerator.visibility = View.GONE
                            val moderatorAdapter = ModeratorRvAdapter(isModerator!!)
                            rvModerator.apply {
                                layoutManager = LinearLayoutManager(context)
                                adapter = moderatorAdapter
                            }
                        }

                    } else {
                        CustomSnackBar().show(
                            requireView(), "${res?.message}"
                        )
                    }
                }

                override fun onFailure(
                    call: Call<WrappedListResponse<ModeratorResponseModel>>,
                    t: Throwable
                ) {
                    CustomSnackBar().show(
                        requireView(), "${t.message}"
                    )
                }

            })
    }

}

data class ModeratorResponseModel(
    val communityId: Int?,
    val userId: String?,
    val isModerator: Boolean?,
    val createdAt: String?,
    val updatedAt: String?,
    @SerializedName("User.username") val username: String?,
    @SerializedName("User.photo") val userPhoto: String?,
    val karma: Int?
) : Serializable
