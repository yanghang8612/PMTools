package com.casc.pmtools;

import android.app.Application;

import java.util.concurrent.Executors;

public class MyApplication extends Application {

    private static final String TAG = MyApplication.class.getSimpleName();

    private static MyApplication mInstance;

    public static MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 程序崩溃捕捉并打印响应信息
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());

        // 初始化相关字段
        mInstance = this;
        //ZXingLibrary.initDisplayOpinion(this);
        MyVars.executor = Executors.newScheduledThreadPool(15);
    }
}
