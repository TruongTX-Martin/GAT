package com.gat.feature.scanbarcode;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.gat.R;
import com.gat.app.fragment.ScreenFragment;
import com.gat.common.util.CommonUtil;
import com.gat.common.util.Strings;
import com.gat.data.response.ServerResponse;
import com.gat.feature.search.SearchPresenter;
import com.gat.feature.search.SearchScreen;
import com.gat.repository.entity.Book;
import com.google.zxing.Result;

import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

/**
 * Created by ducbtsn on 3/15/17.
 */

public class ScanBarcodeActivity extends ScreenFragment<SearchScreen, SearchPresenter> implements ZXingScannerView.ResultHandler {
    private static final String TAG = ScanBarcodeActivity.class.getSimpleName();

    private static final int REQUEST_CAMERA = 1;

    @BindView(R.id.barcode_view)
    ZXingScannerView scannerView;

    @BindView(R.id.btn_light)
    Button lightBtn;

    private boolean isTorchOn = false;

    private CompositeDisposable disposables;

    @Override
    protected int getLayoutResource() {
        return R.layout.scanbarcode_activity;
    }

    @Override
    protected Class<SearchPresenter> getPresenterClass() {
        return SearchPresenter.class;
    }

    @Override
    protected SearchScreen getDefaultScreen() {
        return SearchScreen.instance(Strings.EMPTY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        disposables = new CompositeDisposable(
                getPresenter().getBookResult().subscribe(this::onBookResult),
                getPresenter().onError().subscribe(this::onError)
        );

        //scannerView = (ZXingScannerView)findViewById(R.id.barcode_view);

        //lightBtn = (Button) findViewById(R.id.btn_light);

        boolean cameraPermission = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cameraPermission = checkPermission(REQUEST_CAMERA);
        }
        if (cameraPermission) {
            scannerView.setResultHandler(this);
            scannerView.setAutoFocus(true);
            scannerView.startCamera();
        } else {
            requestPermission();
        }
        lightBtn.setOnClickListener(v -> {
            // Turn light on
            scannerView.setFlash(!isTorchOn);
            isTorchOn = !isTorchOn;
            changeBtnText();
        });

        return view;
    }

    private boolean checkPermission(int which) {
        return ContextCompat.checkSelfPermission(getContext(), CAMERA ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{CAMERA}, REQUEST_CAMERA);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        scannerView.setResultHandler(this);
                        scannerView.startCamera();
                    }else {
                        Toast.makeText(getContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        disposables.dispose();
        scannerView.stopCamera();
        super.onDestroy();
    }

    @Override
    public void handleResult(Result result) {
        String isbn = result.getText();
        Log.d(TAG, isbn);
        if (CommonUtil.checkIsbnCode(isbn)) {
            getPresenter().setIsbn(isbn);
        } else {
            Toast.makeText(getContext(), getString(R.string.isbn_invalid), Toast.LENGTH_SHORT).show();
            scannerView.resumeCameraPreview(this);
        }
    }

    private void changeBtnText() {
        if (!isTorchOn) {
            lightBtn.setText(getString(R.string.btn_turn_on_light));
        } else {
            lightBtn.setText(getString(R.string.btn_turn_off_light));
        }
    }

    private void onBookResult(Book book) {
        // TODO go to book detail page
        scannerView.resumeCameraPreview(this);
    }

    private void onError(ServerResponse serverResponse) {
        Toast.makeText(getContext(), getString(R.string.error_throw), Toast.LENGTH_SHORT).show();
        scannerView.resumeCameraPreview(this);
    }
}
