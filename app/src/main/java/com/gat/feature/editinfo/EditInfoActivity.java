package com.gat.feature.editinfo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.Constance;
import com.gat.common.util.Strings;
import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.feature.editinfo.entity.EditInfoInput;
import com.gat.feature.login.LoginScreen;
import com.gat.feature.main.MainActivity;
import com.gat.feature.start.StartActivity;
import com.gat.repository.entity.Data;
import com.gat.repository.entity.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by root on 18/04/2017.
 */

public class EditInfoActivity extends ScreenActivity<EditInfoScreen, EditInfoPresenter> {

    private User user;

    @BindView(R.id.imgAvatar)
    CircleImageView imgAvatar;

    @BindView(R.id.txtEditInfo)
    TextView txtEditInfo;

    @BindView(R.id.edtName)
    EditText edtName;

    @BindView(R.id.txtAddress)
    TextView txtAddress;

    @BindView(R.id.imgChangeName)
    ImageView imgChangeName;

    @BindView(R.id.imgSave)
    ImageView imgSave;

    @BindView(R.id.imgBack)
    ImageView imgBack;

    @BindView(R.id.layoutMenutop)
    RelativeLayout layoutMenutop;

    @BindView(R.id.txtTitle)
    TextView txtTitle;



    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private Bitmap currentBitmap;
    private File fileImage;

    private CompositeDisposable disposablesEditInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (User) getIntent().getSerializableExtra("UserInfo");
        disposablesEditInfo = new CompositeDisposable(getPresenter().getResponseEditInfo().subscribe(this::editInfoSuccess),
                getPresenter().onErrorEditInfo().subscribe(this::editInfoError));
        initView();
        handleEvent();
    }

    private void initView() {
        txtTitle.setText("SỬA THÔNG TIN CÁ NHÂN");
        txtTitle.setTextColor(Color.parseColor("#000000"));
        imgBack.setImageResource(R.drawable.narrow_back_black);
        edtName.setFocusableInTouchMode(false);
        edtName.setFocusable(false);
        if (!Strings.isNullOrEmpty(user.imageId())) {
            String url = ClientUtils.getUrlImage(user.imageId(), Constance.IMAGE_SIZE_ORIGINAL);
            ClientUtils.setImage(imgAvatar, R.drawable.ic_profile, url);
        }
        if (!Strings.isNullOrEmpty(user.name())) {
            edtName.setText(user.name());
            txtAddress.setText(user.name());
        }
//        if(user.usuallyLocation().size() > 0){
//            if(!Strings.isNullOrEmpty(user.usuallyLocation().get(0).getAddress())){
//                txtAddress.setText(user.usuallyLocation().get(0).getAddress());
//            }
//        }
    }

    private void handleEvent() {
        txtEditInfo.setOnClickListener(v -> checkPermission());
        imgChangeName.setOnClickListener(v -> {
            edtName.setFocusableInTouchMode(true);
            edtName.setFocusable(true);
        });
        imgSave.setOnClickListener(v -> updateProfile());
        imgBack.setOnClickListener(v -> backToPreviousActivity());
    }

    private void backToPreviousActivity() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


    private void editInfoError(ServerResponse<ResponseData> error) {
        ClientUtils.showToast(error.message());
        if (error.code() == ServerResponse.HTTP_CODE.TOKEN) {
            MainActivity.start(this, StartActivity.class, LoginScreen.instance(Strings.EMPTY, true));
        }
    }
    private void editInfoSuccess(String message) {
        ClientUtils.showToast(message);
        progressBar.setVisibility(View.GONE);
        backToPreviousActivity();
    }

    private void updateProfile() {
        progressBar.setVisibility(View.VISIBLE);
        String name = edtName.getText().toString().trim();
        if (Strings.isNullOrEmpty(name)) {
            ClientUtils.showToast("Bạn không được để trống tên");
            return;
        }
        if (currentBitmap != null) {
            currentBitmap = getResizedBitmap(currentBitmap, 400);
        }
        //update profile
        EditInfoInput input = new EditInfoInput();
        input.setName(name);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        currentBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        input.setImageBase64(encoded);
        getPresenter().requestEditInfo(input);
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        int newHeight = (height * newWidth) / width;
        if (width > newWidth && height > newHeight) {
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);

            // "RECREATE" THE NEW BITMAP
            Bitmap resizedBitmap = Bitmap.createBitmap(
                    bm, 0, 0, width, height, matrix, false);
            bm.recycle();
            return resizedBitmap;
        } else {
            return bm;
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT > 22) {
            try {
                String[] mPermission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(this, mPermission, Constance.REQUEST_ACCESS_PERMISSION_WRITESTORAGE);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            chooseImage();
        }
    }

    private void chooseImage() {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select File"), Constance.REQUEST_ACCESS_IMAGE);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constance.REQUEST_ACCESS_IMAGE:
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = null;
                    if (data != null) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                            if (bitmap != null) {
                                try {
                                    Uri uri = data.getData();
                                    fileImage = new File(getRealPathFromURI(uri));
                                    currentBitmap = bitmap;
                                    if (currentBitmap != null) {
                                        currentBitmap = getResizedBitmap(currentBitmap, 400);
                                    }
                                    imgAvatar.setImageBitmap(currentBitmap);
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    ClientUtils.showToast("User not allow access device");
                }
                break;
            default:
                break;
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constance.REQUEST_ACCESS_PERMISSION_WRITESTORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseImage();
                } else {
                    ClientUtils.showToast("User rejected");
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposablesEditInfo.dispose();
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backToPreviousActivity();
    }


}
