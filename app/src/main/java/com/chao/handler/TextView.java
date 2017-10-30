package com.chao.handler;

/**
 * Created by yang2 on 2017/10/30.
 */

public class TextView {
    private Thread mThread;

    public TextView() {
        mThread = Thread.currentThread();
    }

    public void setText(CharSequence text){
        checkThread();
        System.out.println("更新UI成功"+text);
    }


    void checkThread() {
        if (mThread != Thread.currentThread()) {
            throw new RuntimeException(
                    "Only the original thread that created a view hierarchy can touch its views.");
        }
    }

}
