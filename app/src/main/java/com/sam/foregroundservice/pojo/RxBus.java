package com.sam.foregroundservice.pojo;

import androidx.annotation.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;

// https://medium.com/@bishoy_abd/sending-data-between-different-parts-of-your-app-using-rxjava2-5675153e37c
public class RxBus {
    public RxBus() {
    }

    //this how to create our bus
    private static final BehaviorSubject<Object> behaviorSubject = BehaviorSubject.create();

    public static Disposable subscribe(@NonNull Consumer<Object> action) {
        return behaviorSubject.subscribe(action);
    }

    //use this method to send data
    public static void publish(@NonNull Object message) {
        behaviorSubject.onNext(message);
    }
}
