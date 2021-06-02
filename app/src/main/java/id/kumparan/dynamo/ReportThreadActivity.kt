package id.kumparan.dynamo

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import id.kumparan.dynamo.api.ApiUtility
import id.kumparan.dynamo.model.MyListThreadModel
import kotlinx.android.synthetic.main.activity_report_thread.*
import kotlinx.android.synthetic.main.dynamo_card_messagge.view.*
import kotlinx.android.synthetic.main.report_thread_bottom_sheet.*
import java.io.Serializable

class ReportThreadActivity : AppCompatActivity() {
    private val loading = CustomLoading()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_thread)
        tvCommunityName.text = data().communityName
        tvCommunityDesc.text = data().content
        tvUsername.text = data().username
        tvCommunityDate.text = data().createdAt
        popScreen.setOnClickListener {
            onBackPressed()
        }
        initRadioButton()
        initTextField()
        submitBtn.setOnClickListener {
            initDialog()
        }
    }

    private fun data(): MyListThreadModel {
        return intent.extras?.get("data") as MyListThreadModel

    }

    private fun initRadioButton() {
        radioGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { g, c ->
            val radio: RadioButton = findViewById(c)
            if (radio.text.equals("Lainnya")) {
                otherField.visibility = View.VISIBLE

            } else {
                otherField.visibility = View.GONE
                submitBtn.isEnabled = true
            }

        })

    }

    private fun initTextField() {
        otherField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                submitBtn.isEnabled = p0!!.isNotEmpty()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    @SuppressLint("InflateParams")
    private fun initDialog() {
        messageCardLayout.visibility = View.GONE
        val btnSheet = layoutInflater.inflate(R.layout.report_thread_bottom_sheet, null)
        val dialog = BottomSheetDialog(this)
        val messCard = messageIncludeCard.findViewById<TextView>(R.id.messageCard)
        val popMessage = messageIncludeCard.findViewById<ImageView>(R.id.closeError)
        popMessage.setOnClickListener {
            messageCardLayout.visibility = View.GONE
        }
        dialog.setContentView(btnSheet)
        dialog.acceptBtn.setOnClickListener {
            loading.show(this, "Please Wait..")
            val payload =
                ReportThreadPayload(data().id, data().userCreateId, otherField.text.toString())
            ApiUtility().reportThread(payload) { mess ->
                messCard.text = mess
                messageCardLayout.visibility = View.VISIBLE

            }
            dialog.dismiss()
            loading.hide()
        }
        dialog.cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}

data class ReportThreadPayload(val threadId: Int?, val userCreateId: Int?, val reason: String?) :
    Serializable

data class ReportThreadResponse(val status: String?, val data: List<Int>?) : Serializable
