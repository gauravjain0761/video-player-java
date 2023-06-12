package com.app.mvpdemo.businessframe;


import android.content.Context;


import com.app.mvpdemo.businessframe.api.BusinessLogicApiNativeImp;
import com.app.mvpdemo.businessframe.api.BusinessLogicApiRemoteImp;
import com.app.mvpdemo.businessframe.api.IBusinessLogicApi;
import com.app.mvpdemo.businessframe.base.BusinessLogicException;
import com.app.mvpdemo.businessframe.base.ICallback;
import com.app.mvpdemo.businessframe.config.BusinessFrameConfig;
import com.app.mvpdemo.util.log.LogUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * frame框架配置
 *
 * @author weiwen
 * @since 2022/9/19 10:52 AM
 */
public class BusinessLogicManager {

    private static final String TAG = "BusinessLogicManager";

    private int mThreadCount = 3;
    private BusinessLogicApiNativeImp mBusinessLogicApiNative;
    private BusinessLogicApiRemoteImp mBusinessLogicApiRemote;
    private ExecutorService mExecutorService = null;
    private static Map<String, IBusinessLogicApi> mApiMap = new HashMap<>();


    /**
     * 上下文对象
     */
    private Context mContext;

    private BusinessFrameConfig mBusinessFrameConfig;


    public int getThreadCount() {
        return mThreadCount;
    }

    public void setThreadCount(int threadCount) {
        this.mThreadCount = threadCount;
    }


    /**
     * @return
     */
    public String getAppName() {
        if (mBusinessFrameConfig != null) {
            return mBusinessFrameConfig.getAppName();
        }
        return "Microsingle";
    }

    /**
     * 在访问时创建单例
     */
    private static class SingletonHolder {

        private static final BusinessLogicManager INSTANCE = new BusinessLogicManager();

    }

    /**
     * 获取单例
     */
    public static BusinessLogicManager getInstance() {
        return SingletonHolder.INSTANCE;
    }


    /**
     * init
     *
     * @param context
     * @param threadCount
     */
    @Deprecated
    public void initial(Context context, int threadCount) {
        this.mContext = context;
        this.mThreadCount = threadCount;
        if (mExecutorService == null) {
            mExecutorService = Executors.newFixedThreadPool(mThreadCount);
        }
        initBusinessLogicApi();
    }

    /**
     * init
     *
     * @param context
     * @param threadCount
     */
    private void initBusinessFrame(Context context, int threadCount) {
        this.mContext = context;
        this.mThreadCount = threadCount;
        if (mExecutorService == null) {
            Executors.newFixedThreadPool(mThreadCount);
        }
        initBusinessLogicApi();
    }

    public void initConstants(BusinessFrameConfig configuration) {
        this.mBusinessFrameConfig = configuration;
        initBusinessFrame(configuration.getContext(), configuration.getThreadCount());
    }

    private void initBusinessLogicApi() {
        if (mBusinessLogicApiRemote == null) {
            mBusinessLogicApiRemote = new BusinessLogicApiRemoteImp(mContext);
        }
        if (mBusinessLogicApiNative == null) {
            mBusinessLogicApiNative = new BusinessLogicApiNativeImp(mContext);
        }
        if (!mBusinessLogicApiRemote.isInitializationSuccessful()) {
            mBusinessLogicApiRemote.initialize();
        }
        if (!mBusinessLogicApiNative.isInitializationSuccessful()) {
            mBusinessLogicApiNative.initialize();
        }
    }

    /**
     * get business logic api instance
     *
     * @return IBusinessLogicApi
     */
    public IBusinessLogicApi getBusinessLogicApi(boolean isRemote) {
        LogUtil.d(TAG, "getBusinessLogicApi() enter");
        if (isRemote) {
            if (mBusinessLogicApiRemote != null) {
                return mBusinessLogicApiRemote;
            } else {
                mBusinessLogicApiRemote = new BusinessLogicApiRemoteImp(mContext);
                return mBusinessLogicApiRemote;
            }
        } else {
            if (mBusinessLogicApiNative != null) {
                return mBusinessLogicApiNative;
            } else {
                mBusinessLogicApiNative = new BusinessLogicApiNativeImp(mContext);
                return mBusinessLogicApiNative;
            }
        }
    }

    /**
     * get business logic api instance
     *
     * @param context
     * @param remoteServicePackage 跨进程 包名
     * @return IBusinessLogicApi
     */
    public IBusinessLogicApi getBusinessLogicApi(Context context, final String remoteServicePackage) {
        String paName = null == remoteServicePackage ? context.getPackageName() : remoteServicePackage;
        synchronized (mApiMap) {
            IBusinessLogicApi api = mApiMap.get(paName);
            if (null == api) {
                if (null == remoteServicePackage) {
                    api = new BusinessLogicApiNativeImp(context.getApplicationContext());
                } else {
                    api = new BusinessLogicApiRemoteImp(context.getApplicationContext(), remoteServicePackage);
                }
                mApiMap.put(paName, api);
            }
            return api;
        }
    }


    public void asyncExecute(Runnable runnable) throws BusinessLogicException {
        LogUtil.d(TAG, "asyncExecute() : ");
        if (null == mExecutorService) {
            throw new BusinessLogicException(ICallback.ResultCode.PLAT_SERVICE_EXECUTOR_IS_NULL,
                    ICallback.ResultCode.getResultMessage(ICallback.ResultCode.PLAT_SERVICE_EXECUTOR_IS_NULL));
        }
        mExecutorService.execute(runnable);
    }


}
