package com.app.mvpdemo.util;

import android.app.Activity;


import com.app.mvpdemo.util.log.LogUtil;

import java.util.Stack;

/**
 * activity堆栈式管理
 *
 * @author weiwen
 */
public class AppManager {

    private static final String TAG = "AppManager";

    private static Stack<Activity> mActivityStack;

    private AppManager() {
        if (mActivityStack == null) {
            mActivityStack = new Stack<Activity>();
        }
    }

    /**
     * 在访问时创建单例
     */
    private static class SingletonHolder {
        private static final AppManager INSTANCE = new AppManager();
    }

    public static AppManager getInstance() {
        return SingletonHolder.INSTANCE;
    }


    /**
     * 获取指定的Activity
     */
    public static Activity getActivity(Class<?> cls) {
        if (mActivityStack != null)
            for (Activity activity : mActivityStack) {
                if (activity.getClass().equals(cls)) {
                    return activity;
                }
            }
        return null;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (mActivityStack != null) {
            mActivityStack.add(activity);
        }
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        if (mActivityStack != null) {
            return mActivityStack.lastElement();
        } else {
            return null;
        }
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        if (mActivityStack != null) {
            Activity activity = mActivityStack.lastElement();
            finishActivity(activity);
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null && mActivityStack != null && mActivityStack.contains(activity)) {
            mActivityStack.remove(activity);
            LogUtil.i(TAG, "remove activity==", activity);
            if (!activity.isFinishing() && !activity.isDestroyed()) {
                LogUtil.i(TAG, "activity.finish()");
                activity.finish();
            }
        }
    }

    /**
     * 结束指定的Activity
     */
    public void removeActivity(Activity activity) {
        if (activity != null && mActivityStack != null) {
            mActivityStack.remove(activity);
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        if (mActivityStack != null) {
            for (Activity activity : mActivityStack) {
                if (activity.getClass().equals(cls)) {
                    finishActivity(activity);
                    break;
                }
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        if (mActivityStack != null && mActivityStack.size() > 0) {
            for (int i = mActivityStack.size()-1; i >= 0; i--) {
                Activity activity = mActivityStack.get(i);
                if (null != activity) {
                    finishActivity(activity);
                }
            }
            mActivityStack.clear();
        }
    }

    /**
     * 退出应用程序
     */
    public void AppExit() {
        try {
            finishAllActivity();
            // System.exit(0);
        } catch (Exception e) {
        }
    }
}
