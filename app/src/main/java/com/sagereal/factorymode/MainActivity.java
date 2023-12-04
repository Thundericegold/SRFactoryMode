package com.sagereal.factorymode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sagereal.factorymode.activities.BaseActivity;
import com.sagereal.factorymode.activities.SingleTestActivity;
import com.sagereal.factorymode.activities.TestReportActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;

public class MainActivity extends BaseActivity {

    private long lastBackDownTime;
    double batteryCapacity;
    private final String phone = "tel:112";
    private final int CALL_PHONE_REQUEST_CODE = 10001;//拨号请求码
    private final int CAMERA_REQUEST_CODE = 10002;//相机请求码

    TextView deviceNameTextView,deviceTypeTextView,versionNumberTextView,androidVersionTextView,
            batterySizeTextView,ramTextView,romTextView,screenSizeTextView,screenResolutionTextView;
    Button cameraButton,dialButton,singleTestButton,testReportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();

        String deviceName = Build.DEVICE;
        String deviceType = Build.MODEL;
        String androidVersion = Build.VERSION.RELEASE;
        String versionNumber = Build.DISPLAY;

        deviceNameTextView.setText(deviceName);
        deviceTypeTextView.setText(deviceType);
        versionNumberTextView.setText(versionNumber);
        androidVersionTextView.setText(androidVersion);
        ramTextView.setText(getTotalRam());
        romTextView.setText(getTotalRom());

        DecimalFormat decimalFormat = new DecimalFormat("#");
        String result = decimalFormat.format(getBatteryTotal(MainActivity.this))+getString(R.string.mah);
        batterySizeTextView.setText(result);

        double screenSize = getScreenSize(MainActivity.this);
        screenSizeTextView.setText( decimalFormat.format(screenSize)+getString(R.string.inches));

        String screenResolution;
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            WindowMetrics currentWindowMetrics = wm.getCurrentWindowMetrics();
            int width = currentWindowMetrics.getBounds().width();
            int height = currentWindowMetrics.getBounds().height();
            screenResolution = width + "X" + height;
        }else {
            int width = wm.getDefaultDisplay().getWidth();
            int height = wm.getDefaultDisplay().getHeight();
            screenResolution = width + "X" + height;
        }
        screenResolutionTextView.setText(screenResolution);
    }

    @Override
    public void initView() {
        deviceNameTextView = findViewById(R.id.device_name);
        deviceTypeTextView = findViewById(R.id.device_type);
        versionNumberTextView = findViewById(R.id.version_number);
        androidVersionTextView = findViewById(R.id.android_version);
        batterySizeTextView = findViewById(R.id.battery_size);
        ramTextView = findViewById(R.id.ram);
        romTextView = findViewById(R.id.rom);
        screenSizeTextView = findViewById(R.id.screen_size);
        screenResolutionTextView = findViewById(R.id.screen_resolution);

        cameraButton = findViewById(R.id.camera);
        dialButton = findViewById(R.id.dial);
        singleTestButton = findViewById(R.id.single_test);
        testReportButton = findViewById(R.id.test_report);
    }

    @Override
    public void initListener() {
        cameraButton.setOnClickListener(onClickListener);
        dialButton.setOnClickListener(onClickListener);
        singleTestButton.setOnClickListener(onClickListener);
        testReportButton.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.camera:
                    openCamera();
                    break;
                case R.id.dial:
                    callPhoneUI();
                    break;
                case R.id.single_test:
                    Intent intent1 = new Intent(MainActivity.this, SingleTestActivity.class);
                    startActivity(intent1);
                    break;
                case R.id.test_report:
                    Intent intent2 = new Intent(MainActivity.this, TestReportActivity.class);
                    startActivity(intent2);
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        long currentBackDownTime = System.currentTimeMillis();
        if (currentBackDownTime - lastBackDownTime < 1000){
            super.onBackPressed();
        }else {
            Toast.makeText(this, getString(R.string.back_toast), Toast.LENGTH_SHORT).show();
            lastBackDownTime = currentBackDownTime;
        }
    }

    // 判断是否有拨号权限
    private boolean ifHaveCallPhonePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // 动态申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_REQUEST_CODE);
            return false;
        }
        return true;
    }

    // 判断是否有相机权限
    private boolean ifHaveCameraPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 动态申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            return false;
        }
        return true;
    }

    // 申请权限回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALL_PHONE_REQUEST_CODE) {
            if (permissions.length != 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "请添加拨号权限后重试", Toast.LENGTH_SHORT).show();
            } else {
//                callPhone();
//                callUI();
                callPhoneUI();
            }
        }else if (requestCode == CAMERA_REQUEST_CODE) {
            if (permissions.length != 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "请添加相机权限后重试", Toast.LENGTH_SHORT).show();
            } else {
                openCamera();
            }
        }
    }

    // 直接拨号
    private void callPhone() {
        if (ifHaveCallPhonePermission()) {
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(phone)));
        }
    }

    // 跳转到拨号界面
    private void callUI() {
        if (ifHaveCallPhonePermission()) {
            startActivity(new Intent(Intent.ACTION_CALL_BUTTON));
        }
    }


    // 跳转到拨号界面 同时附带号码
    private void callPhoneUI() {
        if (ifHaveCallPhonePermission()) {
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(phone)));
        }
    }

    private void openCamera() {
        if (ifHaveCameraPermission()) {
            Intent mIntent = new Intent();
            mIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            mIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
            startActivity(mIntent);
        }
    }

    /**
     * RAM内存大小, 返回1GB/2GB/3GB/4GB/8G/16G
     * @return
     */
    public static String getTotalRam(){
        String path = "/proc/meminfo";
        String ramMemorySize = null;
        int totalRam = 0 ;
        try{
            FileReader fileReader = new FileReader(path);
            BufferedReader br = new BufferedReader(fileReader, 4096);
            ramMemorySize = br.readLine().split("\\s+")[1];
            br.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(ramMemorySize != null){
            totalRam = (int)Math.ceil((Float.valueOf(Float.parseFloat(ramMemorySize) / (1024 * 1024)).doubleValue()));
        }

        return totalRam + "GB";
    }

    /**
     * ROM内存大小，返回 64G/128G/256G/512G
     * @return
     */
    private static String getTotalRom() {
        File dataDir = Environment.getDataDirectory();
        StatFs stat = new StatFs(dataDir.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        long size = totalBlocks * blockSize;
        long GB = 1024 * 1024 * 1024;
        final long[] deviceRomMemoryMap = {2*GB, 4*GB, 8*GB, 16*GB, 32*GB, 64*GB, 128*GB, 256*GB, 512*GB, 1024*GB, 2048*GB};
        String[] displayRomSize = {"2GB","4GB","8GB","16GB","32GB","64GB","128GB","256GB","512GB","1024GB","2048GB"};
        int i;
        for(i = 0 ; i < deviceRomMemoryMap.length; i++) {
            if(size <= deviceRomMemoryMap[i]) {
                break;
            }
            if(i == deviceRomMemoryMap.length) {
                i--;
            }
        }
        return displayRomSize[i];
    }

    public double getBatteryTotal(Context context) {
        if (batteryCapacity > 0) {
            return batteryCapacity;
        }
        Object mPowerProfile;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";
        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS).getConstructor(Context.class).newInstance(context);
            batteryCapacity = (double) Class.forName(POWER_PROFILE_CLASS).getMethod("getBatteryCapacity").invoke(mPowerProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return batteryCapacity;
    }

    public static double getScreenSize(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        double width = metrics.widthPixels / metrics.xdpi;
        double height = metrics.heightPixels / metrics.ydpi;
        double screenSize = Math.sqrt(width * width + height * height);
        return screenSize;
    }
}