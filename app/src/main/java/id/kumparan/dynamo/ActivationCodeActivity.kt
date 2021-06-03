package id.kumparan.dynamo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import id.kumparan.dynamo.api.ApiUtility
import id.kumparan.dynamo.localstorage.LocalStorage
import id.kumparan.dynamo.model.UserViewModel
import id.kumparan.dynamo.utility.ModelInjector
import id.kumparan.dynamo.utility.Utility
import kotlinx.android.synthetic.main.activity_activation_code.*


class ActivationCodeActivity : AppCompatActivity() {
    private val userFactory = ModelInjector.provideUserViewModelFactory()
    private val gson = Gson()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activation_code)
        countDownTimer().start()
        val createProfileActivity = Intent(this, CreateProfileActivity::class.java)
        val editText =
            arrayOf<EditText>(et1, et2, et3, et4, et5, et6)
        val sentCodeText =
            "Masukkan kode verikasi yang telah dikirim melalui email ke " + "<b>" + "${userModel().getData().value?.data?.email}" + "</b> "
        sentCode.text = HtmlCompat.fromHtml(sentCodeText, HtmlCompat.FROM_HTML_MODE_LEGACY)
        configureEditText(editText)
        userModel().getData().observe(this,{
            val codeValidation= it.verificationCode?.verification_code?.map { c->c.toString() }
            submitBtn.setOnClickListener {
                val inputCode = editText.map { et-> et.text.toString() }
                println("CODE VER $inputCode $codeValidation")
                if (inputCode == codeValidation) {
                    LocalStorage.saveData(this, "isVerify", "true")
                    Utility.pushReplaceAll(this, createProfileActivity)
                } else {
                    errorTextCard.text=resources.getText(R.string.wrong_code_verification)
                    errorCardLayout.visibility = View.VISIBLE
                }
            }
        })
        closeError.setOnClickListener {
            errorCardLayout.visibility = View.GONE
        }
    }

    private fun userModel(): UserViewModel {
        return ViewModelProvider(this, userFactory).get(UserViewModel::class.java)
    }

    private fun resendCode() {
        countDownText.setOnClickListener {
            pbProgress.visibility=View.VISIBLE
            ApiUtility().resendVerificationCode(userModel()){message ->
                errorTextCard.text=message
                errorCardLayout.visibility =View.VISIBLE
                if (message=="Success"){
                    LocalStorage.saveData(this, "user_session", gson.toJson(userModel().getData().value))
                    pbProgress.visibility=View.GONE
                    countDownTimer().start()
                }else{
                    pbProgress.visibility=View.GONE
                }
            }
        }
    }

    private fun configureEditText(editText: Array<EditText>) {
        for (et in editText) {
            et.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    submitBtn.isEnabled = et1.text.isNotEmpty()
                            && et2.text.isNotEmpty()
                            && et3.text.isNotEmpty()
                            && et4.text.isNotEmpty()
                            && et5.text.isNotEmpty()
                            && et6.text.isNotEmpty()
                }

                override fun afterTextChanged(s: Editable?) {
                    val text = s.toString()
                    when (et) {
                        et1 -> if (text.length == 1) editText[1].requestFocus()
                        et2 -> if (text.length == 1) editText[2].requestFocus()
                        else if (text.isEmpty()) editText[0].requestFocus()
                        et3 -> if (text.length == 1) editText[3].requestFocus()
                        else if (text.isEmpty()) editText[1].requestFocus()
                        et4 -> if (text.length == 1) editText[4].requestFocus()
                        else if (text.isEmpty()) editText[2].requestFocus()
                        et5 -> if (text.length == 1) editText[5].requestFocus()
                        else if (text.isEmpty()) editText[3].requestFocus()
                        et6 -> if (text.isEmpty()) editText[4].requestFocus()
                    }
                }
            })
        }
    }

    private fun countDownTimer(): CountDownTimer {
        return object : CountDownTimer(59000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(p0: Long) {
                countDownText.text = "${(p0 / 1000)} detik"
                countDownText.setOnClickListener {
                }
            }

            override fun onFinish() {
                countDownText.text = "Kirim Ulang"
                countDownText.setOnClickListener {
                    resendCode()
                }

            }

        }
    }
}
