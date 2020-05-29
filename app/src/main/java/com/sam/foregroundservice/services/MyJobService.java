package com.sam.foregroundservice.services;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.sam.foregroundservice.pojo.RxBus;
import com.sam.foregroundservice.pojo.RxEvent;
import com.sam.foregroundservice.utilities.Util;

public class MyJobService extends JobService {


    @Override
    public boolean onStartJob(JobParameters params) {
        Util.scheduleJob(getApplicationContext()); // reschedule the job
        System.out.println("SAM: MyJobService onStartJob");
        RxEvent data = new RxEvent("Service is running!");
        RxBus.publish(data);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        System.out.println("SAM: MyJobService onStopJob");
        return true;
    }
}
