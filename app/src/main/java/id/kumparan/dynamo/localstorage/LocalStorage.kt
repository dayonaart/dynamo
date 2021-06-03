package id.kumparan.dynamo.localstorage

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import id.kumparan.dynamo.api.User
import id.kumparan.dynamo.api.WrappedResponse
import id.kumparan.dynamo.model.UserModel
import java.io.*

class LocalStorage {
    companion object{
       fun readLocalData(context: Context?,fileName: String?):String? {
           try {
               var fileInputStream: FileInputStream? = null
               fileInputStream = context?.openFileInput(fileName)
               val inputStreamReader = InputStreamReader(fileInputStream)
               val bufferedReader = BufferedReader(inputStreamReader)
               val stringBuilder: StringBuilder = StringBuilder()
               var text: String? = null
               while ({ text = bufferedReader.readLine(); text }() != null) {
                   stringBuilder.append(text)
               }
               return stringBuilder.toString()

           }catch (e: FileNotFoundException) {
               return null;
           } catch (e: UnsupportedEncodingException) {
               return null;
           } catch (e: IOException) {
               return null;
           }

       }
        fun saveData(context: Context,fileName:String,fileData:String?):Boolean{
           val fileName:String = fileName
           val data:String? = fileData
           val fileOutputStream: FileOutputStream
           return try {
               fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
               fileOutputStream.write(data?.toByteArray())
               true
           }catch (e: Exception){
               e.printStackTrace()
               false
           }
       }
        fun deleteSaveData(context: Context,fileName:String):Boolean{
            val fileName:String = fileName
            val data:String? = null
            val fileOutputStream: FileOutputStream
            return try {
                fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
                fileOutputStream.write(data?.toByteArray())
                true
            }catch (e: Exception){
                e.printStackTrace()
                false
            }
        }

        fun isVerify(context: Context):Boolean?{
            return try {
              readLocalData(context,"isVerify").toBoolean()
            }catch (e: Exception){
                false;
            }
        }
       fun readLocalToModel(context: Context): User?{
           val gson = Gson()
           return try {
               val sType = object : TypeToken<User>() {}.type
               gson.fromJson<User>(readLocalData(context,"user_session"), sType)
           }catch (e: Exception){
               null;
           }

       }

   }
}