package com.sagereal.factorymode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    double batteryCapacity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String deviceName = Build.DEVICE;
        String deviceType = Build.MODEL;
        String androidVersion = Build.VERSION.RELEASE;
        String versionNumber = Build.DISPLAY;


        TextView deviceNameTextView = findViewById(R.id.device_name);
        TextView deviceTypeTextView = findViewById(R.id.device_type);
        TextView versionNumberTextView = findViewById(R.id.version_number);
        TextView androidVersionTextView = findViewById(R.id.android_version);
        TextView batterySizeTextView = findViewById(R.id.battery_size);
        TextView ramTextView = findViewById(R.id.ram);
        TextView romTextView = findViewById(R.id.rom);
        TextView screenSizeTextView = findViewById(R.id.screen_size);
        TextView screenResolutionTextView = findViewById(R.id.screen_resolution);

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