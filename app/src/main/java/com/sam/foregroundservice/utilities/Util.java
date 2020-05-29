package com.sam.foregroundservice.utilities;

import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.sam.foregroundservice.pojo.RxBus;
import com.sam.foregroundservice.pojo.RxEvent;
import com.sam.foregroundservice.services.MyJobService;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class Util {

    private static final int JOB_ID = 44;

    public static final String START = "START";
    public static final String STOP = "STOP";
    public static final String STOPNEXT = "STOPNEXT";
    public static final String MSG = "MSG";

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    System.out.println("SAM: isMyServiceRunning? "+true);
                    return true;
                }
            }
        }
        System.out.println("SAM: isMyServiceRunning? "+false);
        return false;
    }

    public static void sendBroadcast(Context context, String msg, String action){
        System.out.println("SAM: Util sendBroadcast action: "+action);
        Intent intent = new Intent();
        if(action!=null && !action.equals("") && !action.isEmpty())
        intent.setAction(action);
        intent.putExtra(Util.MSG, msg);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, MyJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceComponent);
        builder.setMinimumLatency(1000);    // wait at least
        builder.setOverrideDeadline(10*1000);  //delay time
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);  // require unmetered network
        builder.setRequiresCharging(false);  // we don't care if the device is charging or not
        builder.setRequiresDeviceIdle(true); // device should be idle

        JobScheduler jobScheduler = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            jobScheduler = context.getSystemService(JobScheduler.class);
        }
        if (jobScheduler != null) {
            jobScheduler.schedule(builder.build());
        }
    }

    private static boolean isJobServiceOn( Context context ) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService( Context.JOB_SCHEDULER_SERVICE ) ;

        boolean hasBeenScheduled = false ;

        if (jobScheduler != null) {
            for ( JobInfo jobInfo : jobScheduler.getAllPendingJobs() ) {
                if ( jobInfo.getId() == JOB_ID ) {
                    hasBeenScheduled = true ;
                    break ;
                }
            }
        }

        return hasBeenScheduled ;
    }

    public static void stopJob(Context context, boolean showToast){
        if(isJobServiceOn(context)){
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService( Context.JOB_SCHEDULER_SERVICE ) ;
            if (jobScheduler != null) {
                jobScheduler.cancel(JOB_ID);
                if(showToast){
                    RxEvent data = new RxEvent("Service is stopped!");
                    RxBus.publish(data);
                }
            }
        }
    }
}
