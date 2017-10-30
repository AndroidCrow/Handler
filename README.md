#### handler的底层如何发送和接收消息 并且了解为什么会在主线程收到消息

#### 首先来看看handler发送与接收消息的伪代码，先看发送消息 究竟里面是怎么操作的

```
  Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String str = (String) msg.obj;
            
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                message.obj = "发送";
                mHandler.sendMessage(message);
            }
        }).start();

    }
```

#### 我们来改改这段代码发送三条消息


```
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String str = (String) msg.obj;

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message1 = Message.obtain();
                message1.obj = "发送";
                mHandler.sendMessage(message1);
                Message message2 = Message.obtain();
                mHandler.sendMessageDelayed(message2,1000);
                Message message3 = Message.obtain();
                mHandler.sendMessageDelayed(message3,500);
            }
        }).start();
    }
```

#### 代码一直走进去会走到queue.enqueueMessage(msg, uptimeMillis);    queue是MessageQueue看名字就能知道是消息队列找到enqueueMessage方法，msg.target 是handler 这个是在Handler中的  enqueueMessage()赋值的     msg.target = this;没列出的代码暂时不需要关注，这段代码如果熟悉链表的人应该看的很容易



#### 先看看第一条消息进来 msg用msg1来命名


```
 boolean enqueueMessage(Message msg, long when) {
        if (msg.target == null) {
            throw new IllegalArgumentException("Message must have a target.");
        }
        
        synchronized (this) {
            //when 是传过来的时间 when里面有加上系统时间 
            msg.when = when;
            //mMessages 只是有对象 但是里面的参数都是空
            Message p = mMessages;
            boolean needWake;
            
            这里if判断 p  == 0成立
            if (p == null || when == 0 || when < p.when) {
                // New head, wake up the event queue if blocked.
                msg.next 是Message对象
                这里p == null
                msg.next = p;
                这里mMessages被赋值为msg1
                mMessages = msg;
                
            } else {
                // Inserted within the middle of the queue.  Usually we don't have to wake
                // up the event queue unless there is a barrier at the head of the queue
                // and the message is the earliest asynchronous message in the queue.
                needWake = mBlocked && p.target == null && msg.isAsynchronous();
                Message prev;
                for (;;) {
                    prev = p;
                    p = p.next;
                    if (p == null || when < p.when) {
                        break;
                    }
                    if (needWake && p.isAsynchronous()) {
                        needWake = false;
                    }
                }
                msg.next = p; // invariant: p == prev.next
                prev.next = msg;
            }

            // We can assume mPtr != 0 because mQuitting is false.
            if (needWake) {
                nativeWake(mPtr);
            }
        }
        return true;
    }
```
#### 接下来看第二条消息，msg用msg2命名


```
 boolean enqueueMessage(Message msg, long when) {
        if (msg.target == null) {
            throw new IllegalArgumentException("Message must have a target.");
        }
        
        synchronized (this) {
        
            //when上面是1000
            msg.when = when;
            //第一条消息mMessages是msg1，p = msg1
            Message p = mMessages;
            
            
            //这里if判断 p  = msg1 不成立 || when !=0 || when < p.when 这里是大于 因为when是1000 p.when是0
            if (p == null || when == 0 || when < p.when) {
                // New head, wake up the event queue if blocked.
                msg.next 是Message对象
                这里p == null
                msg.next = p;
                这里mMessages被赋值为msg1
                mMessages = msg;
                
            } else {
                
                Message prev;
                //死循环
                for (;;) {
                    //p是msg1 prev是msg1
                    prev = p;
                    //根据第一条消息得出 p.next是空 p这里变成了null
                    p = p.next;
                    //p==null成立 跳出循环
                    if (p == null || when < p.when) {
                        break;
                    }
                    if (needWake && p.isAsynchronous()) {
                        needWake = false;
                    }
                }
                这里msg2.next = null
                msg.next = p; // invariant: p == prev.next
                这里prev是msg1 ，所以msg1.next = msg2
                
                prev.next = msg;
            }

            // We can assume mPtr != 0 because mQuitting is false.
            if (needWake) {
                nativeWake(mPtr);
            }
        }
        return true;
    }
```

#### 最后看看第三条过来的消息，msg用msg3命名，最后得到这里msg3和msg2对调了位置。


```
 boolean enqueueMessage(Message msg, long when) {
        if (msg.target == null) {
            throw new IllegalArgumentException("Message must have a target.");
        }
        
        synchronized (this) {
        
            //when==500
            
            msg.when = when;
            /mMessages是msg1
            Message p = mMessages;
            
            
           //p!=null
            if (p == null || when == 0 || when < p.when) {
                // New head, wake up the event queue if blocked.
               // msg.next 是Message对象
                这里p == null
                msg.next = p;
                这里mMessages被赋值为msg1
                mMessages = msg;
                
            } else {
                
                Message prev;
               
                for (;;) {
                    //p是msg1 prev是msg1
                    prev = p;
                    //p是msg1 msg1.next是msg2 由上一个判断得出
                    p = p.next;
                    //p!=null
                    if (p == null || when < p.when) {
                        break;
                    }
                    if (needWake && p.isAsynchronous()) {
                        needWake = false;
                    }
                }
                //msg.next= msg2
                msg.next = p; // invariant: p == prev.next
                
                //msg1.next =msg3
                prev.next = msg;
            }

            // We can assume mPtr != 0 because mQuitting is false.
            if (needWake) {
                nativeWake(mPtr);
            }
        }
        return true;
    }
```
#### 这里解释一下为什么handler在子线程new出来的情况下会崩溃找到handler的构造方法,这段代码意思是looper是空，我们继续走到Looper.myLooper();方法里面

```
 mLooper = Looper.myLooper();
        if (mLooper == null) {
            throw new RuntimeException(
                "Can't create handler inside thread that has not called Looper.prepare()");
        }
```

#### sThreadLocal简单理解就是防止脏数据的出现，每个当前线程只能出现一个，sThreadLocal.get()这段话应该不能理解 意思就是拿到当前线程的looper，这样就能明白为什么不能在子线程new Handler了
```
   public static @Nullable Looper myLooper() {
        return sThreadLocal.get();
    }

```

#### 为什么主线程没有创建looper也不会报错呢，这里系统已经帮我们操作好了，找到ActivityThread ，这里main方法进来的时候就已经创建好了

```
    public static void main(String[] args) {
     
        Looper.prepareMainLooper();

      
        Looper.loop();

        throw new RuntimeException("Main thread loop unexpectedly exited");
    }
```

#### 剩下的就只需要看消息处理了 看到Looper.loop方法中

```
public static void loop() {
        final Looper me = myLooper();
        
        final MessageQueue queue = me.mQueue;


        //这是个死循环
        for (;;) {
            //这个方法里面没解释， 就是从消息队列中拿到message
            Message msg = queue.next(); // might block
            if (msg == null) {
                // No message indicates that the message queue is quitting.
                return;
            }

     

            try {
            
                //这条就是发送消息代码
                msg.target.dispatchMessage(msg);
                end = (slowDispatchThresholdMs == 0) ? 0 : SystemClock.uptimeMillis();
            } finally {
                if (traceTag != 0) {
                    Trace.traceEnd(traceTag);
                }
            }
            

            msg.recycleUnchecked();
        }
    }
```
#### 会走到handleMessage(msg);中 就可以接收到消息了
```
    public void dispatchMessage(Message msg) {
        if (msg.callback != null) {
            handleCallback(msg);
        } else {
            if (mCallback != null) {
                if (mCallback.handleMessage(msg)) {
                    return;
                }
            }
            handleMessage(msg);
        }
    }
```


#### 整个handler消息机制到现在已经分析结束 自己写的简单handler,模拟handler的流程


