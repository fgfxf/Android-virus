package com.example.androidvirsservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

public void MyRequestPermission(){
    String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_BOOT_COMPLETED
    };//你需要申请权限的列表

    List<String> mPermissionList = new ArrayList<>();
    for (int i = 0; i < permissions.length; i++) {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
            mPermissionList.add(permissions[i]);
        }
    }
    if(!mPermissionList.isEmpty())
        ActivityCompat.requestPermissions(MainActivity.this,  mPermissionList.toArray(new String[mPermissionList.size()]), 66);
}

    private BroadcastReceiver mFinishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //开启一个发送notification的service
            Intent intentService = new Intent(MainActivity.this,ForegroundService.class);
            startService(intentService);
            //一定要注销广播
            unregisterReceiver(mFinishReceiver);
        }
    };




    @Override
    protected void onDestroy() {
        super.onDestroy();
        //发送广播
        sendBroadcast(new Intent("finish"));
    }
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       MyRequestPermission();
       Intent mForegroundService;
       if (!ForegroundService.serviceIsLive) {
           // Android 8.0使用startForegroundService在前台启动新服务
           mForegroundService = new Intent(this, ForegroundService.class);
           mForegroundService.putExtra("Foreground", "This is a foreground service.");
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
               startForegroundService(mForegroundService);
           } else {
               startService(mForegroundService);
           }
       } else {
           Toast.makeText(this, "前台服务正在运行中...", Toast.LENGTH_SHORT).show();
       }
    }


}