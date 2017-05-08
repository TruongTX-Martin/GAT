package com.gat.feature.start;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.CommonCheck;
import com.gat.common.util.Strings;
import com.gat.data.firebase.entity.Notification;
import com.gat.data.firebase.entity.NotificationParcelable;
import com.gat.feature.login.LoginActivity;
import com.gat.feature.login.LoginPresenter;
import com.gat.feature.login.LoginScreen;
import com.gat.feature.main.MainActivity;
import com.gat.feature.main.MainScreen;
import com.gat.feature.register.RegisterActivity;
import com.gat.feature.register.RegisterScreen;
import com.gat.repository.entity.User;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Timed;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

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


    @Override
    protected int getLayoutResource() {
        return R.layout.start_activity;
    }

    @Override
    protected void onCreate(Bundle savedBundle) {
        super.onCreate(savedBundle);

        disposables = new CompositeDisposable(
                getPresenter().loginResult().subscribe(this::onLoginResult),
                getPresenter().onError().subscribe(this::onLoginError),
                Observable.interval(5, TimeUnit.SECONDS).timeInterval().delay(2, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(longTimed -> {
                    changePage();
                })

        );

        // array index of all welcome sliders
        arrLayoutIdx = new int[]{
                R.layout.activity_intro_slider1,
                R.layout.activity_intro_slider1,
                R.layout.activity_intro_slider1
        };

        if (!getScreen().tokenChange())
            getPresenter().loadLocalUser();

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
            start(getApplicationContext(), MainActivity.class, MainScreen.instance());
        });

        loginBtn.setOnClickListener(view -> {
            start(getApplicationContext(), LoginActivity.class, LoginScreen.instance(Strings.EMPTY));
        });

        registerBtn.setOnClickListener(view -> {
            start(getApplicationContext(), RegisterActivity.class, RegisterScreen.instance());
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
            dotViews[i].setTextSize(40);
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
        if (user.isValid()) {
            getPresenter().loginOnFirebase();
            if (getIntent().getExtras()!= null) {
                processDataPayload();
            } else {
                start(this, MainActivity.class, MainScreen.instance());
            }
            finish();
        }
    }

    private void onLoginError(String error) {
        // Do nothing
    }

    private void changePage() {
        int current = viewPager.getCurrentItem();
        viewPager.setCurrentItem((current + 1) % 3);
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
            ImageView imageView =  (ImageView)view.findViewById(R.id.intro_image);
            TextView headerText = (TextView)view.findViewById(R.id.intro_header);
            TextView contentText = (TextView)view.findViewById(R.id.intro_content);
            switch (position) {
                case 0:
                    imageView.setImageResource(R.drawable.intro_slider_1);
                    headerText.setText(getString(R.string.intro_title_1));
                    contentText.setText(getString(R.string.intro_content_1));
                    break;
                case 1:
                    imageView.setImageResource(R.drawable.intro_slider_2);
                    headerText.setText(getString(R.string.intro_title_2));
                    contentText.setText(getString(R.string.intro_content_2));
                    break;
                case 2:
                    imageView.setImageResource(R.drawable.intro_slider_3);
                    headerText.setText(getString(R.string.intro_title_3));
                    contentText.setText(getString(R.string.intro_content_3));
                    break;
                default:
                    break;
            }

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


    private void processDataPayload() {
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            int pushType = bundle.getInt("pushType");
            int badge = bundle.getInt("badge");
            int senderId = bundle.getInt("senderId");
            int requestId = bundle.getInt("requestId");
            Notification notification = Notification.instance(Strings.EMPTY,Strings.EMPTY, pushType, "default", badge, senderId, requestId);
            CommonCheck.processNotification(notification, this);
        }
    }

}
