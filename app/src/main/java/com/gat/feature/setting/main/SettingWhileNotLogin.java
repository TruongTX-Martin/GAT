package com.gat.feature.setting.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gat.R;
import com.gat.common.util.Strings;
import com.gat.feature.login.LoginScreen;
import com.gat.feature.main.MainActivity;
import com.gat.feature.start.StartActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mozaa on 23/05/2017.
 */

public class SettingWhileNotLogin extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frame_setting_while_not_login, container, false);

        ButterKnife.bind(this, view);

        return view;
    }


    @OnClick(R.id.button_go_login)
    void goLogin () {
        MainActivity.startAndClear(getActivity(), StartActivity.class, LoginScreen.instance(Strings.EMPTY, true));
    }


}
