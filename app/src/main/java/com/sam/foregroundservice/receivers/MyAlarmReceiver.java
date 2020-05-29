package com.sam.foregroundservice.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.sam.foregroundservice.services.MyStickyService;
import com.sam.foregroundservice.utilities.Util;

public class MyAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("SAM: MyAlarmReceiver onReceive");
        String action = intent.getAction();
        callService(context, action);
    }

    private void callService(Context context, String action){
        MyStickyService myStickyService = new MyStickyService(context);
        Intent mServiceIntent = new Intent(context, myStickyService.getClass());
        mServiceIntent.setAction(action);
        System.out.println("SAM: MyAlarmReceiver startService: action: "+action);
        if(action.equals(Util.STOP) || action.equals(Util.STOPNEXT)){
            if (Util.isMyServiceRunning(context, myStickyService.getClass())) {
                startService(context, mServiceIntent);
            }
        }else{
            if (!Util.isMyServiceRunning(context, myStickyService.getClass())) {
                startService(context, mServiceIntent);
            }
        }

    }

    private void startService(Context context, Intent mServiceIntent){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(mServiceIntent);
        } else {
            context.startService(mServiceIntent);
        }
    }

}
