package com.app.mvpdemo.businessframe.api;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;

import com.app.mvpdemo.businessframe.base.BusinessLogicException;
import com.app.mvpdemo.businessframe.base.ICallback;
import com.app.mvpdemo.businessframe.base.IUiRequest;
import com.app.mvpdemo.businessframe.base.ListenServiceConnectCallback;
import com.app.mvpdemo.util.log.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 类功能描述
 *
 * @author weiwen
 * @since 2022/9/19 10:59 AM
 */
public class BusinessLogicApiNativeImp implements IBusinessLogicApi {

    private static final String TAG = "BusinessLogicApiNativeImp";
    private static final String ACTION = "com.microsingle.lib.business.api.plat.native.service.action";
    private Context mContext;
    private INativeBindApi mBinder;
    private List<ICallback> mListenServiceCallbacks = new ArrayList<>();
    private Handler mCallbackHandler = null;
    private HandlerThread mCallbackHandlerThread = null;

    public BusinessLogicApiNativeImp(Context context) {
        mContext = context;
        LogUtil.d(TAG, "BusinessLogicApiNativeImp()   mContext : ", mContext);
    }

    private void startHandlerThread() {
        LogUtil.i(TAG, "startHandlerThread() mCallbackHandlerThread : ", mCallbackHandlerThread);
        if (null == mCallbackHandlerThread) {
            mCallbackHandlerThread = new HandlerThread(TAG + "Callback Thread");
            mCallbackHandlerThread.start();
            mCallbackHandler = new Handler(mCallbackHandlerThread.getLooper());
        }
    }

    /**
     * initialize bind service
     */
    private void bindService() {
        LogUtil.i(TAG, "bindService");
        String packageName = mContext.getApplicationContext().getPackageName();
        //String packageName = "com.ju.plat.businessframe.api";

        Intent intent = new Intent();
        intent.setAction(ACTION);
        intent.setPackage(packageName);
        LogUtil.d(TAG, "packageName : ", packageName);
        mContext.bindService(intent, mConnect, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnect = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            // TODO Auto-generated method stub
            LogUtil.i(TAG, "onServiceConnected, iBinder : ", iBinder);
            if (iBinder instanceof INativeBindApi) {
                mBinder = (INativeBindApi) iBinder;
                for (ICallback callback : getListenServiceCallbacks()) {
                    callback.onSuccess(true, null);
                }
                clearListenServiceCallbacks();
            } else {
                LogUtil.d(TAG, "onServiceConnected: mBinder is inValid:" + iBinder);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            LogUtil.i(TAG, "onServiceDisconnected");
            mBinder = null;
        }
    };

    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "destroy()");

        if (null == mBinder) {
            return;
        }

        mContext.unbindService(mConnect);
        mConnect = null;
        mBinder = null;
    }

    @Override
    public void observeInitializationSuccessful(ICallback callback) {
        if (null == callback) {
            return;
        }

        if (isInitializationSuccessful()) {
            callback.onSuccess(true, null);
            return;
        }

        getListenServiceCallbacks().add(callback);
    }

    @Override
    public void initialize() {
        LogUtil.i(TAG, "initialize()");
        LogUtil.d("performanceTAG", "BusinessLogicApiNativeImp onCreate");
        bindService();
        startHandlerThread();
    }

    @Override
    public boolean isInitializationSuccessful() {
        return null != mBinder;
    }


    @Override
    public void get(final  String componentName, final IUiRequest request)
            throws BusinessLogicException {
        LogUtil.d(TAG, "get : ", componentName, ",", request.toString());
        //checkParameter(componentName, request, true, false);
        try {
            request.createConversionCallback(false);
            request.setDefaultHandler(mCallbackHandler);
            checkBinder().get(componentName, request);
        } catch (BusinessLogicException e) {
            getListenServiceCallbacks().add(new ListenServiceConnectCallback(new Runnable() {
                @Override
                public void run() {
                    try {
                        LogUtil.d(TAG, "after connect service get again : ", componentName, ",", request.toString());
                        checkBinder().get(componentName, request);
                    } catch (BusinessLogicException e) {
                        e.printStackTrace();
                    }
                }
            }));
            bindService();
        }
    }

    @Override
    public void set(final  String componentName, final IUiRequest request)
            throws BusinessLogicException {
        LogUtil.d(TAG, "set : ", componentName, ",", request.toString());
        //checkParameter(componentName, request, false, false);
        try {
            request.createConversionCallback(false);
            request.setDefaultHandler(mCallbackHandler);
            checkBinder().set(componentName, request);
        } catch (BusinessLogicException e) {
            getListenServiceCallbacks().add(new ListenServiceConnectCallback(new Runnable() {
                @Override
                public void run() {
                    try {
                        LogUtil.d(TAG, "after connect service set again : ", componentName, ",", request.toString());
                        checkBinder().set(componentName, request);
                    } catch (BusinessLogicException e) {
                        e.printStackTrace();
                    }
                }
            }));
            bindService();
        }
    }

    @Override
    public void registerListener(final  String componentName, final  IUiRequest request)
            throws BusinessLogicException {
        LogUtil.d(TAG, "registerListener : ", componentName, ",", request.toString());
        //checkParameter(componentName, request, true, false);
        try {
            request.createConversionCallback(false);
            request.setDefaultHandler(mCallbackHandler);
            checkBinder().registerListener(componentName, request);
        } catch (BusinessLogicException e) {
            getListenServiceCallbacks().add(new ListenServiceConnectCallback(new Runnable() {
                @Override
                public void run() {
                    try {
                        LogUtil.d(TAG, "after connect service registerListener again : ", componentName, ",", request.toString());
                        checkBinder().registerListener(componentName, request);
                    } catch (BusinessLogicException e) {
                        e.printStackTrace();
                    }
                }
            }));
            bindService();
        }
    }

    @Override
    public void unregisterListener(final  String componentName, final  IUiRequest request)
            throws BusinessLogicException {
        LogUtil.d(TAG, "unregisterListener : ", componentName, ",", request.toString());
        //checkParameter(componentName, request, false, false);
        try {
            request.createConversionCallback(false);
            request.setDefaultHandler(mCallbackHandler);
            checkBinder().unregisterListener(componentName, request);
        } catch (BusinessLogicException e) {
            getListenServiceCallbacks().add(new ListenServiceConnectCallback(new Runnable() {
                @Override
                public void run() {
                    try {
                        LogUtil.d(TAG, "after connect service unregisterListener again : ", componentName, ",", request.toString());
                        checkBinder().unregisterListener(componentName, request);
                    } catch (BusinessLogicException e) {
                        e.printStackTrace();
                    }
                }
            }));
            bindService();
        }
    }

    @Override
    public Object syncGet(final  String componentName, final  IUiRequest request)
            throws BusinessLogicException {
        LogUtil.d(TAG, "syncGet : ", componentName, ",", request.toString());
        //checkParameter(componentName, request, false, true);
        Object ret = null;
        try {
            ret = checkBinder().syncGet(componentName, request);
        } catch (BusinessLogicException e) {
            bindService();
            throw new BusinessLogicException(ICallback.ResultCode.PLAT_SERVICE_IS_NULL,
                    ICallback.ResultCode.getResultMessage(ICallback.ResultCode.PLAT_SERVICE_IS_NULL));
        }
        return ret;
    }

    @Override
    public Object syncSet(final  String componentName, final  IUiRequest request)
            throws BusinessLogicException {
        LogUtil.d(TAG, "syncSet : ", componentName, ",", request.toString());
        //checkParameter(componentName, request, false, true);
        Object ret = null;
        try {
            ret = checkBinder().syncSet(componentName, request);
        } catch (BusinessLogicException e) {
            bindService();
            throw new BusinessLogicException(ICallback.ResultCode.PLAT_SERVICE_IS_NULL,
                    ICallback.ResultCode.getResultMessage(ICallback.ResultCode.PLAT_SERVICE_IS_NULL));
        }
        return ret;
    }

    @Override
    public void unBindBusinessLogic(final  String componentName) throws BusinessLogicException {
        LogUtil.d(TAG, "unBindBusinessLogic(), ", componentName);
        if (null == componentName) {
            throw new BusinessLogicException(ICallback.ResultCode.COMPONENT_NAME_IS_NULL,
                    ICallback.ResultCode.getResultMessage(ICallback.ResultCode.COMPONENT_NAME_IS_NULL));
        }

        checkBinder().unBindBusinessLogic(componentName);
    }

    private INativeBindApi checkBinder() throws BusinessLogicException {
        if (null == mBinder) {
            LogUtil.e(TAG, "checkBinder() mService is null ");
            throw new BusinessLogicException(ICallback.ResultCode.PLAT_SERVICE_IS_NULL,
                    ICallback.ResultCode.getResultMessage(ICallback.ResultCode.PLAT_SERVICE_IS_NULL));
        } else {
            LogUtil.i(TAG, "checkBinder  mBinder = ", mBinder);
        }

        return mBinder;
    }

    private synchronized List<ICallback> getListenServiceCallbacks() {
        return mListenServiceCallbacks;
    }

    private synchronized void clearListenServiceCallbacks() {
        mListenServiceCallbacks.clear();
    }
}
