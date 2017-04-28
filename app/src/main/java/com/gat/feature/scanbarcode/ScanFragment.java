package com.gat.feature.scanbarcode;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.gat.R;
import com.gat.app.fragment.ScreenFragment;
import com.gat.common.util.CommonCheck;
import com.gat.common.util.Strings;
import com.gat.data.response.ServerResponse;
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
import io.reactivex.disposables.CompositeDisposable;
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

    private boolean isTorchOn = false;

    private CompositeDisposable disposables;

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
        return ScanScreen.instance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        disposables = new CompositeDisposable(
                getPresenter().onSuccess().subscribe(this::onBookResult),
                getPresenter().onError().subscribe(this::onError)
        );

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

    @Override
    public void onDestroyView() {
        scannerView.setFlash(false);
        scannerView.stopCamera();

        super.onDestroyView();
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
                        scannerView.setAutoFocus(true);
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
        if (CommonCheck.checkIsbnCode(isbn)) {
            getPresenter().searchByIsbn(isbn);
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

    private void onBookResult(int bookEditionId) {
        MainActivity.start(getActivity(), BookDetailActivity.class, BookDetailScreen.instance(bookEditionId));
    }

    private void onError(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        try {
            TimeUnit.SECONDS.sleep(3l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        scannerView.resumeCameraPreview(this);
    }
}
