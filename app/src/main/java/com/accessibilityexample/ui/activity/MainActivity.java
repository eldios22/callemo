package com.accessibilityexample.ui.activity;



import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.accessibilityexample.Service.ForegroundService;
import com.accessibilityexample.R;
import com.accessibilityexample.Service.MyAccessibilityService;



public class MainActivity extends AppCompatActivity {
    Button btnStartService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        btnStartService = findViewById(R.id.buttonStartService);


        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                startService();
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Settings.canDrawOverlays(this) && isAccessibilitySettingsOn(getApplicationContext())){
        LocalBroadcastManager localBroadcastManager
                = LocalBroadcastManager.getInstance(this);
        Intent broadcastIntent = new Intent("my-local-broadcast");
        localBroadcastManager.sendBroadcast(broadcastIntent);

        Intent passedActivityIntent = new Intent(this,PassedActivity.class);
        startActivity(passedActivityIntent);
        finish();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    public void startService() {
        LocalBroadcastManager localBroadcastManager
                = LocalBroadcastManager.getInstance(this);

        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
        ContextCompat.startForegroundService(this, serviceIntent);


        if (!Settings.canDrawOverlays(this)) {
            int REQUEST_CODE = 101;
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE);
        }
        while(!Settings.canDrawOverlays(this)){


        }


        if (!isAccessibilitySettingsOn(getApplicationContext())) {
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));

        }
    }
    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + MyAccessibilityService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.e("TAG", "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e("TAG", "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.e("TAG", "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.e("TAG", "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.e("TAG", "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.e("TAG", "***ACCESSIBILITY IS DISABLED***");
        }

        return false;
    }


}
