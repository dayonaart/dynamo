package id.kumparan.dynamo.api

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import com.facebook.*
import id.kumparan.dynamo.BuildConfig
import id.kumparan.dynamo.FacebookResponse
import id.kumparan.dynamo.utility.Utility
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class Api {
    companion object {
        private var retrofit: Retrofit? = null
        private var opt = OkHttpClient.Builder().apply {
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)
            connectTimeout(30, TimeUnit.SECONDS)
        }.build()

        private fun getClient(): Retrofit {
            return if (retrofit != null) {
                retrofit!!
            } else {
                retrofit = Retrofit.Builder().apply {
                    client(opt)
                    baseUrl(Utility.API_ENDPOINT)
                    addConverterFactory(GsonConverterFactory.create())
                }.build()
                retrofit!!
            }
        }

        fun instance(): ApiServices = getClient().create(ApiServices::class.java)

        private fun facebookProperty(jsonObject: JSONObject, param: String): String? {
            return if (jsonObject.has(param)) {
                jsonObject.getString(param)
            } else {
                null
            }
        }

        @SuppressLint("LongLogTag")
        fun getUserProfileFacebook(
            token: AccessToken?,
            userId: String?,
            callback: (FacebookResponse) -> Unit
        ) {
            val parameters = Bundle()
            parameters.putString(
                "fields",
                "id, first_name, middle_name, last_name, name, picture, email"
            )
            GraphRequest(
                token,
                "/$userId/",
                parameters,
                HttpMethod.GET
            ) { response ->
                val jsonObject = response.jsonObject
                // Facebook Access Token
                // You can see Access Token only in Debug mode.
                // You can't see it in Logcat using Log.d, Facebook did that to avoid leaking user's access token.
                if (BuildConfig.DEBUG) {
                    FacebookSdk.setIsDebugEnabled(true)
                    FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS)
                }
                val facebookId = facebookProperty(jsonObject, "id")
                val facebookFirstName = facebookProperty(jsonObject, "first_name")
                val facebookMiddleName = facebookProperty(jsonObject, "middle_name")
                val facebookLastName = facebookProperty(jsonObject, "last_name")
                val facebookName = facebookProperty(jsonObject, "name")
                val facebookEmail = facebookProperty(jsonObject, "email")

                // Facebook Profile Pic URL
                if (jsonObject.has("picture")) {
                    val facebookPictureObject = jsonObject.getJSONObject("picture")
                    if (facebookPictureObject.has("data")) {
                        val facebookDataObject = facebookPictureObject.getJSONObject("data")
                        if (facebookDataObject.has("url")) {
                            val facebookProfilePicURL = facebookDataObject.getString("url")
                            callback(
                                FacebookResponse(
                                    facebookFirstName,
                                    facebookMiddleName,
                                    facebookLastName,
                                    facebookName,
                                    facebookProfilePicURL,
                                    facebookEmail
                                )
                            )
                        }
                    }
                } else {
                    callback(
                        FacebookResponse(
                            facebookFirstName,
                            facebookMiddleName,
                            facebookLastName,
                            facebookName,
                            null,
                            facebookEmail
                        )
                    )
                }

            }.executeAsync()
        }

        fun isFacebookLoggedIn(): Boolean {
            val accessToken = AccessToken.getCurrentAccessToken()
            return accessToken != null && !accessToken.isExpired
        }
    }
}