package com.chao.handler;

import android.os.SystemClock;
import android.util.Log;

/**
 * Created by yang2 on 2017/10/30.
 */

public class Handler {

     MessageQueue mQueue;

    public Handler(){
        Looper looper = Looper.myLooper();

        if (looper == null) {
            throw new RuntimeException(
                    "Can't create handler inside thread that has not called Looper.prepare()");
        }

        mQueue = looper.mQueue;

    }

    public void handleMessage(Message msg){

    }

    public void setMessage(Message message) {


    }


    public final boolean sendMessageDelayed(Message msg, long delayMillis)
    {
        if (delayMillis < 0) {
            delayMillis = 0;
        }
        return sendMessageAtTime(msg, SystemClock.uptimeMillis() + delayMillis);
    }

    public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
        MessageQueue queue = mQueue;

        return enqueueMessage(queue, msg, uptimeMillis);
    }

    private boolean enqueueMessage(MessageQueue queue, Message msg, long uptimeMillis) {
        msg.target = this;

        return queue.enqueueMessage(msg, uptimeMillis);

    }
}
