package id.kumparan.dynamo
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import id.kumparan.dynamo.api.Api
import id.kumparan.dynamo.api.User
import id.kumparan.dynamo.api.WrappedResponse
import id.kumparan.dynamo.localstorage.LocalStorage
import id.kumparan.dynamo.model.UserModel
import id.kumparan.dynamo.model.UserViewModel
import id.kumparan.dynamo.utility.GSO
import id.kumparan.dynamo.utility.ModelInjector
import id.kumparan.dynamo.utility.afterTextChanged
import id.kumparan.dynamo.utility.isValidEmail
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private val factory = ModelInjector.provideUserViewModelFactory()
    private val API = Api.instance()
    lateinit var googleClient: GoogleSignInClient
    private val RC_SIGN_IN = 1010
    lateinit var callbackManager: CallbackManager
    private val gson = Gson()
    private val loading = CustomLoading()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()
        googleClient = GoogleSignIn.getClient(this, GSO)
        callbackManager = CallbackManager.Factory.create()
        popScreen.setOnClickListener {
            onBackPressed()
        }
        initEmailField()
        initPasswordField()
        registerBtn.setOnClickListener {
            submit(this, payload())
        }
        googleSignup.setOnClickListener {
            val d=LocalStorage.readLocalToModel(this)
            println(d)
//            googleSignUp()
        }
        facebookSignup.setOnClickListener {
            facebookSignUp(this)
        }
        closeError.setOnClickListener {
            errorCardLayout.visibility = View.GONE
        }
    }

    private fun payload(): RegisterPayload {
        return RegisterPayload(
            emailField.text.toString(),
            emailField.text.toString(),
            passwordField.text.toString(),
            null
        )
    }

    private fun userModel(): UserViewModel {
        return ViewModelProvider(this, factory).get(UserViewModel::class.java)
    }

    private fun submit(context: Context, registerPayload: RegisterPayload) {
        val createProfileActivity = Intent(this, CreateProfileActivity::class.java)
        val startingActivity = Intent(this, StartingActivity::class.java)
        val activationCodeActivity = Intent(this, ActivationCodeActivity::class.java)
        errorCardLayout.visibility = View.GONE
        loading.show(this, "Mohon tunggu")
        API.register(registerPayload).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                val res = response.body()
                if (response.isSuccessful) {
                    if (res?.message == "Success") {
                        if (res.data?.isRegisterUsing != null) {
                            LocalStorage.saveData(context,"isVerify","false")
                            LocalStorage.saveData(context, "user_session", gson.toJson(res))
                            userModel().updateData(res)
                            loading.hide()
                            startActivity(startingActivity)
                        } else {
                            LocalStorage.saveData(context,"isVerify","false")
                            LocalStorage.saveData(context, "user_session", gson.toJson(res))
                            userModel().updateData(res)
                            loading.hide()
                            startActivity(activationCodeActivity)
                        }
                    } else {
                        errorTextCard.text = res?.message
                        errorCardLayout.visibility = View.VISIBLE
                        loading.hide()
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

    private fun initEmailField() {
        emailField.afterTextChanged {
            emailLayout.error = when {
                it.isEmpty() -> {
                    registerBtn.isEnabled = false
                    "email wajib diisi"
                }
                !isValidEmail(it) -> {
                    registerBtn.isEnabled = false
                    "Harus diisi dengan format email"
                }
                else -> {
                    if (passwordField.text.toString().length < 8) {
                        registerBtn.isEnabled = false
                        emailLayout.isErrorEnabled = false
                        null
                    } else {
                        registerBtn.isEnabled = true
                        emailLayout.isErrorEnabled = false
                        null
                    }
                }
            }
        }
    }

    private fun initPasswordField() {
        passwordField.afterTextChanged {
            passwordLayout.error = when {
                it.isEmpty() -> {
                    registerBtn.isEnabled = false
                    null
                }
                it.length < 8 -> {
                    registerBtn.isEnabled = false
                    "Minimal 8 karakter"
                }
                else -> {
                    if (emailField.text.isEmpty() || !isValidEmail(emailField.text.toString())) {
                        registerBtn.isEnabled = false
                        passwordLayout.isErrorEnabled = false
                        null
                    } else {
                        passwordLayout.isErrorEnabled = false
                        registerBtn.isEnabled = true
                        null
                    }
                }
            }
        }
    }

    private fun googleSignUp() {
        val googleSignUpInIntent = googleClient.signInIntent
        startActivityForResult(
            googleSignUpInIntent, RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignInResult(task, this)
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleGoogleSignInResult(
        completedTask: Task<GoogleSignInAccount>,
        context: Context
    ) {
        try {
            val account = completedTask.getResult(
                ApiException::class.java
            )
            val payload = RegisterPayload(
                account?.givenName,
                account?.email,
                null, "google"
            )
            submit(context, payload)
        } catch (e: ApiException) {
            // Sign in was unsuccessful
            Log.e(
                "failed code=", e.statusCode.toString()
            )
        }
    }

    private fun facebookSignUp(context: Context) {
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile", "email"))
        // Callback registration
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    Api.getUserProfileFacebook(
                        loginResult?.accessToken,
                        loginResult?.accessToken?.userId
                    ) {
                        submit(context, RegisterPayload(it.firstName, it.email, "", "facebook"))
                    }
                }

                override fun onCancel() {
                    Toast.makeText(context, "Login Cancelled", Toast.LENGTH_LONG).show()
                }

                override fun onError(exception: FacebookException) {
                    Toast.makeText(context, exception.message, Toast.LENGTH_LONG).show()
                }
            })
    }

}

data class RegisterPayload(
    @SerializedName("username") val username: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("password") val password: String?,
    @SerializedName("isRegisterUsing") val isRegisterUsing: String?
)

data class FacebookResponse(
    val firstName: String?,
    val middleName: String?,
    val lastName: String?,
    val name: String?,
    val profilePic: String?,
    val email: String?
)