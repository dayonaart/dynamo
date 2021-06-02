package id.kumparan.dynamo.utility

import id.kumparan.dynamo.database.Database
import id.kumparan.dynamo.model.*
import id.kumparan.dynamo.ui.home.data.HomeModelFactory
import id.kumparan.dynamo.ui.home.data.HomeRepository

object ModelInjector {
    fun provideUserViewModelFactory(): UserModelModelFactory {
        val userData = UserModelRepository.getInstance(Database.getInstance().userData)
        return UserModelModelFactory(userData)
    }

    fun provideHomeViewModelFactory(): HomeModelFactory {
        val homeData = HomeRepository.getInstance(Database.getInstance().homeData)
        return HomeModelFactory(homeData)
    }
    fun provideUserListViewModelFactory(): UserListModelFactory {
        val userListData = UserListModelRepository.getInstance(Database.getInstance().userListData)
        return UserListModelFactory(userListData)
    }
    fun provideListCommunityViewModeFactory(): CommunityListModelFactory {
        val communityListData = CommunityListModelRepository.getInstance(Database.getInstance().communityListData)
        return CommunityListModelFactory(communityListData)
    }
    fun provideListMyCommunityViewModeFactory(): MyCommunityListModelFactory {
        val myCommunityListData = MyCommunityListModelRepository.getInstance(Database.getInstance().myCommunityListData)
        return MyCommunityListModelFactory(myCommunityListData)
    }
    fun provideListThreadViewModelFactory(): ListThreadModelFactory {
        val data = ListThreadModelRepository.getInstance(Database.getInstance().listThreadData)
        return ListThreadModelFactory(data)
    }
    fun provideMyListThreadViewModelFactory(): MyListThreadModelFactory {
        val data = MyListThreadModelRepository.getInstance(Database.getInstance().myListThreadData)
        return MyListThreadModelFactory(data)
    }
    fun provideAllCommentListViewModelFactory(): AllCommentListModelFactory {
        val data = AllCommentListModelRepository.getInstance(Database.getInstance().allCommentListModel)
        return AllCommentListModelFactory(data)
    }
}

