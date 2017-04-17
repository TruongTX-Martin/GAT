package com.gat.feature.book_detail.comment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.gat.R;
import com.gat.common.util.Views;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mryit on 4/17/2017.
 */

public class CommentActivity extends Activity{


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.image_view_back)
    void onBack () {
        finish();
    }


}
