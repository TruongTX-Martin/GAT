package com.gat.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gat.app.screen.Screen;
import com.gat.dependency.AppComponent;
import com.gat.dependency.Components;
import com.rey.mvp2.Presenter;
import com.rey.mvp2.PresenterManager;
import com.rey.mvp2.impl.MvpFragment;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by mryit on 3/26/2017.
 */

public abstract class ScreenFragment<S extends Screen, P extends Presenter> extends MvpFragment<P> {

    protected static final String EXTRA_SCREEN = "screen";

    private Unbinder unbinder;
    private volatile S screen;
    protected View mView;
    protected Context mContext;

    protected abstract @LayoutRes int getLayoutResource();

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity().getApplicationContext();
        mView = inflater.inflate(getLayoutResource(), container, false);
        unbinder = ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    @Override
    protected Object getPresenterKey() {
        return getScreen();
    }

    @Override
    protected PresenterManager getPresenterManager() {
        return getAppComponent().getPresenterManager();
    }

    protected S getScreen(){
        if(screen == null){
            synchronized (this){
                if(screen == null) {
                    screen =  getDefaultScreen();
                }
            }
        }
        return screen;
    }


    protected S getDefaultScreen(){
        return null;
    }

    @Override
    protected Class<P> getPresenterClass() {
        return null;
    }

    protected AppComponent getAppComponent(){
        return Components.getComponent(getActivity().getApplicationContext(), AppComponent.class);
    }
}
