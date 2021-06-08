package id.kumparan.dynamo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import id.kumparan.dynamo.api.ApiUtility
import id.kumparan.dynamo.model.MyListThreadModel
import id.kumparan.dynamo.utility.afterTextChanged
import kotlinx.android.synthetic.main.activity_delete_thread.*
import kotlinx.android.synthetic.main.report_thread_bottom_sheet.*
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

class DeleteThreadActivity : AppCompatActivity() {
    private val loading = CustomLoading()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_thread)
        initRadioButton()
        initTextField()
        popScreen.setOnClickListener {
            onBackPressed()
        }
        submitBtn.setOnClickListener {
            initDialog()
        }
    }

    private fun initRadioButton() {
        radioGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { g, c ->
            val radio: RadioButton = findViewById(c)
            if (radio.text.equals("Lainnya")) {
                submitBtn.isEnabled = false
                otherField.visibility = View.VISIBLE

            } else {
                otherField.visibility = View.GONE
                submitBtn.isEnabled = true
            }

        })

    }

    private fun data(): MyListThreadModel {
        return intent.extras?.get("data") as MyListThreadModel

    }

    private fun initTextField() {
        otherField.afterTextChanged {
            submitBtn.isEnabled = it.trim().isNotEmpty()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun parseDate(date: String?): String? {
        val sdf = SimpleDateFormat("dd MMMM y")
        return sdf.format(
            Date.from(Instant.parse(date ?: "1990-11-30T18:35:24.00Z"))
        )
    }

    @SuppressLint("InflateParams")
    private fun initDialog() {
        val btnSheet = layoutInflater.inflate(R.layout.report_thread_bottom_sheet, null)
        val dialog = BottomSheetDialog(this)
        val messCard = messageIncludeCard.findViewById<TextView>(R.id.messageCard)
        val popMessage = messageIncludeCard.findViewById<ImageView>(R.id.closeError)

        popMessage.setOnClickListener {
            messageCardLayout.visibility = View.GONE
        }
        dialog.setContentView(btnSheet)
        dialog.acceptBtn.setOnClickListener {
            messageCardLayout.visibility = View.GONE
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
