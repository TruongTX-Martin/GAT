package com.gat.dependency;

import com.gat.data.api.GatApi;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;

/**
 * Created by ducbtsn on 3/13/17.
 */
@Singleton
@Component(
        modules = {
                DataModule.class
        }
)
public interface DataComponent {
    @Named("private") GatApi getPrivateGatApi();
    @Named("public") GatApi getPublicGatApi();
}
