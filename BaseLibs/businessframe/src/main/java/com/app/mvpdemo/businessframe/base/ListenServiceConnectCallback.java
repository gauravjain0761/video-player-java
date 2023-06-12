package com.app.mvpdemo.businessframe.base;


import com.app.mvpdemo.util.log.LogUtil;

/**
 * 类功能描述
 *
 * @author weiwen
 * @since 2022/9/19 12:40 PM
 */
public class ListenServiceConnectCallback implements ICallback<Boolean> {
    private static final String TAG = "ListenServiceConnectCallback";

    private Runnable mRunnable;

    public ListenServiceConnectCallback(Runnable mRunnable) {
        this.mRunnable = mRunnable;
    }

    @Override
    public void onSuccess(Boolean data, IRequest request) {
        LogUtil.d(TAG, "onSuccess()");
        if (null != mRunnable) {
            mRunnable.run();
        }
    }

    @Override
    public void onFailure(int errorCode, String errorMsg, IRequest request) {

    }

    @Override
    public void onProgress(Object data, IRequest request) {

    }
}
