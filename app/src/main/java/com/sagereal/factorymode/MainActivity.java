package com.sagereal.factorymode;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import androidx.annotation.NonNull;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.sagereal.factorymode.activities.BaseActivity;
import com.sagereal.factorymode.activities.SingleTestActivity;
import com.sagereal.factorymode.activities.TestReportActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends BaseActivity {

    private long lastBackDownTime;
    double batteryCapacity;
    private String phone;

    AlertDialog.Builder builder;
    AlertDialog alertDialog;

    TextView deviceNameTextView, deviceTypeTextView, versionNumberTextView, androidVersionTextView,
            batterySizeTextView, ramTextView, romTextView, screenSizeTextView, screenResolutionTextView;
    Button cameraButton, dialButton, singleTestButton, testReportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
        builder = new AlertDialog.Builder(this);
        phone = getString(R.string.call_number);

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
        String result = decimalFormat.format(getBatteryTotal(MainActivity.this)) + getString(R.string.mah);
        batterySizeTextView.setText(result);

        double screenSize = getScreenSize(MainActivity.this);

        decimalFormat = new DecimalFormat("#.##");
        screenSizeTextView.setText(String.format("%s%s", decimalFormat.format(screenSize), getString(R.string.inches)));

        String screenResolution;
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            WindowMetrics currentWindowMetrics = wm.getCurrentWindowMetrics();
            int width = currentWindowMetrics.getBounds().width();
            int height = currentWindowMetrics.getBounds().height();
            screenResolution = width + getString(R.string.pixels_x) + height + getString(R.string.pixels);
        } else {
            int width = wm.getDefaultDisplay().getWidth();
            int height = wm.getDefaultDisplay().getHeight();
            screenResolution = width + getString(R.string.pixels_x) + height + getString(R.string.pixels);
        }
        screenResolutionTextView.setText(screenResolution);
    }

    @Override
    protected void onStart() {
        super.onStart();
        XXPermissions.with(this)
                .permission(Permission.RECORD_AUDIO)
                .permission(Permission.CAMERA)
                .permission(Permission.CALL_PHONE)
                // 设置权限请求拦截器（局部设置）
                //.interceptor(new PermissionInterceptor())
                // 设置不触发错误检测机制（局部设置）
                //.unchecked()
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        if (!allGranted) {
//                            showAlertDialog(permissions);
                            return;
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
                        XXPermissions.startPermissionActivity(MainActivity.this, permissions);
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
        builder.setCancelable(false);
        alertDialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
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

    View.OnClickListener onClickListener = view -> {
        int id = view.getId();
        if (id == R.id.camera) {
            openCamera();
        } else if (id == R.id.dial) {
            callPhoneUI();
        } else if (id == R.id.single_test) {
            Intent intent1 = new Intent(MainActivity.this, SingleTestActivity.class);
            startActivity(intent1);
        } else if (id == R.id.test_report) {
            Intent intent2 = new Intent(MainActivity.this, TestReportActivity.class);
            startActivity(intent2);
        }
    };

    @Override
    public void onBackPressed() {
        long currentBackDownTime = System.currentTimeMillis();
        if (currentBackDownTime - lastBackDownTime < 1000) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, getString(R.string.back_toast), Toast.LENGTH_SHORT).show();
            lastBackDownTime = currentBackDownTime;
        }
    }

    // 直接拨号
    private void callPhone() {
        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(phone)));
    }

    // 跳转到拨号界面
    private void callUI() {
        startActivity(new Intent(Intent.ACTION_CALL_BUTTON));
    }


    // 跳转到拨号界面 同时附带号码
    private void callPhoneUI() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(phone)));
    }

    private void openCamera() {
        Intent mIntent = new Intent();
        mIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        mIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        startActivity(mIntent);
    }

    /**
     * RAM内存大小, 返回1GB/2GB/3GB/4GB/8G/16G
     *
     * @return
     */
    private String getTotalRam() {
        String path = "/proc/meminfo";
        String ramMemorySize = null;
        int totalRam = 0;
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader br = new BufferedReader(fileReader, 4096);
            ramMemorySize = br.readLine().split("\\s+")[1];
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ramMemorySize != null) {
            totalRam = (int) Math.ceil((Float.valueOf(Float.parseFloat(ramMemorySize) / (1024 * 1024)).doubleValue()));
        }

        return totalRam + getString(R.string.gb);
    }

    /**
     * ROM内存大小，返回 64G/128G/256G/512G
     *
     * @return
     */
    private String getTotalRom() {
        File dataDir = Environment.getDataDirectory();
        StatFs stat = new StatFs(dataDir.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        long size = totalBlocks * blockSize;
        long GB = 1024 * 1024 * 1024;
        final long[] deviceRomMemoryMap = {2 * GB, 4 * GB, 8 * GB, 16 * GB, 32 * GB, 64 * GB, 128 * GB, 256 * GB, 512 * GB, 1024 * GB, 2048 * GB};
        int[] displayRomSize = {2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048};
        int i;
        for (i = 0; i < deviceRomMemoryMap.length; i++) {
            if (size <= deviceRomMemoryMap[i]) {
                break;
            }
        }
        return displayRomSize[i] + getString(R.string.gb);
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
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getRealMetrics(realDisplayMetrics);

        int widthPixels = realDisplayMetrics.widthPixels;
        int heightPixels = realDisplayMetrics.heightPixels;

        double x = Math.pow(widthPixels / realDisplayMetrics.xdpi, 2);
        double y = Math.pow(heightPixels / realDisplayMetrics.ydpi, 2);

        return Math.sqrt(x + y);
    }
}