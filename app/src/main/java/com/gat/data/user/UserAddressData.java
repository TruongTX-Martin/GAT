package com.gat.data.user;

import android.location.Address;

import com.google.android.gms.maps.model.LatLng;
import com.google.auto.value.AutoValue;

/**
 * Created by ducbtsn on 3/5/17.
 */

@AutoValue
public abstract class UserAddressData {
    public abstract String address();
    public abstract LatLng location();
    public static UserAddressData instance(String address, LatLng location) {
        return new AutoValue_UserAddressData(address, location);
    }
}
