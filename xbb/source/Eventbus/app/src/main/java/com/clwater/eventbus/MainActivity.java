package com.clwater.eventbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    private Button button ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("EventBus" , "EventBus Post in " + Thread.currentThread().getName());
                        EventBus.getDefault().post(new EventBusMessage());
                    }
                }).start();
            }
        });

    }

//    @Subscribe
//    public void onEventBusMessagePost(EventBusMessage e){
//        Toast.makeText(this , "EventBusMessage is posted ." , Toast.LENGTH_SHORT).show();
//    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEventBusMessagePostInPOSTING(EventBusMessage e){
        Log.i("EventBus" , "POSTING in " + Thread.currentThread().getName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusMessagePostInMAIN(EventBusMessage e){
        Log.i("EventBus" , "MAIN in " + Thread.currentThread().getName());
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEventBusMessagePostInBACKGROUND(EventBusMessage e){
        Log.i("EventBus" , "BACKGROUND in " + Thread.currentThread().getName());
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEventBusMessagePostInASYNC(EventBusMessage e){
        Log.i("EventBus" , "ASYNC in " + Thread.currentThread().getName());
    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
