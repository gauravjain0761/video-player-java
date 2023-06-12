package com.app.mvpdemo.businessframe.mvp.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import com.app.mvpdemo.businessframe.base.BusinessHandler;
import com.app.mvpdemo.businessframe.base.IBaseContract;
import com.app.mvpdemo.businessframe.base.IBasePresenterView;
import com.app.mvpdemo.businessframe.mvp.activity.BaseActivity;
import com.app.mvpdemo.businessframe.mvp.fragment.BaseFragment;
import com.app.mvpdemo.util.log.LogUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by weiwen.
 */

abstract public class BasePresenter<T extends IBasePresenterView> implements IBaseContract.IPresenter {

    private final static String TAG = "BasePresenter";
    private Context mContext;
    private Handler mMainHandler, mAsyncHandler;
    private HandlerThread mHandlerThread;
    private T mView;
    private Map<Integer, Object> mCache = new HashMap<>();
    private boolean mDestroyed = false;

    public BasePresenter(Context context, T view) {
        LogUtil.d(TAG, "BasePresenter");
        this.mContext = context;
        mView = view;
        mMainHandler = new BusinessHandler(context);
        mHandlerThread = new HandlerThread(this.getClass().getName());
        mHandlerThread.start();
        mAsyncHandler = new Handler(mHandlerThread.getLooper());
    }

    @Override
    public boolean cacheDataOnWillInvisible(int type, Object obj) {
        LogUtil.d(TAG, "cacheDataOnWillInvisible(), obj : " + obj + ",  " + this.hashCode());
        if (!(mView instanceof IBaseContract.IView)) {
            return false;
        }
        if (((IBaseContract.IView) mView).isPauseRefresh() && !mDestroyed) {
            mCache.put(type, obj);
            return true;
        }
        return false;
    }

    @Override
    public void cancelProcessCacheDataOnInvisible() {
        mMainHandler.removeCallbacks(mResumeProcess);
    }

    @Override
    public void resumeProcessCacheDataOnVisible() {
        LogUtil.d(TAG, "resumeProcessCacheDataOnVisible(), mCache.size() : " + mCache.size() + ",  " + this.hashCode());
        if (mCache.size() == 0) {
            return;
        }
        mMainHandler.removeCallbacks(mResumeProcess);
        mMainHandler.postDelayed(mResumeProcess, 500);
    }

    private Runnable mResumeProcess = new Runnable() {
        @Override
        public void run() {
            resumeProcessOnVisible(mCache);
            mCache.clear();
        }
    };

    public Handler getAsyncHandler() {
        return mAsyncHandler;
    }

    public Context getContext() {
        return mContext;
    }

    public Handler getMainHandler() {
        return mMainHandler;
    }

    public T getPresenterView() {
        return mView;
    }
    /**
     * show loading dialog
     *
     * @param title loading title
     */
    public void showLoading(String title) {
        if (getPresenterView() instanceof BaseActivity) {
            BaseActivity baseActivity = ((BaseActivity) getPresenterView());
            baseActivity.showLoading(title);
        } else if (getPresenterView() instanceof BaseFragment) {
            BaseFragment baseFragment = ((BaseFragment) getPresenterView());
            baseFragment.showLoading(title);
        }
    }

    /**
     * dismiss loading dialog
     */
    public void dismissLoading() {
        T presenterView = getPresenterView();
        if (presenterView instanceof BaseActivity) {
            BaseActivity baseActivity = ((BaseActivity) presenterView);
            baseActivity.dismissLoading();
        } else if (getPresenterView() instanceof BaseFragment) {
            BaseFragment baseFragment = ((BaseFragment) getPresenterView());
            baseFragment.dismissLoading();
        }
    }



    /**
     *
     */
    public void destroy() {
        LogUtil.d(TAG, "destroy(), this : ", this);
        if (mMainHandler != null) {
            mMainHandler.removeCallbacksAndMessages(null);
            mMainHandler = null;
        }
        if (mAsyncHandler != null) {
            mAsyncHandler.removeCallbacksAndMessages(null);
            mAsyncHandler.getLooper().quit();
            mAsyncHandler = null;
        }
        mCache.clear();
        mDestroyed = true;
    }

    /**
     * get the object unique id
     *
     * @return
     */
    public String getId() {
        return this.getClass().getName() + String.valueOf(this.hashCode());
    }

    /**
     * resume process on visible
     *
     * @param map
     */
    public void resumeProcessOnVisible(Map<Integer, Object> map) {

    }

    /**
     * if the presenter is destroyed
     *
     * @return
     */
    public boolean ismDestroyed() {
        return mDestroyed;
    }

    public void runRunnableInThread(Runnable runnable) {
        if (getAsyncHandler() != null) {
            getAsyncHandler().post(runnable);
        }
    }

    public void runRunnableInThreadDelay(Runnable runnable, int delayTime) {
        if (getAsyncHandler() != null) {
            getAsyncHandler().postDelayed(runnable, delayTime);
        }
    }

    public void runRunnableInMain(Runnable runnable) {
        if (getMainHandler() != null) {
            getMainHandler().post(runnable);
        }
    }

    public void runRunnableInMainDelay(Runnable runnable, int delayTime) {
        if (getMainHandler() != null) {
            getMainHandler().postDelayed(runnable, delayTime);
        }
    }
}
