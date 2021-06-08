package id.kumparan.dynamo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.onesignal.OneSignal
import id.kumparan.dynamo.localstorage.LocalStorage
import id.kumparan.dynamo.model.UserViewModel
import id.kumparan.dynamo.utility.ModelInjector

class MainActivity : AppCompatActivity() {
    private val ONESIGNAL_APP_ID = "fe11cf26-f3d1-499a-8e6a-830f0d573b77"
    private val userFactory = ModelInjector.provideUserViewModelFactory()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        initOnesignal()
        val userViewModel =
            ViewModelProvider(this, userFactory).get(UserViewModel::class.java)
        val activationCodeActivity =
            Intent(this, ActivationCodeActivity::class.java)
        val startingActivity =
            Intent(this, StartingActivity::class.java)
        val introActivity =
            Intent(this, IntroActivity::class.java)
        val createProfileActivity =
            Intent(this, CreateProfileActivity::class.java)
        val localUser = LocalStorage.readLocalToModel(this)
        val isVerify = LocalStorage.isVerify(this)
        println("VER $localUser")
        when {
            localUser?.verificationCode?.verification_code == null && localUser?.data?.email == null -> {
                startActivity(introActivity)
                finish()
            }
            localUser.verificationCode?.verification_code != null  -> {
                userViewModel.updateData(localUser)
                startActivity(activationCodeActivity)
                finish()
            }
            else -> {
                userViewModel.updateData(localUser)
                startActivity(startingActivity)
                finish()
            }
        }
    }
    private fun initOnesignal(){
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
    }
}