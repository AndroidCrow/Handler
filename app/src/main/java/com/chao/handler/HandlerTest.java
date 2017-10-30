package com.chao.handler;

import android.os.*;
import android.util.Log;
import android.util.LogPrinter;

import java.lang.reflect.Array;

/**
 * Created by yang2 on 2017/10/30.
 */

public class HandlerTest {
    public static void main(String[] args){
        Looper.prepare();

        ActivityThread thread = new ActivityThread();
        thread.attach(false);



        // End of event ActivityThreadMain.
        Looper.loop();
    }

}
