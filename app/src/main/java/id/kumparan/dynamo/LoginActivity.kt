package id.kumparan.dynamo

import android.annotation.SuppressLint
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
import id.kumparan.dynamo.api.Api
import id.kumparan.dynamo.api.User
import id.kumparan.dynamo.localstorage.LocalStorage
import id.kumparan.dynamo.model.UserModel
import id.kumparan.dynamo.model.UserViewModel
import id.kumparan.dynamo.utility.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.closeError
import kotlinx.android.synthetic.main.activity_login.emailField
import kotlinx.android.synthetic.main.activity_login.emailLayout
import kotlinx.android.synthetic.main.activity_login.errorCardLayout
import kotlinx.android.synthetic.main.activity_login.errorTextCard
import kotlinx.android.synthetic.main.activity_login.passwordField
import kotlinx.android.synthetic.main.activity_login.passwordLayout
import kotlinx.android.synthetic.main.activity_login.popScreen
import kotlinx.android.synthetic.main.activity_login.signupContainer
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class LoginActivity : AppCompatActivity() {
    private val factory = ModelInjector.provideUserViewModelFactory()
    private val API = Api.instance()
    private val gson = Gson()
    lateinit var googleClient: GoogleSignInClient
    lateinit var callbackManager: CallbackManager
    private val RC_SIGN_IN = 1010
    private val loading = CustomLoading()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        callbackManager = CallbackManager.Factory.create()
        googleClient = GoogleSignIn.getClient(this, GSO)
        popScreen.setOnClickListener {
            onBackPressed()
        }
        initEmailField()
        initPasswordField()
        loginBtn.setOnClickListener {
            submit(this,payload(emailField.text.toString(), passwordField.text.toString(),null))
        }
        googleLogin.setOnClickListener {
            googleSignIn()
        }
        facebookLogin.setOnClickListener {
            facebookLogin(this)
        }
        closeError.setOnClickListener {
            errorCardLayout.visibility = View.GONE
        }
        signupContainer.setOnClickListener {
            val registerActivity = Intent(this, RegisterActivity::class.java)
            Utility.pushReplace(this, registerActivity)
        }
    }

    private fun userModel(): UserViewModel {
        return ViewModelProvider(this, factory).get(UserViewModel::class.java)
    }

    private fun initEmailField() {
        emailField.afterTextChanged {
            emailLayout.error = when {
                it.isEmpty() -> {
                    loginBtn.isEnabled = false
                    "email wajib diisi"
                }
                !isValidEmail(it) -> {
                    loginBtn.isEnabled = false
                    "Harus diisi dengan format email"
                }
                else -> {
                    if (passwordField.text.toString().length < 8) {
                        loginBtn.isEnabled = false
                        emailLayout.isErrorEnabled = false
                        null
                    } else {
                        loginBtn.isEnabled = true
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
                    loginBtn.isEnabled = false
                    null
                }
                it.length < 8 -> {
                    loginBtn.isEnabled = false
                    "Minimal 8 karakter"
                }
                else -> {
                    if (emailField.text.isEmpty() || !isValidEmail(emailField.text.toString())) {
                        loginBtn.isEnabled = false
                        passwordLayout.isErrorEnabled = false
                        null
                    } else {
                        passwordLayout.isErrorEnabled = false
                        loginBtn.isEnabled = true
                        null
                    }
                }
            }
        }
    }

    private fun facebookLogin(context: Context) {
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile", "email"))
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    Api.getUserProfileFacebook(
                        loginResult?.accessToken,
                        loginResult?.accessToken?.userId
                    ) {
                        if (it.email != null) {
                            submit(context,payload(it.email,null,"facebook"))
                        } else {
                            Toast.makeText(context, "email must not be null", Toast.LENGTH_LONG)
                                .show()
                        }
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

    private fun payload(email: String, password: String?,isRegisterUsing: String?): LoginPayload {
        return LoginPayload(email, password,isRegisterUsing)
    }

    private fun submit(context: Context,payload: LoginPayload) {
        errorCardLayout.visibility = View.GONE
        loading.show(this, "Mohon tunggu")
        API.login(payload).enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                errorCardLayout.visibility = View.VISIBLE
                errorTextCard.text = "$t"
                loading.hide()
            }

            @SuppressLint("SimpleDateFormat")
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val res = response.body()
                    if (res?.status != "Error") {
                        val startingActivity = Intent(context, StartingActivity::class.java)
                        userModel().updateData(res!!)
                        LocalStorage.saveData(context, "user_session", gson.toJson(res))
                        loading.hide()
                        Utility.pushReplaceAll(context, startingActivity)
                    } else {
                        errorCardLayout.visibility = View.VISIBLE
                        errorTextCard.text = res.message
                        loading.hide()
                    }

                } else {
                    errorCardLayout.visibility = View.VISIBLE
                    errorTextCard.text = "error"
                    loading.hide()
//                        val error = JSONObject(response.errorBody()!!.string())

                }
            }
        })
    }

    private fun googleSignIn() {
        val signInIntent = googleClient.signInIntent
        startActivityForResult(
            signInIntent, RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignInResult(task, this)
        }
    }

    private fun handleGoogleSignInResult(
        completedTask: Task<GoogleSignInAccount>,
        context: Context
    ) {
        try {
            val account = completedTask.getResult(
                ApiException::class.java
            )
            val userData = UserModel(
                null,
                account?.givenName,
                account?.email,
                account?.photoUrl.toString(),
                account?.idToken,
                null, null, null
            )
            submit(context,payload(userData.email!!, null,"google"))
        } catch (e: ApiException) {
            // Sign in was unsuccessful
            Log.e(
                "failed code=", e.statusCode.toString()
            )
        }
    }

    private fun googleSignOut() {
        googleClient.signOut()
            .addOnCompleteListener(this) {
            }
    }

    private fun revokeGoogleAccess() {
        googleClient.revokeAccess()
            .addOnCompleteListener(this) {
                onBackPressed()
            }
    }
}

data class LoginPayload(
    val email: String, val password: String?, val isRegisterUsing: String?
) : Serializable