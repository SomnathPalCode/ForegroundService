package com.sam.foregroundservice.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.sam.foregroundservice.R;
import com.sam.foregroundservice.pojo.RxBus;
import com.sam.foregroundservice.pojo.RxEvent;
import com.sam.foregroundservice.utilities.Util;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class JobActivity extends AppCompatActivity {

    private MaterialButton start, stop;
    private LinearLayout LL_root;
    private Disposable disposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job);

        start = (MaterialButton)findViewById(R.id.start);
        stop = (MaterialButton)findViewById(R.id.stop);
        LL_root = (LinearLayout)findViewById(R.id.LL_root);

        start.setOnClickListener(view -> Util.scheduleJob(getApplicationContext()));

        stop.setOnClickListener(view -> Util.stopJob(getApplicationContext(), true));

        disposable = RxBus.subscribe((Consumer<Object>) o -> {
            if (o instanceof RxEvent) {
                RxEvent data = (RxEvent) o;
                System.out.println("SAM: object: "+ data.getMessage());
                Snackbar snackbar = Snackbar.make(LL_root, data.getMessage(), Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(JobActivity.this, R.color.nebula));
                int snackbarTextId = com.google.android.material.R.id.snackbar_text;
                TextView textView = (TextView)sbView.findViewById(snackbarTextId);
                textView.setTextColor(getResources().getColor(R.color.navyblue));
                textView.setTextSize(25);
                snackbar.show();
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        //if you no longer need data "very important"
        disposable.dispose(); //unsubscribe
    }



    @Override
    public void onBackPressed() {
        System.out.println("SAM: obBackPressed");
        Util.stopJob(getApplicationContext(), false);
        super.onBackPressed();
    }
}
