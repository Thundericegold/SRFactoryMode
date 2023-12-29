package com.sagereal.factorymode.activities.test;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.sagereal.factorymode.R;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class CameraTestActivity extends BaseTestActivity {
    private PreviewView previewView;
    Button toggleButton;
    //摄像头ID（通常0代表后置摄像头，1代表前置摄像头）
    private String mCameraId = "0";
    boolean isTested = false;
    Preview preview;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;

    private ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture;
    private ProcessCameraProvider cameraProvider;
    private Camera camera;
    CameraSelector backCamera, frontCamera;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_test);
        initView();
        builder = new AlertDialog.Builder(this);
    }

    @Override
    public void initView() {
        previewView = findViewById(R.id.previewView);
        toggleButton = findViewById(R.id.toggle);
        passButton = findViewById(R.id.pass);
        failButton = findViewById(R.id.fail);
    }

    @Override
    public void initListener() {
        toggleButton.setOnClickListener(onClickListener);
        passButton.setOnClickListener(onClickListener);
        failButton.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = v -> {
        int id = v.getId();
        if (id == R.id.toggle) {
            switchCamera();
            isTested = true;
        } else if (id == R.id.pass) {
            if (isTested) {
                editor.putInt(STATUS_CAMERA, 0);
                editor.commit();
                setResult(RESULT_PASS);
                finish();
            }
        } else if (id == R.id.fail) {
            editor.putInt(STATUS_CAMERA, 1);
            editor.commit();
            setResult(RESULT_FAIL);
            finish();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        XXPermissions.with(this)
                .permission(Permission.CAMERA)
                // 设置权限请求拦截器（局部设置）
                //.interceptor(new PermissionInterceptor())
                // 设置不触发错误检测机制（局部设置）
                //.unchecked()
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        if (!allGranted) {
                            return;
                        } else {
                            initListener();
                            preview = new Preview.Builder()
                                    .build();
                            backCamera = new CameraSelector.Builder()
                                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                                    .build();
                            frontCamera = new CameraSelector.Builder()
                                    .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                                    .build();

                            cameraProviderListenableFuture = ProcessCameraProvider.getInstance(CameraTestActivity.this);
                            cameraProviderListenableFuture.addListener(() -> {
                                try {
                                    cameraProvider = cameraProviderListenableFuture.get();
                                    bindPreview(cameraProvider, preview, backCamera);
                                } catch (ExecutionException | InterruptedException e) {
                                    // No errors need to be handled for this Future.
                                    // This should never be reached.
                                }
                            }, ContextCompat.getMainExecutor(CameraTestActivity.this));
                        }
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                        showAlertDialog(permissions);
                    }
                });
    }

    private void showAlertDialog(List<String> permissions) {
        builder.setTitle(getString(R.string.permission_alert_title))
                .setMessage(getString(R.string.permission_alert_message))
                .setPositiveButton(getString(R.string.permission_alert_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //跳转应用消息，间接打开应用权限设置-效率高
                        XXPermissions.startPermissionActivity(CameraTestActivity.this, permissions);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.permission_alert_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    public void switchCamera() {
        if (mCameraId.equals("0")) {
            mCameraId = "1";
            bindPreview(cameraProvider, preview, frontCamera);
            toggleButton.setText(R.string.camera_back_shooting);
        } else if (mCameraId.equals("1")) {
            mCameraId = "0";
            bindPreview(cameraProvider, preview, backCamera);
            toggleButton.setText(R.string.camera_proactive);
        }
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider, Preview preview, CameraSelector cameraSelector) {
        //解除所有绑定，防止CameraProvider重复绑定到Lifecycle发生异常
        cameraProvider.unbindAll();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview);
    }
}
