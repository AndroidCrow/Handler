package com.chao.handler;

import android.provider.FontRequest;

import java.util.Queue;

/**
 * Created by yang2 on 2017/10/30.
 */

public class Looper {
    private static ThreadLocal<Looper> sThreadLocal = new ThreadLocal<>();
    MessageQueue mQueue;

    public static void prepareMainLooper() {

    }

    public Looper(){
        mQueue = new MessageQueue();
    }

    public static void prepare() {
        sThreadLocal.set(new Looper());
    }

    public static void loop() {
        Looper looper = myLooper();
        for (; ; ) {
            MessageQueue queue = looper.mQueue;

            Message message = queue.next();


            if (message == null) {
                return;
            }
            message.target.handleMessage(message);
        }
    }

    public static Looper myLooper() {
        return sThreadLocal.get();


    }
}
