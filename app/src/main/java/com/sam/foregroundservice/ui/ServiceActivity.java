package com.sam.foregroundservice.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.sam.foregroundservice.R;
import com.sam.foregroundservice.receivers.MyAlarmReceiver;
import com.sam.foregroundservice.services.MyStickyService;
import com.sam.foregroundservice.utilities.Util;

public class ServiceActivity extends AppCompatActivity {

    private MaterialButton start, stop, next;
    private LinearLayout LL_root;
    private final ServiceReceiver _serviceReceiver = new ServiceReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        start = (MaterialButton)findViewById(R.id.start);
        stop = (MaterialButton)findViewById(R.id.stop);
        next = (MaterialButton)findViewById(R.id.next);
        LL_root = (LinearLayout)findViewById(R.id.LL_root);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyAlarmReceiver.class);
                intent.setAction(Util.START);
                sendBroadcast(intent);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyAlarmReceiver.class);
                intent.setAction(Util.STOP);
                sendBroadcast(intent);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goNext();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Util.START);
        intentFilter.addAction(Util.STOP);
        intentFilter.addAction(Util.STOPNEXT);
        LocalBroadcastManager.getInstance(this).registerReceiver(_serviceReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(_serviceReceiver);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private class ServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("SAM: ServiceActivity ServiceReceiver onReceive");
            Bundle extras = intent.getExtras();
            if (extras != null) {
                if (extras.containsKey(Util.MSG)) {
                    String msg = extras.getString(Util.MSG);
                    String action = intent.getAction();
                    System.out.println("SAM: ServiceReceiver msg: "+msg);
                    System.out.println("SAM: ServiceReceiver action: "+action);

                    Snackbar snackbar = null;
                    if (msg != null) {
                        snackbar = Snackbar.make(LL_root, msg, Snackbar.LENGTH_LONG);
                    }else{
                        snackbar = Snackbar.make(LL_root, getString(R.string.default_msg), Snackbar.LENGTH_LONG);
                    }
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(ServiceActivity.this, R.color.nebula));
                    int snackbarTextId = com.google.android.material.R.id.snackbar_text;
                    TextView textView = (TextView)sbView.findViewById(snackbarTextId);
                    textView.setTextColor(getResources().getColor(R.color.navyblue));
                    textView.setTextSize(25);
                    snackbar.show();
                    if(action!=null && action.equals(Util.STOPNEXT)){
                        goToJobActivity();
                    }
                }
            }
        }
    }

    private void goNext(){
        MyStickyService myStickyService = new MyStickyService(ServiceActivity.this);

        if(Util.isMyServiceRunning(ServiceActivity.this, myStickyService.getClass())){
            Intent intent = new Intent(getApplicationContext(), MyAlarmReceiver.class);
            intent.setAction(Util.STOPNEXT);
            sendBroadcast(intent);
        }else{
            goToJobActivity();
        }

    }

    private void goToJobActivity(){
        Intent newintent = new Intent(ServiceActivity.this, JobActivity.class);
        startActivity(newintent);
    }

}
