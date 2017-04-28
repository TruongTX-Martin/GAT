package com.gat.common;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rey on 12/4/2015.
 */
public class FragmentManagerHelper {

    private static final String TAG = FragmentManagerHelper.class.getName();

    private FragmentActivity mActivity;
    private FragmentManager mFragmentManager;
    private List<Fragment> mFragments = new ArrayList<>();
    private List<Fragment> mPendingFragments = new ArrayList<>();

    private boolean mActivityCreated = false;

    private int mContainerId;

    public FragmentManagerHelper(FragmentActivity activity, int containerId){
        mActivity = activity;
        mFragmentManager = mActivity.getSupportFragmentManager();
        mContainerId = containerId;
    }

    public void onCreate(){
        mActivityCreated = true;
        if(!mPendingFragments.isEmpty()) {
            for (int i = 0, size = mPendingFragments.size(); i < size; i++)
                mFragments.add(mPendingFragments.get(i));
            mPendingFragments.clear();
            notifyCurrentFragmentUp();
        }
    }

    public void onAttachFragment(Fragment fragment){
        if(!mActivityCreated && fragment instanceof ManageableFragment)
            mPendingFragments.add(fragment);
    }

    public boolean isFragmentHandleBackKey(){
        Fragment fragment = getCurrentFragment();
        if(fragment != null && fragment instanceof ManageableFragment)
            return ((ManageableFragment) fragment).shouldHandleBackKey();

        return false;
    }

    public boolean onBackPressed(boolean finishActivityIfNeed) {
        if(isFragmentHandleBackKey()){
            ManageableFragment manageableFragment = (ManageableFragment)getCurrentFragment();
            manageableFragment.onFragmentBackPressed();
            return true;
        }
        else if(!popBackStack()){
            if(finishActivityIfNeed)
                mActivity.finish();
            return false;
        }
        else
            return true;
    }

    public int getBackStackCount(){
        return mFragments.size();
    }

    private Fragment getFragmentAt(int index) {
        return mFragments.isEmpty() ?  null : mFragments.get(index);
    }

    private Fragment getCurrentFragment(){
        return getFragmentAt(getBackStackCount() - 1);
    }

    private void notifyCurrentFragmentUp(){
        Fragment curFragment = getCurrentFragment();
        if(curFragment != null && curFragment instanceof ManageableFragment)
            ((ManageableFragment)curFragment).onFragmentUp(mActivity);
    }

    private void notifyCurrentFragmentDown(){
        Fragment curFragment = getCurrentFragment();
        if(curFragment != null && curFragment instanceof ManageableFragment)
            ((ManageableFragment)curFragment).onFragmentDown(mActivity);
    }

    public boolean showFragment(Fragment fragment, boolean addNew){
        return showFragment(fragment, 0, 0, 0, 0, addNew);
    }

    public boolean showFragment(Fragment fragment, int animEnter, int animExit, int animPopEnter, int animPopExit, boolean addNew){
        boolean result = false;
        if(!fragment.isVisible()){
            notifyCurrentFragmentDown();

            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            if (addNew)
                mFragmentManager.popBackStackImmediate(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            transaction.setCustomAnimations(animEnter, animExit, animPopEnter, animPopExit);
            transaction.replace(mContainerId, fragment, null);
            transaction.addToBackStack(null);

            try {
                transaction.commit();
                result = true;

                mFragments.add(fragment);
                notifyCurrentFragmentUp();
            }
            catch (Exception ex){
                Log.e(TAG, "Error commit transaction", ex);
            }
        }

        return result;
    }

    /**
     * Pop the top state off the back stack. If the stack has only 1 fragment, then it cannot be poped.
     */
    public boolean popBackStack(){
        return getBackStackCount() > 1 && popBackStack(getBackStackCount() - 1, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     * Pop all back stack states up to the one with the given index.
     * @param flags Either 0 or FragmentManager.POP_BACK_STACK_INCLUSIVE.
     */
    public boolean popBackStack(int index, int flags){
        if(index < 0)
            return false;

        boolean result = false;
        notifyCurrentFragmentDown();
        try {
            mFragmentManager.popBackStackImmediate(index, flags);

            result = true;

            for(int i = mFragments.size() - 1, end = flags == FragmentManager.POP_BACK_STACK_INCLUSIVE ? index - 1 : index; i > end; i--)
                mFragments.remove(i);

            notifyCurrentFragmentUp();
        }
        catch (Exception ex){
            Log.e(TAG, "Error pop back stack", ex);
        }

        return result;
    }
}
