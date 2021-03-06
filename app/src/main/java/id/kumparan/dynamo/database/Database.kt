package id.kumparan.dynamo.database

import id.kumparan.dynamo.model.*
import id.kumparan.dynamo.ui.home.data.HomeTempData

// Private primary constructor inaccessible from other classes
class Database private constructor() {

    // All the User go here!
    var userData = UserModelTempData()
    var userListData = UserListModelTempData()
    var communityListData = CommunityListModelTempData()
    var myCommunityListData = MyCommunityListModelTempData()
    var listThreadData = ListThreadModelTempData()
    var myListThreadData = MyListThreadModelTempData()
    var allCommentListModel = AllCommentListModelTempData()
    var homeData = HomeTempData()
        private set

    companion object {
        // @Volatile - Writes to this property are immediately visible to other threads
        @Volatile
        private var instance: Database? = null

        // The only way to get hold of the FakeDatabase object
        fun getInstance() =
        // Already instantiated? - return the instance
            // Otherwise instantiate in a thread-safe manner
            instance ?: synchronized(this) {
                // If it's still not instantiated, finally create an object
                // also set the "instance" property to be the currently created one
                instance ?: Database().also { instance = it }
            }
    }
}