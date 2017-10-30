package com.chao.handler;

import android.os.SystemClock;
import android.view.View;

/**
 * Created by yang2 on 2017/10/30.
 */

public class MainActivity extends Activity {

    private TextView mTextView;


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            mTextView.setText((String) msg.obj);
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();

        mTextView  = findViewById(Rid.id.text_view);

        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message message = new Message();

                message.obj = "数据";
                mHandler.setMessage(message);
            }
        }.start();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public TextView findViewById(int id){
        return new TextView();
    }

}
