package com.xhkj.facedemo.db;

import android.content.Context;

import com.xhkj.facedemo.module.NameBean;
import com.orhanobut.hawk.Hawk;

import java.util.List;


/**
 * Created by Administrator on 2018/8/15.
 */

public class HawkSave {

    // 用户信息
    private static final String KEY_INDEX_NAME= "user_info";

    private Context context;
    // 持有私有静态类实例，防止被引用，此处赋值null的目的实现延迟加载
    private volatile static HawkSave instance = null;
    // 私有构造方法，防止被实例化
    private HawkSave(){}
    // 静态工程方法，创建实例(双重校验锁)
    public static HawkSave getInstance() {
        if(instance == null){
            synchronized (HawkSave.class){
                if(instance == null){
                    instance = new HawkSave();
                }
            }
        }
        return instance;
    }

    /*
    * 存储索引index和照片名
    * */
    public void saveUserInfo(List<NameBean> info){
        Hawk.put(KEY_INDEX_NAME, info);
    }

    /*
    * 存储照片信息
    * */
    public List<NameBean> getUserInfo(){
        if(Hawk.get(KEY_INDEX_NAME) == null) return null;
        return Hawk.get(KEY_INDEX_NAME);
    }

}
