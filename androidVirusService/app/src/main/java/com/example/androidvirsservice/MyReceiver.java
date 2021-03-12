package com.example.androidvirsservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver
{
    public MyReceiver()
    {
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
//        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
//        {
        Intent activityIntent = new Intent(context, MainActivity.class);
        activityIntent.setAction("android.intent.action.MAIN");
        activityIntent.addCategory("android.intent.category.LAUNCHER");
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(activityIntent);
//        }
    }
}