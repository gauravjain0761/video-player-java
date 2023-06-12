package com.app.mvpdemo.businessframe.base;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import java.lang.ref.WeakReference;

/**
 * Created by weiwen on 2018/4/24.
 */

public class BusinessHandler extends Handler {

    private WeakReference<Context> mContext;

    public BusinessHandler(Context context) {
        this.mContext = new WeakReference<Context>(context);
    }

    public WeakReference<Context> getmContext() {
        return mContext;
    }

    /**
     * if the handler is invalid
     * @return
     */
    public boolean isInvalid() {
        Context context = mContext.get();
        if (null == context) {
            return true;
        }
        if (context instanceof Activity) {
            return ((Activity) context).isFinishing();
        }
        return false;
    }
}
