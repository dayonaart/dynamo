package id.kumparan.dynamo.utility
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

val GOOGLE_REQ_TOKEN =
    "60628986729-pdppdqvd9i74qt2dbbm1l237s99v76sr.apps.googleusercontent.com"
val GSO = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
    .requestIdToken(GOOGLE_REQ_TOKEN)
    .requestEmail()
    .build()
