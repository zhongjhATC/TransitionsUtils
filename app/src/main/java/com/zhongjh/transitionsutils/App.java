package com.zhongjh.transitionsutils;

import android.app.Application;

import androidx.annotation.NonNull;

/**
 * @author zhongjh
 * @date 2022/2/22
 */
public class App extends Application {

    private static Application mInstance;

    /**
     * 获得当前app运行的Application
     */
    public static Application getInstance() {
        if (mInstance == null) {
            throw new NullPointerException("请调用setApplication方法或者继承于BaseApplication");
        }
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setApplication(this);
    }

    public static synchronized void setApplication(@NonNull Application application) {
        mInstance = application;
    }
}
