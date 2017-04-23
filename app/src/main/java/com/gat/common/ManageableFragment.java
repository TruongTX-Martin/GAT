package com.gat.common;

import android.app.Activity;

/**
 * Created by Rey on 12/4/2015.
 */
public interface ManageableFragment {

    /**
     * Called when this fragment is put at top of stack.
     */
    void onFragmentUp(Activity activity);

    /**
     * Called when this fragment is remove from top of stack.
     */
    void onFragmentDown(Activity activity);

    boolean shouldHandleBackKey();

    void onFragmentBackPressed();
}
