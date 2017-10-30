package com.chao.handler;

/**
 * Created by yang2 on 2017/10/30.
 */

public class Message {

    public Object obj;

    public Handler target;
    public long when;
    public Message next;
}
