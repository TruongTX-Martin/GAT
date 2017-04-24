package com.gat.dependency;

/**
 * Created by Rey on 5/10/2016.
 */
public interface HasComponent<T extends Object> {

    T getComponent();

}
