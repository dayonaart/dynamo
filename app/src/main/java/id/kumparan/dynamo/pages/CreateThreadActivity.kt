package id.kumparan.dynamo.pages

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import id.kumparan.dynamo.CustomLoading
import id.kumparan.dynamo.R
import id.kumparan.dynamo.api.Api
import id.kumparan.dynamo.api.ApiUtility
import id.kumparan.dynamo.api.WrappedListResponse
import id.kumparan.dynamo.model.MyCommunityModelViewModel
import id.kumparan.dynamo.model.MyListThreadModelViewModel
import id.kumparan.dynamo.model.UserViewModel
import id.kumparan.dynamo.utility.ModelInjector
import id.kumparan.dynamo.utility.Utility
import id.kumparan.dynamo.utility.afterTextChanged
import kotlinx.android.synthetic.main.activity_create_thread.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.Serializable

class CreateThreadActivity : AppCompatActivity() {
    private val myCommunityListFactory = ModelInjector.provideListMyCommunityViewModeFactory()
    private val myThreadListFactory = ModelInjector.provideMyListThreadViewModelFactory()
    private val userFactory = ModelInjector.provideUserViewModelFactory()
    private val REQUEST_CODE = 10
    private var bitMap: String? = null
    private val loading = CustomLoading()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_thread)
        myCommunityViewModel().getData().observe(this, {
            if (it.isEmpty()) {
                ApiUtility().getMyCommunity(
                    myCommunityViewModel(),
                    userViewModel().getData().value?.data?.id!!
                )
            }
        })
        setSpinner()
        popScreen.setOnClickListener { onBackPressed() }
        validationField()
        submitBtn.setOnClickListener {
            submit()
        }
//        val `is` = ImageSpan(this, R.drawable.thread_information)
//        val text = SpannableString("Lorem ipsum dolor sit amet\n")
//        text.setSpan(`is`, text.length - 2, text.length, 0)
//        openGallery()
    }

    private fun validationField(){
        etTitle.afterTextChanged {
            submitBtn.isEnabled = it.isNotEmpty()&& etContent.text.toString().isNotEmpty()
        }

        etContent.afterTextChanged {
            submitBtn.isEnabled = it.isNotEmpty()&& etTitle.text.toString().isNotEmpty()
        }
    }

    private fun myCommunityViewModel(): MyCommunityModelViewModel {
        return ViewModelProvider(
            this,
            myCommunityListFactory
        ).get(MyCommunityModelViewModel::class.java)
    }

    private fun myThreadListViewModel(): MyListThreadModelViewModel {
        return ViewModelProvider(
            this,
            myThreadListFactory
        ).get(MyListThreadModelViewModel::class.java)
    }

    private fun userViewModel(): UserViewModel {
        return ViewModelProvider(this, userFactory).get(UserViewModel::class.java)
    }

    private fun setSpinner() {
        myCommunityViewModel().getData().observe(this, {
            val listSpinner = it.map { name -> name.communityName }
            val adapter: ArrayAdapter<String> =
                ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, listSpinner)
            communitySpinner.adapter = adapter
        })
    }

    private fun submit() {
        val title = etTitle.text.toString()
        val desc = etContent.text.toString()
        loading.show(this, "Please Wait")
        val indexSpinner = communitySpinner?.selectedItemPosition
        val communityId =
            myCommunityViewModel().getData().value?.get(indexSpinner!!)?.communityId
        val payload =
            AddThreadModelPayload(
                title,
                desc,
                communityId,
                userViewModel().getData().value?.data?.id
            )
        Api.instance().addThread(payload)
            .enqueue(object : Callback<WrappedListResponse<AddThreadResponseModel>> {
                override fun onResponse(
                    call: Call<WrappedListResponse<AddThreadResponseModel>>,
                    response: Response<WrappedListResponse<AddThreadResponseModel>>
                ) {
                    val res = response.body()
                    if (response.isSuccessful) {
                        ApiUtility().getMyThread(
                            myThreadListViewModel(),
                            userViewModel().getData().value?.data?.id!!
                        )
                        loading.hide()
                        Utility.customToast(applicationContext, "${res?.message}")
                        onBackPressed()
                    } else {
                        loading.hide()
                        Utility.customToast(applicationContext, "${res?.message}")
                    }
                }

                override fun onFailure(
                    call: Call<WrappedListResponse<AddThreadResponseModel>>,
                    t: Throwable
                ) {
                    loading.hide()
                    Utility.customToast(applicationContext, "${t.message}")
                    println("MESS ${t.message}")
                }

            })
    }

    private fun openGallery() {
        openGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(
                        this.contentResolver,
                        data?.data!!
                    )
                )
            } else {
                MediaStore.Images.Media.getBitmap(this.contentResolver, data?.data!!)
            }
//            openFile.setBackgroundResource(0)
//            openFile.setImageURI(data?.data)
//            val bitmap = (openGallery.drawable as BitmapDrawable).bitmap
//            val d= BitmapFactory.decodeResource(resources,R.drawable.dynamo_profile)
            println("BAOS $bitmap")
        }
    }

    private fun encodeImage(bm: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }
}

data class AddThreadModelPayload(
    val title: String?,
    val content: String?,
    val communityId: Int?,
    val userCreateId: Int?
) : Serializable

data class AddThreadResponseModel(
    val id: Int?,
    val title: String?,
    val content: String?,
    val communityId: Int?,
    val userCreateId: Int?,
    val createdAt: String?,
    val updatedAt: String?
) : Serializable
