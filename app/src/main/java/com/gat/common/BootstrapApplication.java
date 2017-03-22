package com.gat.common;

import android.app.Application;
import android.content.Context;

import com.gat.BuildConfig;
import com.gat.dependency.AppComponent;
import com.gat.dependency.AppModule;
import com.gat.dependency.DaggerAppComponent;
import com.gat.dependency.HasComponent;

import java.lang.reflect.Method;

import timber.log.Timber;

/**
 * Created by Rey on 1/9/2016.
 */
public class BootstrapApplication extends Application implements HasComponent<AppComponent>{

    private AppComponent mAppComponent;

    public static BootstrapApplication get(Context context){
        return (BootstrapApplication)context.getApplicationContext();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if(BuildConfig.MULTI_DEX){
            try {
                Class<?> clazz = Class.forName("android.support.multidex.MultiDex");
                Method method = clazz.getDeclaredMethod("install", Context.class);
                method.invoke(null, this);
            } catch (Exception e) {
                Timber.e(e, "Error");
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if(BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());

        //Global dependencies graph is created here
        mAppComponent = prepareAppComponent();
    }

    protected AppComponent prepareAppComponent(){
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    @Override
    public AppComponent getComponent() {
        return mAppComponent;
    }

}
