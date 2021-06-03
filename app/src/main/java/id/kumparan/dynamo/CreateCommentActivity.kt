package id.kumparan.dynamo

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.ViewModelProvider
import id.kumparan.dynamo.api.ApiUtility
import id.kumparan.dynamo.model.*
import id.kumparan.dynamo.utility.ModelInjector
import id.kumparan.dynamo.utility.Utility
import id.kumparan.dynamo.utility.afterTextChanged
import kotlinx.android.synthetic.main.activity_create_comment.*
import java.io.Serializable
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

class CreateCommentActivity : AppCompatActivity() {
    private val allCommentFactory=ModelInjector.provideAllCommentListViewModelFactory()
    private val myListThreadFactory = ModelInjector.provideMyListThreadViewModelFactory()
    private val userFactory = ModelInjector.provideUserViewModelFactory()
    private val allListThreadFactory=ModelInjector.provideListThreadViewModelFactory()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_comment)
        toolbar_title.text = detailThread().title
        tvUsername.text = detailThread().username
        tvCommunityName.text = detailThread().communityName
        tvCreatedDate.text = parseDate(detailThread().createdAt)
        tvContent.text = detailThread().content
        initTextField()
        popScreen.setOnClickListener { onBackPressed() }
        submitBtn.setOnClickListener {
            contentTextField.visibility=View.GONE
            pbProgress.visibility=View.VISIBLE
            val payload = CommentToThreadPayload(detailThread().id,detailThread().userCreateId,contentTextField.text.toString())
            ApiUtility().addCommentToThread(payload){message->
                if (message=="Success"){
                    ApiUtility().getAllComment(allCommentListModel())
                    ApiUtility().getMyThread(myThreadModel(),userModel()?.id!!)
//                    ApiUtility().getAllThread()
                    pbProgress.visibility=View.GONE
                    onBackPressed()
                }else{
                    pbProgress.visibility=View.GONE
                    Utility.customToast(this,message)
                }
            }
        }
    }

    private fun allCommentListModel(): AllCommentListModelViewModel {
        return ViewModelProvider(
            this,
            allCommentFactory
        ).get(AllCommentListModelViewModel::class.java)

    }
//    private fun allThreadModel():ListThreadModelViewModel{
//        return ViewModelProvider(this,allListThreadFactory).get(AllCommentListModelViewModel::class.java)
//    }
    private fun myThreadModel(): MyListThreadModelViewModel {
        return ViewModelProvider(
            this,
            myListThreadFactory
        ).get(MyListThreadModelViewModel::class.java)
    }
    private fun detailThread(): MyListThreadModel {
        return intent.extras?.get("data") as MyListThreadModel
    }


    private fun userModel(): UserModel? {
        val observer = ViewModelProvider(this, userFactory).get(UserViewModel::class.java)
        return observer.getData().value?.data
    }


    @SuppressLint("SimpleDateFormat")
    private fun parseDate(date: String?): String? {
        val sdf = SimpleDateFormat("dd MMMM y")
        return sdf.format(
            Date.from((Instant.parse(date ?: "1990-11-30T18:35:24.00Z")))
        )
    }

    private fun initTextField() {
        contentTextField.afterTextChanged {
            submitBtn.isEnabled = it.trim().isNotEmpty()
        }
    }
}

data class CommentToThreadPayload(
    val threadId: Int?,
    val userCreateId: Int?,
    val content: String?
) : Serializable

data class CommentToThreadResponse(
    val isDeleted: Boolean?,
    val id: Int?,
    val threadId: Int?,
    val userCreateId: Int?,
    val content: String?,
    val isReply: Boolean?,
    val updatedAt: String?,
    val createdAt: String?,
    val replyToCommentId:Any?,
    val deletedOn:Any?
):Serializable


