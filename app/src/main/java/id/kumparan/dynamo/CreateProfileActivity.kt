package id.kumparan.dynamo
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import id.kumparan.dynamo.api.Api
import id.kumparan.dynamo.api.User
import id.kumparan.dynamo.localstorage.LocalStorage
import id.kumparan.dynamo.model.UserViewModel
import id.kumparan.dynamo.utility.ModelInjector
import id.kumparan.dynamo.utility.Utility
import id.kumparan.dynamo.utility.afterTextChanged
import kotlinx.android.synthetic.main.activity_create_profile.*
import kotlinx.android.synthetic.main.bottom_sheet_dialog.*
import kotlinx.android.synthetic.main.bottom_sheet_permission.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream


class CreateProfileActivity : AppCompatActivity() {
    private val STRING_LENGTH = 5
    private val ALPHANUMERIC_REGEX = "[a-zA-Z0-9]+"
    private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    private val factory = ModelInjector.provideUserViewModelFactory()
    private val API = Api.instance()
    private val gson = Gson()
    private val REQUEST_CODE = 10
    private val READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE"
    private val loading = CustomLoading()
    private var bitMap:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)
        supportActionBar?.hide()
        val userViewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)
        popScreen.setOnClickListener {
            onBackPressed()
        }
        usernameField.setText(randomName())
        initField()
        openFile()
        saveBtn.setOnClickListener {
            submit(this, userViewModel)
        }
        closeError.setOnClickListener {
            errorCardLayout.visibility = View.GONE
        }
    }
    private fun randomName(): String {
        return (1..STRING_LENGTH)
            .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("");
    }
    private fun submit(context: Context, userViewModel: UserViewModel) {
        val startingActivity = Intent(this, StartingActivity::class.java)
        errorCardLayout.visibility = View.GONE
        loading.show(this, "Mohon tunggu")
        val readLocalStorage = LocalStorage.readLocalToModel(context)
        val payload = UpdateUserPayload(usernameField.text.toString(), bitMap)
        API.updateUser(payload, readLocalStorage?.data?.id!!)
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        val res = response.body()
                        println(res)
                        if (res?.message == "Success") {
                            userViewModel.updateData(res)
                            LocalStorage.saveData(context, "user_session", gson.toJson(res))
                            loading.hide()
                            Utility.pushReplaceAll(context, startingActivity)
                        }
                    } else {
                        errorTextCard.text = response.message()
                        errorCardLayout.visibility = View.VISIBLE
                        loading.hide()
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    errorTextCard.text = "$t"
                    errorCardLayout.visibility = View.VISIBLE
                    loading.hide()
                }

            })
    }

    private fun initField() {
        usernameField.afterTextChanged {
            userNameLayout.error = when {
                it.isEmpty() -> {
                    saveBtn.isEnabled = validateFields()
                    "Wajib diisi"
                }
                else -> {
                    saveBtn.isEnabled = validateFields()
                    null
                }
            }
        }

        bioField.afterTextChanged {
            bioLayout.error = when {
                it.trim().isEmpty() -> {
                    saveBtn.isEnabled = validateFields()
                    "Wajib diisi"
                }
                else -> {
                    saveBtn.isEnabled = validateFields()
                    null
                }
            }
        }

        phoneField.afterTextChanged {
            phoneLayout.error = when {
                it.isEmpty() -> {
                    saveBtn.isEnabled = validateFields()
                    "Wajib diisi"
                }
                else -> {
                    saveBtn.isEnabled = validateFields()
                    null
                }
            }
        }
    }

    private fun validateFields(): Boolean {
        return (usernameField.text.toString().isNotEmpty() && bioField.text.toString()
            .isNotEmpty() && phoneField.text.toString().isNotEmpty())
    }

    private fun openFile() {
        val btnSheet = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        val btnSheetPermission = layoutInflater.inflate(R.layout.bottom_sheet_permission, null)
        val dialog = BottomSheetDialog(this)
        val dialogPermission = BottomSheetDialog(this)
        dialog.setContentView(btnSheet)
        dialogPermission.setContentView(btnSheetPermission)
        openFile.setOnClickListener {
            if (checkPermission() != PackageManager.PERMISSION_GRANTED) {
                openPermission(dialogPermission, btnSheetPermission)
            } else {
                openGallery(dialog, btnSheet)
            }
        }
        dialog.cancelBottomSheet.setOnClickListener {
            dialog.dismiss()
        }
        dialogPermission.rejectPermission.setOnClickListener {
            dialogPermission.dismiss()
        }
    }

    private fun openGallery(dialog: BottomSheetDialog, btnSheet: View) {
        dialog.show()
        dialog.openGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE)
        }
        btnSheet.setOnClickListener {
            dialog.dismiss()
        }

    }

    private fun openPermission(dialogPermission: BottomSheetDialog, btnSheetPermission: View) {
        dialogPermission.show()
        dialogPermission.accPermission.setOnClickListener {
            permissionResult()
        }
        btnSheetPermission.setOnClickListener {
            dialogPermission.dismiss()
        }
    }

    private fun openSettingPermissionDenied() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            openFile.setBackgroundResource(0)
            openFile.setImageURI(data?.data)
            val bitmap = (openFile.drawable as BitmapDrawable).bitmap
            encodeImage(bitmap)
        }
    }


    private fun checkPermission(): Int {
        return ContextCompat.checkSelfPermission(
            this,
            READ_EXTERNAL_STORAGE
        )
    }

    private fun permissionResult() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(READ_EXTERNAL_STORAGE), REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (isUserCheckNeverAskAgain()) {
            userPermissionReadStorageError.visibility = View.VISIBLE
            userPermissionReadStorageError.setOnClickListener {
                openSettingPermissionDenied()
            }
        }
    }

    private fun isUserCheckNeverAskAgain() =
        !ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            READ_EXTERNAL_STORAGE
        )

    override fun onResume() {
        super.onResume()
        if (checkPermission() == PackageManager.PERMISSION_GRANTED) {
            userPermissionReadStorageError.visibility = View.GONE
        }
    }
    private fun encodeImage(bm: Bitmap){
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        bitMap = Base64.encodeToString(b, Base64.DEFAULT)
    }
}

data class UpdateUserPayload(
    @SerializedName("username") val username: String?,
    @SerializedName("photo") val photo: String?
)

