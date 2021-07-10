package com.xhkj.facedemo;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.orhanobut.hawk.Hawk;
import com.xhkj.facedemo.db.HawkSave;
import com.xhkj.facedemo.module.NameBean;

import java.util.ArrayList;
import java.util.List;

public class MyApp extends Application {

    private static Context mContext;

    public static List<NameBean> list;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        Hawk.init(this).build();
        // 先判断本地是否有值，若有就取本地的
        if (HawkSave.getInstance().getUserInfo() != null) {
            list = HawkSave.getInstance().getUserInfo();
        } else {
            list = new ArrayList<>();
        }

    }

    public static Context getContext() {
        return mContext;
    }


}
