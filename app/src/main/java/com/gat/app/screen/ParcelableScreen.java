package com.gat.app.screen;

import android.os.Parcel;
import android.os.Parcelable;

import com.gat.feature.login.LoginScreen;
import com.gat.feature.main.MainScreen;
import com.gat.feature.register.RegisterScreen;
import com.gat.feature.register.update.category.AddCategoryScreen;
import com.gat.feature.register.update.location.AddLocationScreen;
import com.gat.feature.search.SearchScreen;
import com.gat.feature.suggestion.SuggestionScreen;
import com.gat.feature.suggestion.nearby_user.ShareNearByUserDistanceScreen;

import java.util.List;

/**
 * Created by Rey on 2/14/2017.
 */

public class ParcelableScreen implements Parcelable {

    private final Screen screen;

    private static final int MAIN = 0;
    private static final int SEARCH = 1;
    private static final int LOGIN = 2;
    private static final int REGISTER = 3;
    private static final int ADD_LOCATION = 4;
    private static final int ADD_CATEGORY = 5;
    private static final int SUGGESTION = 6;
    private static final int SHARE_NEAR_BY_USER_DISTANCE = 7;

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
//            ShareNearByUserDistanceScreen shareNearByUserDistanceScreen =
//                    (ShareNearByUserDistanceScreen) screen;
//            dest.writeList(shareNearByUserDistanceScreen.listUsers());
        }

        else
            throw new IllegalArgumentException("Not implement serialization for " + screen);
    }

    ParcelableScreen(Parcel in) {
        int type = in.readInt();
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
