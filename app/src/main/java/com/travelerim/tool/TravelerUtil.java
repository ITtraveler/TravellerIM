package com.travelerim.tool;

import android.content.Context;
import android.content.SharedPreferences;

import com.travelerim.data.User;

import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2016/3/16.
 */
public class TravelerUtil {
    private static SharedPreferences sp;

    /**
     * 用于记住用户名，密码
     *
     * @param context
     */
    public static void saveUser(Context context, User user) {
        sp = context.getSharedPreferences("PASSWORDFILE", Context.MODE_PRIVATE);
        //将数据保存在passwordfile文件中
        sp.edit().putString("username", user.getUsername()).commit();
        sp.edit().putString("password", user.getPassword()).commit();
    }

    public static User getUser(Context context) {
        User user = new User();
        sp = context.getSharedPreferences("PASSWORDFILE", Context.MODE_PRIVATE);
        String username = sp.getString("username", "");
        String psw = sp.getString("password", "");
        user.setUsername(username);
        user.setPassword(psw);
        return user;
    }

    public static void setFirstUse(Context context, boolean bool) {
        sp = context.getSharedPreferences("ISFIRSTUSE", Context.MODE_PRIVATE);
        sp.edit().putBoolean("firstUse", bool).commit();
    }

    public static boolean isFirstUse(Context context) {
        sp = context.getSharedPreferences("ISFIRSTUSE", Context.MODE_PRIVATE);
        boolean isFirst = sp.getBoolean("firstUse", true);
        return isFirst;
    }

    public static String getTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String curTime = format.format(System.currentTimeMillis());
        return curTime;
    }

}
