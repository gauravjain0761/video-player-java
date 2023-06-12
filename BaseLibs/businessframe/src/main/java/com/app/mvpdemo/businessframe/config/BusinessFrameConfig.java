package com.app.mvpdemo.businessframe.config;

import android.content.Context;

public class BusinessFrameConfig {
    private static final String TAG = "BusinessFrameConfig";

    private int mThreadCount = 3;

    /**
     * 上下文对象
     */
    private Context mContext;

    /**
     *app名称
     */
    private String mAppName;

    /**
     * 是否测试
     */
    private boolean isDebug;

    private BusinessFrameConfig(Builder builder) {
        mAppName = builder.appName;
        mContext = builder.context;
        isDebug = builder.isDebug;
        mThreadCount = builder.threadCount;
    }


    public Context getContext() {
        return mContext;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public String getAppName() {
        return mAppName;
    }

    public int getThreadCount() {
        return mThreadCount;
    }


    public static class Builder {
        private Context context;
        private boolean isDebug;
        private String appName;
        private int threadCount;

        public Builder() {
        }

        public Builder context(Context context) {
            this.context = context;
            return this;
        }

        public Builder isDebug(boolean isDebug) {
            this.isDebug = isDebug;
            return this;
        }

        public Builder appName(String appName) {
            this.appName = appName;
            return this;
        }
        public Builder threadCount(int threadCount) {
            this.threadCount = threadCount;
            return this;
        }


        public BusinessFrameConfig build(Context context) {
            return new BusinessFrameConfig(this);
        }
    }
}
