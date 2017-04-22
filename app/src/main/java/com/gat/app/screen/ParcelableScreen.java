package com.gat.app.screen;

import android.os.Parcel;
import android.os.Parcelable;

import com.gat.common.util.MZDebug;
import com.gat.data.response.UserResponse;
import com.gat.data.response.impl.BookInfo;
import com.gat.data.response.impl.BookReadingInfo;
import com.gat.feature.book_detail.BookDetailScreen;
import com.gat.feature.book_detail.add_to_bookcase.AddToBookcaseScreen;
import com.gat.feature.book_detail.list_user_sharing_book.ListUserSharingBookScreen;
import com.gat.feature.book_detail.self_update_reading.SelfUpdateReadingScreen;
import com.gat.feature.login.LoginScreen;
import com.gat.feature.main.MainScreen;
import com.gat.feature.register.RegisterScreen;
import com.gat.feature.register.update.category.AddCategoryScreen;
import com.gat.feature.register.update.location.AddLocationScreen;
import com.gat.feature.search.SearchScreen;
import com.gat.feature.suggestion.SuggestionScreen;
import com.gat.feature.suggestion.nearby_user.ShareNearByUserDistanceScreen;
import com.gat.feature.suggestion.search.SuggestSearchScreen;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rey on 2/14/2017.
 */

public class ParcelableScreen implements Parcelable {

    private Screen screen;

    private static final int SEARCH = 1;
    private static final int LOGIN = 2;
    private static final int REGISTER = 3;
    private static final int ADD_LOCATION = 4;
    private static final int ADD_CATEGORY = 5;
    private static final int SUGGESTION = 6;
    private static final int SHARE_NEAR_BY_USER_DISTANCE = 7;
    private static final int SUGGESTION_SEARCH = 8;
    private static final int MAIN = 9;
    private static final int BOOK_DETAIL = 10;
    private static final int SELF_UPDATE_READING = 12;
    private static final int LIST_USER_SHARING_BOOK = 13;
    private static final int ADD_TO_BOOKCASE = 14;

    public ParcelableScreen(Screen screen){
        this.screen = screen;
    }

    public Screen getScreen(){
        return screen;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private int getScreenType(){
        if(screen instanceof SearchScreen)
            return SEARCH;
        if(screen instanceof LoginScreen)
            return LOGIN;
        if(screen instanceof RegisterScreen)
            return REGISTER;
        if (screen instanceof AddLocationScreen)
            return ADD_LOCATION;
        if (screen instanceof AddCategoryScreen)
            return ADD_CATEGORY;
        if (screen instanceof SuggestionScreen)
            return SUGGESTION;
        if (screen instanceof MainScreen)
            return MAIN;
        if (screen instanceof ShareNearByUserDistanceScreen)
            return SHARE_NEAR_BY_USER_DISTANCE;
        if (screen instanceof SuggestSearchScreen)
            return SUGGESTION_SEARCH;
        if (screen instanceof BookDetailScreen)
            return BOOK_DETAIL;
        if (screen instanceof SelfUpdateReadingScreen)
            return SELF_UPDATE_READING;
        if (screen instanceof ListUserSharingBookScreen)
            return LIST_USER_SHARING_BOOK;
        if (screen instanceof AddToBookcaseScreen)
            return ADD_TO_BOOKCASE;


        throw new IllegalArgumentException("Not support screen " + screen);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getScreenType());
        if(screen instanceof SearchScreen){
            SearchScreen searchScreen = (SearchScreen)screen;
            dest.writeString(searchScreen.keyword());
        } else if (screen instanceof LoginScreen) {
            LoginScreen loginScreen = (LoginScreen) screen;
            dest.writeString(loginScreen.email());
        } else if (screen instanceof RegisterScreen) {

        } else if (screen instanceof AddLocationScreen) {

        } else if (screen instanceof AddCategoryScreen) {

        } else if (screen instanceof SuggestionScreen) {

        } else if (screen instanceof MainScreen) {

        } else if (screen instanceof ShareNearByUserDistanceScreen) {

        } else if (screen instanceof SuggestSearchScreen) {

        } else if (screen instanceof BookDetailScreen) {
            BookDetailScreen bookDetailScreen = (BookDetailScreen) screen;
            dest.writeInt(bookDetailScreen.editionId());
        } else if (screen instanceof SelfUpdateReadingScreen) {
            SelfUpdateReadingScreen selfUpdateReadingScreen = (SelfUpdateReadingScreen) screen;
            dest.writeParcelable(selfUpdateReadingScreen.bookReadingInfo(), flags);
        } else if (screen instanceof ListUserSharingBookScreen) {
            ListUserSharingBookScreen userSharingBookScreen = (ListUserSharingBookScreen) screen;
            dest.writeList(userSharingBookScreen.listUser());
        } else if (screen instanceof AddToBookcaseScreen ) {
            AddToBookcaseScreen addToBookcaseScreen = (AddToBookcaseScreen) screen;
            dest.writeParcelable(addToBookcaseScreen.bookInfo(), flags);
        }

        else {
            throw new IllegalArgumentException("Not implement serialization for " + screen);
        }

        MZDebug.e("____________________________________ writeToParcel : " + getScreenType());
    }

    ParcelableScreen(Parcel in) {
        int type = in.readInt();

        MZDebug.e("____________________________________ read : " + type);
        switch (type){
            case SEARCH:
                screen = SearchScreen.instance(in.readString());
                break;
            case LOGIN:
                screen = LoginScreen.instance(in.readString());
                break;
            case REGISTER:
                screen = RegisterScreen.instance();
                break;
            case ADD_LOCATION:
                screen = AddLocationScreen.instance();
                break;
            case ADD_CATEGORY:
                screen = AddCategoryScreen.instance();
                break;
            case SUGGESTION:
                screen = SuggestionScreen.instance();
                break;
            case SHARE_NEAR_BY_USER_DISTANCE:
                screen = ShareNearByUserDistanceScreen.instance();
                break;
            case SUGGESTION_SEARCH:
                screen = SuggestSearchScreen.instance();
                break;
            case MAIN:
                screen = MainScreen.instance();
                break;
            case BOOK_DETAIL:
                screen = BookDetailScreen.instance(in.readInt());
                break;
            case SELF_UPDATE_READING:
                screen = SelfUpdateReadingScreen.instance(in.readParcelable(BookReadingInfo.class.getClassLoader()));
                break;
            case LIST_USER_SHARING_BOOK:
                List<UserResponse> myList = null;
                myList = in.readArrayList(ListUserSharingBookScreen.class.getClassLoader());
                screen = ListUserSharingBookScreen.instance(myList);
                break;
            case ADD_TO_BOOKCASE:
                screen = AddToBookcaseScreen.instance(in.readParcelable(BookInfo.class.getClassLoader()));
                break;

            default:
                throw new IllegalArgumentException("Not implement deserialization for type " + type);
        }
    }

    public static final Creator<ParcelableScreen> CREATOR = new Creator<ParcelableScreen>() {
        @Override
        public ParcelableScreen createFromParcel(Parcel source) {
            return new ParcelableScreen(source);
        }

        @Override
        public ParcelableScreen[] newArray(int size) {
            return new ParcelableScreen[size];
        }
    };
}
