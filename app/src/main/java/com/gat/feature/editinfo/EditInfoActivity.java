package com.gat.feature.editinfo;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.customview.CircleImage;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.Constance;
import com.gat.common.util.Strings;
import com.gat.repository.entity.User;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by root on 18/04/2017.
 */

public class EditInfoActivity extends ScreenActivity<EditInfoScreen, EditInfoPresenter> {

    private User user;

    @BindView(R.id.imgAvatar)
    CircleImageView imgAvatar;

    @BindView(R.id.txtEditInfo)
    TextView txtEditInfo;

    @BindView(R.id.txtName)
    TextView txtName;

    @BindView(R.id.txtAddress)
    TextView txtAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (User) getIntent().getSerializableExtra("UserInfo");
        initView();
        handleEvent();
    }

    private void initView(){
        if (!Strings.isNullOrEmpty(user.imageId())) {
            String url = ClientUtils.getUrlImage(user.imageId(), Constance.IMAGE_SIZE_ORIGINAL);
            ClientUtils.setImage(imgAvatar, R.drawable.ic_profile, url);
        }
        if (!Strings.isNullOrEmpty(user.name())) {
            txtName.setText(user.name());
        }
    }

    private void handleEvent(){
        txtEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
    }


    private void chooseImage() {
//        if (Build.VERSION.SDK_INT > 22) {
//            String[] mPermission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
//            ActivityCompat.requestPermissions(this, mPermission, Constance.REQUEST_ACCESS_PERMISSION_WRITESTORAGE);
//        } else {
            try {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select File"), Constance.REQUEST_ACCESS_IMAGE);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Constance.REQUEST_ACCESS_IMAGE:
                if(resultCode ==  RESULT_OK) {
                    Bitmap bitmap = null;
                    if (data != null) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                            if(bitmap != null) {

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                   ClientUtils.showToast("User not allow access device");
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected EditInfoScreen getDefaultScreen() {
        return EditInfoScreen.instance();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.layout_editinfo_activity;
    }

    @Override
    protected Class<EditInfoPresenter> getPresenterClass() {
        return EditInfoPresenter.class;
    }



}
