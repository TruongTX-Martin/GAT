package com.gat.feature.scanbarcode;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gat.R;
import com.gat.app.fragment.ScreenFragment;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.CommonCheck;
import com.gat.common.util.Strings;
import com.gat.data.response.ServerResponse;
import com.gat.domain.SchedulerFactory;
import com.gat.feature.book_detail.BookDetailActivity;
import com.gat.feature.book_detail.BookDetailScreen;
import com.gat.feature.main.MainActivity;
import com.gat.feature.search.SearchPresenter;
import com.gat.feature.search.SearchScreen;
import com.gat.repository.entity.Book;
import com.google.zxing.Result;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

/**
 * Created by mozaa on 02/04/2017.
 */

public class ScanFragment extends ScreenFragment<ScanScreen, ScanPresenter> implements ZXingScannerView.ResultHandler {
    private static final String TAG = ScanFragment.class.getSimpleName();

    private static final int REQUEST_CAMERA = 1;

    @BindView(R.id.barcode_view)
    ZXingScannerView scannerView;

    @BindView(R.id.btn_light)
    Button lightBtn;

    @BindView(R.id.imgBack)
    ImageView imgBack;

    @BindView(R.id.imgSave)
    ImageView imgSave;

    @BindView(R.id.txtTitle)
    TextView txtTitle;

    @BindView(R.id.layoutMenutop)
    RelativeLayout headerLayout;

    private boolean isTorchOn = false;

    private CompositeDisposable disposables;

    private boolean cameraPermision = false;

    @Override
    protected int getLayoutResource() {
        return R.layout.scanbarcode_activity;
    }

    @Override
    protected Class<ScanPresenter> getPresenterClass() {
        return ScanPresenter.class;
    }

    @Override
    protected ScanScreen getDefaultScreen() {
        return ScanScreen.FROM_TAB;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        headerLayout.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.background_header_blue, null));
        txtTitle.setText(getString(R.string.register_title));
        txtTitle.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorWhite, null));
        if (getScreen().from() == ScanScreen.From.SEARCH)
            imgBack.setImageResource(R.drawable.narrow_back_black);
        else
            imgBack.setVisibility(View.GONE);
        imgSave.setVisibility(View.GONE);


        Log.d(TAG, "onCreateView");
        disposables = new CompositeDisposable(
                getPresenter().onSuccess().subscribe(this::onBookResult),
                getPresenter().onError().subscribe(this::onError)
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.cameraPermision = checkPermission(REQUEST_CAMERA);
        }

        scannerView.setResultHandler(this);

        if (this.cameraPermision) {
            if (getUserVisibleHint()) {
                scannerView.startCamera();
                scannerView.setAutoFocus(true);
            }
        }

        lightBtn.setOnClickListener(v -> {
            // Turn light on
            scannerView.setFlash(!isTorchOn);
            isTorchOn = !isTorchOn;
            changeBtnText();
        });

        if (getScreen().from() == ScanScreen.From.SEARCH)
            imgBack.setOnClickListener(v -> {
                // TODO go back to search screen
            });

        return view;
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        scannerView.setFlash(false);
        isTorchOn = false;
        scannerView.stopCamera();
        super.onDestroyView();
    }

    private boolean checkPermission(int which) {
        return ContextCompat.checkSelfPermission(getContext(), CAMERA ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        //ActivityCompat.requestPermissions(getActivity(), new String[]{CAMERA}, REQUEST_CAMERA);
        requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA:
                Log.d(TAG, "RequestCameraResult:" + grantResults.length);
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        cameraPermision = true;
                        if (getUserVisibleHint()) {
                            scannerView.setAutoFocus(true);
                            scannerView.startCamera();
                        }
                    }else {
                        Toast.makeText(getContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }


    public void stopCamera() {
        scannerView.stopCamera();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        disposables.dispose();
        scannerView.stopCamera();
        super.onDestroy();
    }

    @Override
    public void handleResult(Result result) {
        String isbn = result.getText();
        Log.d(TAG, isbn);
        if (CommonCheck.checkIsbnCode(isbn)) {
            getPresenter().searchByIsbn(isbn);
        } else {
            showErrorDialog(getString(R.string.isbn_invalid), getString(R.string.isbn_invalid_message));
            scannerView.resumeCameraPreview(this);
        }
    }

    public void cleanView(){
        scannerView.removeAllViews();
        scannerView.stopCamera();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, "UserVisibleHint:"+isVisibleToUser + "," + cameraPermision);
        if (!isVisibleToUser && scannerView != null) {
            scannerView.setFlash(false);
            isTorchOn = false;
            scannerView.stopCamera();
            changeBtnText();
        } else if (isVisibleToUser && scannerView != null) {
            if (!cameraPermision) {
                requestPermission();
            } else {
                scannerView.startCamera();
                scannerView.setAutoFocus(true);
            }
            changeBtnText();
        }

    }

    private void changeBtnText() {
        if (!isTorchOn) {
            lightBtn.setText(getString(R.string.btn_turn_on_light));
        } else {
            lightBtn.setText(getString(R.string.btn_turn_off_light));
        }
    }

    private void onBookResult(int bookEditionId) {
        MainActivity.start(getActivity(), BookDetailActivity.class, BookDetailScreen.instance(bookEditionId));
    }

    private void onError(String error) {
        showErrorDialog(getString(R.string.book_not_found), error);
        scannerView.resumeCameraPreview(this);
    }

    private void showErrorDialog(String header, String content) {
        ClientUtils.showErrorDialog(header, content, getContext());
    }
}
