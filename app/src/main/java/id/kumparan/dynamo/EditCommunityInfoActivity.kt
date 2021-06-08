package id.kumparan.dynamo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import id.kumparan.dynamo.api.ApiUtility
import id.kumparan.dynamo.model.CommunityListModel
import id.kumparan.dynamo.model.CommunityListModelViewModel
import id.kumparan.dynamo.model.MyCommunityModelViewModel
import id.kumparan.dynamo.model.UserViewModel
import id.kumparan.dynamo.utility.ModelInjector
import id.kumparan.dynamo.utility.Utility
import id.kumparan.dynamo.utility.afterTextChanged
import kotlinx.android.synthetic.main.activity_edit_community_info.*
import kotlinx.android.synthetic.main.bottom_sheet_dialog.*
import kotlinx.android.synthetic.main.bottom_sheet_permission.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.Serializable

class EditCommunityInfo : AppCompatActivity() {
    private val myCommunityFactory = ModelInjector.provideListMyCommunityViewModeFactory()
    private val userFactory = ModelInjector.provideUserViewModelFactory()
    private val REQUEST_CODE_OPEN_FILE_COVER = 8
    private val REQUEST_CODE_OPEN_CAMERA_COVER = 9
    private val REQUEST_CODE_OPEN_FILE_AVATAR = 10
    private val REQUEST_CODE_OPEN_CAMERA_AVATAR = 11
    private val READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE"
    private var bitMap: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_community_info)
        val communityData = intent.extras?.get("data") as CommunityListModel
        initEditText()
        initPermission()
        popScreen.setOnClickListener {
            onBackPressed()
        }
        submit(communityData)
    }

    private fun myCommunityViewModel(): MyCommunityModelViewModel {
        return ViewModelProvider(
            this,
            myCommunityFactory
        ).get(MyCommunityModelViewModel::class.java)
    }
    private fun userViewModel(): UserViewModel {
        return ViewModelProvider(
            this,
            userFactory
        ).get(UserViewModel::class.java)
    }

    private fun submit(data: CommunityListModel) {
        val payload =
            EditCommunityPayload(data.name, etDesc.text.toString(), "", "", null, data.rules)
        submitBtn.setOnClickListener {
            ApiUtility().editCommunity(data.id!!, payload) { message ->
                Utility.customToast(this, message!!)
                if (message == "Success") {
                    ApiUtility().getMyCommunity(myCommunityViewModel(),userViewModel().getData().value?.data?.id!!){m->
                        if (m=="Success"){
                            onBackPressed()
                        }
                    }
                }
            }
        }
    }

    private fun changeBackground() {
        openFileBackground.setOnClickListener { }
    }

    private fun changeAvatar() {
        openFileAvatar.setOnClickListener {
        }
    }

    private fun initEditText() {
        etDesc.afterTextChanged {
            submitBtn.isEnabled = !it.isEmpty()
        }
    }

    @SuppressLint("InflateParams")
    private fun initPermission() {
        val btnSheet = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        val btnSheetPermission = layoutInflater.inflate(R.layout.bottom_sheet_permission, null)
        val dialog = BottomSheetDialog(this)
        val dialogPermission = BottomSheetDialog(this)
        dialog.setContentView(btnSheet)
        dialogPermission.setContentView(btnSheetPermission)
        openFileAvatar.setOnClickListener {
            if (checkPermission() != PackageManager.PERMISSION_GRANTED) {
                openPermission(dialogPermission, btnSheetPermission, REQUEST_CODE_OPEN_FILE_AVATAR)
            } else {
                openGallery(dialog, btnSheet, REQUEST_CODE_OPEN_FILE_AVATAR)
                openDeviceCamera(dialog, btnSheet, REQUEST_CODE_OPEN_CAMERA_AVATAR)
            }
        }

        openFileBackground.setOnClickListener {
            if (checkPermission() != PackageManager.PERMISSION_GRANTED) {
                openPermission(dialogPermission, btnSheetPermission, REQUEST_CODE_OPEN_FILE_COVER)
            } else {
                openGallery(dialog, btnSheet, REQUEST_CODE_OPEN_FILE_COVER)
                openDeviceCamera(dialog, btnSheet, REQUEST_CODE_OPEN_CAMERA_COVER)
            }
        }
        dialog.cancelBottomSheet.setOnClickListener {
            dialog.dismiss()
        }
        dialogPermission.rejectPermission.setOnClickListener {
            dialogPermission.dismiss()
        }
    }

    private fun openGallery(dialog: BottomSheetDialog, btnSheet: View, reqCode: Int) {
        dialog.show()
        dialog.openGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, reqCode)
        }
        btnSheet.setOnClickListener {
            dialog.dismiss()
        }

    }

    private fun openDeviceCamera(dialog: BottomSheetDialog, btnSheet: View, reqCode: Int) {
        dialog.show()
        dialog.openCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, reqCode)
        }
        btnSheet.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && (requestCode == REQUEST_CODE_OPEN_FILE_AVATAR || requestCode == REQUEST_CODE_OPEN_CAMERA_AVATAR)) {
            openFileAvatar.setBackgroundResource(0)
            openFileAvatar.setImageURI(data?.data)
            val bitmap = (openFileAvatar.drawable as BitmapDrawable).bitmap
            encodeImage(bitmap)
        } else if (resultCode == Activity.RESULT_OK && (requestCode == REQUEST_CODE_OPEN_FILE_COVER || requestCode == REQUEST_CODE_OPEN_CAMERA_COVER)) {
//            openFileBackground.setBackgroundResource(0)
            val realPath = File(getRealPathFromURI(data?.data!!)!!)
            if (realPath.exists()) {
                val drawable = Drawable.createFromPath(realPath.absolutePath)
                coverLayout.background = drawable
//                val bitmap = (drawable as BitmapDrawable).bitmap
//                encodeImage(bitmap)
            }
        }
    }

    private fun openPermission(
        dialogPermission: BottomSheetDialog,
        btnSheetPermission: View,
        reqCode: Int
    ) {
        dialogPermission.show()
        dialogPermission.accPermission.setOnClickListener {
            permissionResult(reqCode)
        }
        btnSheetPermission.setOnClickListener {
            dialogPermission.dismiss()
        }
    }

    private fun permissionResult(reqCode: Int) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(READ_EXTERNAL_STORAGE), reqCode
        )
    }

    private fun checkPermission(): Int {
        return ContextCompat.checkSelfPermission(
            this,
            READ_EXTERNAL_STORAGE
        )
    }


    private fun encodeImage(bm: Bitmap) {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        bitMap = Base64.encodeToString(b, Base64.DEFAULT)
    }

    @SuppressLint("Recycle")
    private fun getRealPathFromURI(contentURI: Uri): String? {
        val cursor: Cursor? = contentResolver.query(contentURI, null, null, null, null)
        return if (cursor == null) { // Source is Dropbox or other similar local file path
            contentURI.path
        } else {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor.getString(idx)
        }
    }
}


class EditCommunityPayload(
    val name: String?,
    val description: String?,
    val background: String?,
    val avatar: String?,
    val categories: Any?,
    val rules: String?
) : Serializable

class EditCommunityResponse(
    val id: Int?,
    val name: String?,
    val description: String?,
    val background: String?,
    val avatar: String?,
    val categories: Any?,
    val rules: String?,
    val userCreateId: Int?,
    val userUpdateId: Int?,
    val createdAt: String?,
    val updatedAt: String?
) : Serializable

