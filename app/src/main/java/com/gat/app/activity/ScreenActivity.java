package com.gat.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.gat.app.screen.ParcelableScreen;
import com.gat.app.screen.Screen;
import com.gat.dependency.AppComponent;
import com.gat.dependency.Components;
import com.rey.mvp2.Presenter;
import com.rey.mvp2.PresenterManager;
import com.rey.mvp2.impl.MvpActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Rey on 2/14/2017.
 */

public abstract class ScreenActivity<S extends Screen, P extends Presenter> extends MvpActivity<P> {

    private static final String EXTRA_SCREEN = "screen";

    private Unbinder unbinder;
    private volatile S screen;

    protected abstract @LayoutRes int getLayoutResource();

    public static <S extends Screen, P extends Presenter, T extends ScreenActivity<S, P>> void start(Context context, Class<T> activityClass, S screen){
        Intent intent = new Intent(context, activityClass)
                .putExtra(EXTRA_SCREEN, new ParcelableScreen(screen))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected PresenterManager getPresenterManager() {
        return getAppComponent().getPresenterManager();
    }

    @Override
    protected Object getPresenterKey() {
        return getScreen();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    protected S getScreen(){
        if(screen == null){
            synchronized (this){
                if(screen == null) {
                    ParcelableScreen parcelableScreen = getIntent().getParcelableExtra(EXTRA_SCREEN);
                    screen = parcelableScreen == null ? getDefaultScreen() : (S)parcelableScreen.getScreen();
                }
            }
        }
        return screen;
    }

    /**
     * Return a default screen if the intent not contain any extra data.
     * @return
     */
    protected S getDefaultScreen(){
        return null;
    }

    protected AppComponent getAppComponent(){
        return Components.getComponent(getApplicationContext(), AppComponent.class);
    }

}
