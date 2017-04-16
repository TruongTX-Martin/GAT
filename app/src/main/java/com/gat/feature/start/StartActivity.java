package com.gat.feature.start;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.util.Strings;
import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.feature.login.LoginActivity;
import com.gat.feature.login.LoginPresenter;
import com.gat.feature.login.LoginScreen;
import com.gat.feature.main.MainActivity;
import com.gat.feature.main.MainScreen;
import com.gat.feature.message.GroupMessageActivity;
import com.gat.feature.register.RegisterActivity;
import com.gat.feature.search.SearchActivity;
import com.gat.feature.search.SearchScreen;
import com.gat.repository.entity.LoginData;
import com.gat.repository.entity.User;

import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by ducbtsn on 3/4/17.
 */

public class StartActivity extends ScreenActivity<LoginScreen, LoginPresenter> {

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @BindView(R.id.layout_dots)
    LinearLayout layoutDots;

    @BindView(R.id.skip)
    TextView skip;

    @BindView(R.id.btn_login)
    Button loginBtn;

    @BindView(R.id.btn_register)
    Button registerBtn;

    private ViewPagerAdapter viewPagerAdapter;

    private int[] arrLayoutIdx;
    private TextView[] dotViews;

    private CompositeDisposable disposables;
    private ProgressDialog progressDialog;

    @Override
    protected int getLayoutResource() {
        return R.layout.start_activity;
    }

    @Override
    protected void onCreate(Bundle savedBundle) {
        super.onCreate(savedBundle);

        progressDialog =  new ProgressDialog(this);

        disposables = new CompositeDisposable(
                getPresenter().loginResult().subscribe(this::onLoginResult),
                getPresenter().onError().subscribe(this::onLoginError),
                getPresenter().loadLocalLoginData().filter(loginData -> loginData != LoginData.EMPTY)
                        .subscribe(loginData -> {
                            onLogging(true);
                            getPresenter().setIdentity(loginData);
                        })
        );

        // array index of all welcome sliders
        arrLayoutIdx = new int[]{
                R.layout.activity_intro_slider1,
                R.layout.activity_intro_slider2,
                R.layout.activity_intro_slider3
        };

        // adding bottom dots
        addBottomDots(0);

        viewPagerAdapter = new ViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_IDLE:
                        break;
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        break;
                    case ViewPager.SCROLL_STATE_SETTLING:
                        break;
                    default:
                        break;
                }
            }
        });

        skip.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), GroupMessageActivity.class);
            startActivity(intent);
        });

        loginBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        });

        registerBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        });
    }

    /**
     * add bottom dots view corresponding with each page
     * @param page
     */
    private void addBottomDots(int page) {
        dotViews = new TextView[arrLayoutIdx.length];

        int colorsActive = ContextCompat.getColor(this, R.color.dot_light_screen1);
        int colorsInactive = ContextCompat.getColor(this, R.color.dot_dark_screen1);

        layoutDots.removeAllViews();
        for (int i = 0; i < dotViews.length; i++) {
            dotViews[i] = new TextView(this);
            dotViews[i].setText("•");
            dotViews[i].setTextSize(35);
            dotViews[i].setTextColor(colorsInactive);
            layoutDots.addView(dotViews[i]);
        }

        if (dotViews.length > 0) {
            dotViews[page].setTextColor(colorsActive);
        }
    }


    @Override
    protected Class<LoginPresenter> getPresenterClass() {
        return LoginPresenter.class;
    }

    @Override
    protected LoginScreen getDefaultScreen() {
        return LoginScreen.instance(Strings.EMPTY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.dispose();
    }

    private void onLoginResult(User user) {
        onLogging(false);
        start(this, MainActivity.class, MainScreen.instance());
        finish();
    }

    private void onLoginError(ServerResponse<ResponseData> responseData) {
        onLogging(false);
    }

    private void onLogging(boolean enter) {
        if (enter) {
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.authenticating));
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }
    }

    /**
     * View pager adapter
     */
    public class ViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public ViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(arrLayoutIdx[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return arrLayoutIdx.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

}
