package com.chao.handler;

/**
 * Created by yang2 on 2017/10/30.
 */

public class ActivityThread {
    final H mH = new H();
    private Message mMessage;

    public void attach(boolean b) {

        MainActivity mainActivity = new MainActivity();

        mainActivity.onCreate();

        Message message = new Message();

        message.obj = mainActivity;






    }

    private class H extends Handler{

        public void handleMessage(Message msg){

        }

        public void setMessage(Message message) {
            Activity mainActivity  = (Activity) message.obj;
            mainActivity.onResume();

        }
    }
}
