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
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gat.R;
import com.gat.app.fragment.ScreenFragment;
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
        return ScanScreen.instance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

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
            Toast.makeText(getContext(), getString(R.string.isbn_invalid), Toast.LENGTH_SHORT).show();
            showErrorDialog(getString(R.string.isbn_invalid), getString(R.string.isbn_invalid_message));
            scannerView.resumeCameraPreview(this);
        }
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
        showErrorDialog(error, getString(R.string.book_not_found));
        scannerView.resumeCameraPreview(this);
    }

    private void showErrorDialog(String header, String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.instance);
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.instance);
        View view = layoutInflater.inflate(R.layout.layout_popup_scan, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        TextView textHeader = (TextView)view.findViewById(R.id.scan_popup_header);
        textHeader.setText(header);
        TextView textContent = (TextView) view.findViewById(R.id.scan_popup_content);
        textContent.setText(content);
        Button button = (Button) view.findViewById(R.id.btn_popup_ok);
        button.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
