package com.developer.rimon.zhihudaily.listener;

import android.os.Handler;
import android.os.Message;
import android.view.View;

/**
 * Created by Rimon on 2016/9/1.
 */
public class DoubleClick {

    public static void registerDoubleClickListener(View view, final OnDoubleClickListener listener) {
        if (listener == null) return;
        view.setOnClickListener(new View.OnClickListener() {
            //双击间隔时间350毫秒
            private static final int DOUBLE_CLICK_TIME = 350;
            private boolean flag = true;
            private Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    listener.OnSingleClick((View) msg.obj);
                }

            };
            //等待双击
            public void onClick(final View v) {
                if (flag) {
                    flag = false;//与执行双击事件
                    new Thread() {
                        public void run() {
                            try {
                                Thread.sleep(DOUBLE_CLICK_TIME);
                                //此时线程沉睡 而flag被修改为false  在DOUBLE_CLICK_TIME内点击则 进入else
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }    //等待双击时间，否则执行单击事件
                            if (!flag) {
                                //睡醒了看一看flag被人动过没，没有人动，则认作单击事件
                                //因此不建议用此方法执行单击事件 因为会等待睡醒，有点击延迟的存在
                                //没有人动，自己把它改成true，以接受下次点击
                                flag = true;
                                Message msg = handler.obtainMessage();
                                msg.obj = v;
                                //发个消息，让执行单击
                                handler.sendMessage(msg);
                            }
                        }
                    }.start();
                } else {
                    flag = true;
                    listener.OnDoubleClick(v);    //执行双击
                }
            }
        });
    }

    public interface OnDoubleClickListener {
        public void OnSingleClick(View v);

        public void OnDoubleClick(View v);
    }
}
