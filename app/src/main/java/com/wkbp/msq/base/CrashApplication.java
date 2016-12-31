package com.wkbp.msq.base;

import android.app.Application;
import android.content.Context;

import com.wkbp.msq.util.ImageLoaderUtils;
import com.wkbp.msq.util.SharedPreferenceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shangshuaibo on 2016/11/17 10:09
 */
public class CrashApplication extends Application {
    public static List<BaseActivity> allActivity = new ArrayList<>();
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        // 初始化SharedPreferences
        SharedPreferenceUtils.getInstance().init(context);
        // 初始化ImageLoader
        ImageLoaderUtils.init(context);

//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(context);
    }

}
