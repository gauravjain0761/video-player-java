package com.app.mvpdemo.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.PowerManager;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 设备相关方法类
 *
 * @author weiwen
 * @created 2022/1/17 2:44 PM
 */
public class DeviceUtils {

    private final static String PACKAGE_NAME = "com.google.android.gms.measurement.prefs";
    private final static String FIRST_OPEN_TIME = "first_open_time";

    /**
     * 获取系统属性
     *
     * @param key
     * @return
     */
    public static String getProp(String key) {
        try {
            Method me = Class.forName("android.os.SystemProperties").getMethod("get", String.class);
            return (String) me.invoke(null, key);
        } catch (Exception e) {

        }
        return "";
    }

    public static String getCurrentAppVersionName(Context context) {
        if (context == null) {
            return "";
        }
        return getVersionName(context, context.getPackageName());
    }

    public static int getVersion(Context context, String packageName) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(packageName, 0);
            int version = info.versionCode;
            return version;
        } catch (Exception var4) {
            var4.printStackTrace();
            return 0;
        }
    }

    public static String getVersionName(Context context, String packageName) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(packageName, 0);
            String version = info.versionName;
            return version;
        } catch (Exception var4) {
            var4.printStackTrace();
            return "";
        }
    }

    /**
     * 判断应用是否在后台
     *
     * @return
     */
    public static boolean isAppRunInBackground(Context context) {
        if (context == null) {
            return true;
        }
        try {
            ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
            if (rti != null && rti.size() > 0) {
                ActivityManager.RunningTaskInfo info = rti.get(0);
                if (info != null && info.topActivity != null) {
                    String packageName = info.topActivity.getPackageName();
                    if (packageName.equals(context.getApplicationInfo().packageName)) {
                        return false;
                    }
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return true;
    }


    /**
     * 获取当前最上层Activity名称
     *
     * @param context
     * @return
     */
    public static String getTopActivityName(Context context) {
        if (context == null) {
            return "";
        }
        try {
            ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
            if (rti != null && rti.size() > 0) {
                ActivityManager.RunningTaskInfo info = rti.get(0);
                return info.topActivity.getClassName();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 弹出软键盘
     *
     * @param context
     */
    public static void showSoftInput(Context context) {
        if (context != null) {
            try {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取屏幕是否亮着
     *
     * @param context
     * @return
     */
    public static boolean isScreenOn(Context context) {
        if (context == null) {
            return true;
        }
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return powerManager.isInteractive();
    }

    /**
     * 获取app首次打开时间
     *
     * @param context
     * @return
     */
    public static long getAppFirstOpenTime(Context context) {
        if (context == null) {
            return System.currentTimeMillis();
        }
        try {

            SharedPreferences sharedPreferences = context.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getLong(FIRST_OPEN_TIME, System.currentTimeMillis());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return System.currentTimeMillis();
    }
    /**
     * 获取app安装时间
     *
     * @param context
     * @return
     */
    public static long getAppInstallTime(Context context) {
        if (context == null) {
            return 0;
        }
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.firstInstallTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
