package com.clwater.rxjava;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.d("gzb", "Observable thread is : " + Thread.currentThread().getName());
                Log.d("gzb", "emit 1");
                emitter.onNext(1);
            }
        });

//        Consumer<Integer> consumer = new Consumer<Integer>() {
//            @Override
//            public void accept(Integer integer) throws Exception {
//                Log.d("gzb", "Observer thread is :" + Thread.currentThread().getName());
//                Log.d("gzb", "onNext: " + integer);
//            }
//        };
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);

    }
}
