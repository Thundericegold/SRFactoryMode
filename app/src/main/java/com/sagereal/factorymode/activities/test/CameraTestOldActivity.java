package com.sagereal.factorymode.activities.test;

import static android.os.Environment.DIRECTORY_DCIM;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.sagereal.factorymode.R;
import com.sagereal.factorymode.activities.camera.AutoFitTextureView;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraTestOldActivity extends BaseTestActivity {

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final String TAG = "zhy";

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    boolean isTested = false;
    //定义界面上的根布局管理器
    private FrameLayout rootLayout;
    Button toggleButton;
    //定义自定义的AutoFitTextureView组件，用于预览摄像头照片
    private AutoFitTextureView textureView;
    //摄像头ID（通常0代表后置摄像头，1代表前置摄像头）
    private String mCameraId = "0";
    //定义代表摄像头的成员变量
    private CameraDevice cameraDevice;
    //预览尺寸
    private Size previewSize;
    private CaptureRequest.Builder previewRequestBuilder;
    //定义用于预览照片的捕获请求
    private CaptureRequest previewRequest;
    //定义CameraCaptureSession成员变量
    private CameraCaptureSession captureSession;
    private ImageReader imageReader;
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
            //当Texture可用时打开摄像头
            Log.d(TAG, "onSurfaceTextureAvailable: ");
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
            Log.d(TAG, "onSurfaceTextureSizeChanged: ");
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
            Log.d(TAG, "onSurfaceTextureDestroyed: ");
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
            Log.d(TAG, "onSurfaceTextureUpdated: ");
        }
    };

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        //摄像头被打开时激发该方法
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.d(TAG, "onOpened: ");
            CameraTestOldActivity.this.cameraDevice = camera;
            //开始预览
            createCameraPreviewSession();
        }

        //摄像头断开连接时激发该方法
        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.d(TAG, "onDisconnected: ");
            camera.close();
            CameraTestOldActivity.this.cameraDevice = null;
        }

        //打开摄像头出现错误时激发该方法
        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.d(TAG, "onError: ");
            camera.close();
            CameraTestOldActivity.this.cameraDevice = null;
            CameraTestOldActivity.this.finish();
        }
    };

    public void switchCamera() {
        if (mCameraId.equals("0")) {
            mCameraId = "1";
            closeCamera();
            reopenCamera();
            toggleButton.setText(R.string.camera_back_shooting);
        } else if (mCameraId.equals("1")) {
            mCameraId = "0";
            closeCamera();
            reopenCamera();
            toggleButton.setText(R.string.camera_proactive);
        }
    }

    private void closeCamera() {
        if (null != captureSession) {
            captureSession.close();
            captureSession = null;
        }
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }
    }

    public void reopenCamera() {
        if (textureView != null) {
            if (textureView.isAvailable()) {
                openCamera(previewSize.getWidth(), previewSize.getHeight());
            } else {
                textureView.setSurfaceTextureListener(mSurfaceTextureListener);
            }
        }
    }

    @Override
    public void initView() {
        toggleButton = findViewById(R.id.toggle);
        passButton = findViewById(R.id.pass);
        failButton = findViewById(R.id.fail);
        rootLayout = findViewById(R.id.root_layout);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_test_old);
        initView();
        initListener();
    }

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
                            Toast.makeText(CameraTestOldActivity.this, getString(R.string.permission_camera), Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        } else {
                            //创建预览摄像头照片的TextureView组件
                            textureView = new AutoFitTextureView(CameraTestOldActivity.this, null);
                            //为TextureView组件设置监听器
                            textureView.setSurfaceTextureListener(mSurfaceTextureListener);
                            rootLayout.addView(textureView);
                            Log.d(TAG, "rootLayout.addView(textureView)完成");
                        }
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                        Toast.makeText(CameraTestOldActivity.this, getString(R.string.permission_camera), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeCamera();
    }


    //根据手机的旋转方向确定预览图像的方向
    private void configureTransform(int viewWidth, int viewHeight) {
        if (null == previewSize) {
            return;
        }
        //获取手机的旋转方向
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewHeight, viewHeight);
        RectF bufferRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        //处理手机横屏的情况
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / previewSize.getHeight(),
                    (float) viewWidth / previewSize.getWidth()
            );
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        //处理手机倒置的情况
        else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        textureView.setTransform(matrix);
    }

    //打开摄像头
    private void openCamera(int width, int height) {
        setUpCameraOutputs(width, height);
        configureTransform(width, height);
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            //如果用户没有授权使用摄像头，则直接返回
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            //打开摄像头
            manager.openCamera(mCameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            Surface surface = new Surface(texture);
            //创建作为预览的CaptureRequest.Builder
            previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            //将textureView的surface作为CaptureRequest.Builder的目标
            previewRequestBuilder.addTarget(new Surface(texture));
            Log.d(TAG, "createCameraPreviewSession: 1");
            cameraDevice.createCaptureSession(Arrays.asList(surface,
                    imageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    //如果摄像头为null,直接结束方法
                    if (null == cameraDevice) {
                        return;
                    }
                    Log.d(TAG, "createCameraPreviewSession: 2");
                    //当摄像头已经准备好时，开始显示预览
                    captureSession = session;
                    //设置自动对焦模式
                    previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                    //设置自动曝光模式
                    previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                            CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                    //开始显示相机预览
                    previewRequest = previewRequestBuilder.build();
                    Log.d(TAG, "createCameraPreviewSession: 3");
                    try {
                        //设置预览时连续捕获图像数据
                        captureSession.setRepeatingRequest(previewRequest, null, null);
                        Log.d(TAG, "createCameraPreviewSession: 4");
                    } catch (CameraAccessException e) {
                        Log.d(TAG, "createCameraPreviewSession: 5");
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Toast.makeText(CameraTestOldActivity.this, getString(R.string.configure_failed_toast), Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            Log.d(TAG, "createCameraPreviewSession: 6");
            e.printStackTrace();
        }
    }

    private void setUpCameraOutputs(int width, int height) {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            //获取指定摄像头的特性
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(mCameraId);
            //获取摄像头支持的配置属性
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            //获取摄像头支持的最大尺寸
            Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                    new CompareSizesByArea());
            //创建一个ImageReader对象，用于获取摄像头的图像数据
            imageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),
                    ImageFormat.JPEG, 2);
            imageReader.setOnImageAvailableListener(reader -> {
                //当照片数据可用时激发该方法
                //获取捕获的照片数据
                Image image = reader.acquireLatestImage();
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.remaining()];
                //使用IO流将照片写入指定文件
                File file = new File(getExternalFilesDir(DIRECTORY_DCIM), "pic.jpg");
                buffer.get(bytes);
                try (FileOutputStream output = new FileOutputStream(file)) {
                    output.write(bytes);
                    Toast.makeText(CameraTestOldActivity.this, getString(R.string.save), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    image.close();
                }
            }, null);
            //获取最佳的预览尺寸
            previewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), width, height, largest);
            //根据选中的预览尺寸来调整预览组件(TextureView)的长宽比
//            int orientation = getResources().getConfiguration().orientation;
//            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                textureView.setAspectRatio(previewSize.getWidth(), previewSize.getHeight());
//            } else {
//                textureView.setAspectRatio(previewSize.getHeight(), previewSize.getWidth());
//            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println(getString(R.string.error));
        }
    }

    private Size chooseOptimalSize(Size[] choices, int width, int height, Size
            aspectRatio) {
        //收集摄像头支持的大过预览Surface的分辨率
        List<Size> bigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }
        //如果找到多个预览尺寸，获取其中面积最小的
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            System.out.println(getString(R.string.size_error_toast));
            return choices[0];
        }
    }

    //为Size定义一个比较器Comparator
    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            //强转为long保证不会发生溢出
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
        }
    }
}