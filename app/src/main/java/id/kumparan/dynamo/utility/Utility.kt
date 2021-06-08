package id.kumparan.dynamo.utility

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import id.kumparan.dynamo.DeleteThreadActivity
import id.kumparan.dynamo.R
import id.kumparan.dynamo.ReportThreadActivity
import id.kumparan.dynamo.model.MyListThreadModel


class Utility {
    companion object {
        const val API_ENDPOINT = "https://resep-mau.herokuapp.com/"
        const val api_token =
            "Bearer \$2y\$10\$suZZvr1RdWEeW/ly/NyHIOzv35i0DDkxdOsM0V/E0JIDqw2pBSCVG"

        fun getToken(c: Context): String {
            val s = c.getSharedPreferences("TOKEN", MODE_PRIVATE)
            val token = s?.getString("TOKEN", "undefined")
            return token!!
        }

        fun setToken(context: Context, token: String) {
            val pref = context.getSharedPreferences("TOKEN", MODE_PRIVATE)
            pref.edit().apply {
                putString("TOKEN", token)
                apply()
            }
        }

        fun clearToken(context: Context) {
            val pref = context.getSharedPreferences("TOKEN", MODE_PRIVATE)
            pref.edit().clear().apply()
        }

        fun isValidEmail(email: String): Boolean =
            android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

        fun isValidPassword(password: String) = password.length >= 6
        fun customToast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        fun pushReplaceAll(context: Context, intent: Intent) {
            startActivity(
                context,
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK),
                null
            )
        }

        fun pushReplace(context: Context, intent: Intent) {
            startActivity(
                context,
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME),
                null
            )
        }

        fun performOptionsMenu(context: Context, view: View, data: MyListThreadModel) {
            val reportThreadActivity = Intent(context, ReportThreadActivity::class.java)
            reportThreadActivity.putExtra("data", data)
            val popupMenu =
                PopupMenu(context, view)
            popupMenu.inflate(R.menu.option_menu_thread)
            popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    when (item?.itemId) {
                        R.id.report -> {
                            context.startActivity(reportThreadActivity)
                            return true
                        }
                    }
                    return false
                }
            })
            popupMenu.show()
        }

        fun performOptionsMenu2(context: Context, view: View, data: MyListThreadModel) {
            val reportThreadActivity = Intent(context, ReportThreadActivity::class.java)
            val deleteThreadActivity = Intent(context, DeleteThreadActivity::class.java)
            reportThreadActivity.putExtra("data", data)
            deleteThreadActivity.putExtra("data",data)
            val popupMenu =
                PopupMenu(context, view)
            popupMenu.inflate(R.menu.option_menu_thread2)
            popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    when (item?.itemId) {
                        R.id.report -> {
                            context.startActivity(reportThreadActivity)
                            return true
                        }
                        R.id.delete -> {
                        context.startActivity(deleteThreadActivity)
                            return true
                        }
                    }
                    return false
                }
            })
            popupMenu.show()
        }
    }

}