package com.wkbp.msq.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.wkbp.msq.bean.UserInfoBean;

/**
 * Created by Administrator on 2016/11/17 10:09
 */
public class SharedPreferenceUtils {

    private static SharedPreferenceUtils instance;
    private static Editor editor;
    private static SharedPreferences sp;

    public static SharedPreferenceUtils getInstance() {
        if (instance == null) {
            instance = new SharedPreferenceUtils();
        }
        return instance;
    }

    public void init(Context context) {
        sp = context.getSharedPreferences("msq", Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public static void saveCurrentUserInfo(Context context, UserInfoBean userInfo) {
        editor.putBoolean("isLogin", userInfo.isLogin());
        editor.putString("userIcon", userInfo.getUserIcon());
        editor.putString("userName", userInfo.getUserName());
        editor.putString("nickName", userInfo.getNickName());
        editor.putString("score", userInfo.getScore());
        editor.putInt("ticketCount", userInfo.getTicketCount());
        editor.commit();
    }

    public static UserInfoBean getCurrentUserInfo(Context context) {
        UserInfoBean userInfo = new UserInfoBean();
        userInfo.setLogin(sp.getBoolean("isLogin", false));
        userInfo.setUserName(sp.getString("userName", ""));
        userInfo.setUserIcon(sp.getString("userIcon", ""));
        userInfo.setNickName(sp.getString("nickName", "玛沙琪会员"));
        userInfo.setScore(sp.getString("score", "0"));
        userInfo.setTicketCount(sp.getInt("ticketCount", 0));
        return userInfo;
    }
}
