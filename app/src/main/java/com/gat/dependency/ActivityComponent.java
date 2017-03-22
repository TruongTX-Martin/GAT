package com.gat.dependency;

import dagger.Subcomponent;

/**
 * Created by Rey on 5/10/2016.
 */
@ActivityScope
@Subcomponent(
    modules = {
        ActivityModule.class
    }
)
public interface ActivityComponent {

}
