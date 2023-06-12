package com.app.mvpdemo.businessframe.api;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;


import com.app.mvpdemo.businessframe.base.BusinessDataContainerManager;
import com.app.mvpdemo.businessframe.base.BusinessLogicException;
import com.app.mvpdemo.businessframe.base.ICallback;
import com.app.mvpdemo.businessframe.base.IUiRequest;
import com.app.mvpdemo.businessframe.base.ListenServiceConnectCallback;
import com.app.mvpdemo.util.log.LogUtil;
import com.microsingle.plat.businessframe.api.IBusinessLogicApiAidl;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 跨进程api
 *
 * @author weiwen
 * @since 2022/9/30 11:12 上午
 */
public class BusinessLogicApiRemoteImp implements IBusinessLogicApi {

    private static final String TAG = "BusinessLogicApiRemoteImp";
    private static final String ACTION = "com.microsingle.lib.business.api.plat.remote.service.action";
    private Context mContext;
    private IBusinessLogicApiAidl mBinder;
    private List<ICallback> mListenServiceCallbacks = new ArrayList<>();
    private Map<String, WeakReference<ICallback>> mUiRequestCallbackRecord = new HashMap<>();
    private String mRemoteServicePackageName = null;
    private Handler mCallbackHandler = null;
    private HandlerThread mCallbackHandlerThread = null;

    public BusinessLogicApiRemoteImp(Context context) {
        mContext = context;
        LogUtil.d(TAG, "BusinessLogicApiNativeImp()   mContext : ", mContext);
    }

    public BusinessLogicApiRemoteImp(Context mContext, String mRemoteServicePackageName) {
        this.mContext = mContext;
        this.mRemoteServicePackageName = mRemoteServicePackageName;
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
        Intent intent = new Intent();
        intent.setAction(ACTION);
        String packageName = null == mRemoteServicePackageName ? mContext.getApplicationContext().getPackageName()
                : mRemoteServicePackageName;
        intent.setPackage(packageName);
        LogUtil.d(TAG, "packageName : ", packageName);
        mContext.bindService(intent, mConnect, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnect = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            // TODO Auto-generated method stub
            LogUtil.i(TAG, "onServiceConnected, iBinder : ", iBinder);
            mBinder = IBusinessLogicApiAidl.Stub.asInterface(iBinder);
            for (ICallback callback : getListenServiceCallbacks()) {
                callback.onSuccess(true, null);
            }
            clearListenServiceCallbacks();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            LogUtil.i(TAG, "onServiceDisconnected, mUiRequestCallbackRecord.size() : ",
                    mUiRequestCallbackRecord.size());
            mBinder = null;
            for (Map.Entry<String, WeakReference<ICallback>> entry : mUiRequestCallbackRecord.entrySet()) {
                if (null == entry.getValue()) {
                    LogUtil.i(TAG, "onServiceDisconnected, entry is null.");
                    continue;
                }
                if (null == entry.getValue().get()) {
                    LogUtil.i(TAG, "onServiceDisconnected, entry WeakReference is null.");
                    continue;
                }

                entry.getValue().get().onFailure(ICallback.ResultCode.PLAT_SERVICE_DISCONNECTED,
                        ICallback.ResultCode.getResultMessage(ICallback.ResultCode.PLAT_SERVICE_DISCONNECTED), null);
            }
            mUiRequestCallbackRecord.clear();
        }
    };

    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "destroy()");

        if (null != mCallbackHandler) {
            mCallbackHandler.removeCallbacksAndMessages(null);
            mCallbackHandlerThread.quit();
            mCallbackHandler = null;
            mCallbackHandlerThread = null;
        }

        if (null == mBinder) {
            return;
        }
        LogUtil.d(TAG, "unbindService()");
        mContext.unbindService(mConnect);
        mConnect = null;
        mBinder = null;
    }

    @Override
    public void initialize() {
        LogUtil.i(TAG, "initialize()");
        bindService();
        startHandlerThread();
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
    public boolean isInitializationSuccessful() {
        return null != mBinder;
    }

    @Override
    public void get(final String componentName, final IUiRequest request) throws BusinessLogicException {
        LogUtil.d(TAG, "get : ", componentName, ",", request.toString());
        try {
            request.createConversionCallback(true);
            request.setDefaultHandler(mCallbackHandler);
            checkBinder().get(componentName, request.getRequestAidl());
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (BusinessLogicException e) {
            getListenServiceCallbacks().add(new ListenServiceConnectCallback(new Runnable() {
                @Override
                public void run() {
                    try {
                        LogUtil.d(TAG, "after connect service get again : ", componentName, ",", request.toString());
                        checkBinder().get(componentName, request.getRequestAidl());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (BusinessLogicException e) {
                        e.printStackTrace();
                    }
                }
            }));
            bindService();
        }
    }

    @Override
    public void set(final String componentName, final IUiRequest request) throws BusinessLogicException {
        LogUtil.d(TAG, "set : ", componentName, ",", request.toString());
        try {
            request.createConversionCallback(true);
            request.setDefaultHandler(mCallbackHandler);
            checkBinder().set(componentName, request.getRequestAidl());
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (BusinessLogicException e) {
            getListenServiceCallbacks().add(new ListenServiceConnectCallback(new Runnable() {
                @Override
                public void run() {
                    try {
                        LogUtil.d(TAG, "after connect service set again : ", componentName, ",", request.toString());
                        checkBinder().set(componentName, request.getRequestAidl());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (BusinessLogicException e) {
                        e.printStackTrace();
                    }
                }
            }));
            bindService();
        }
    }

    @Override
    public void registerListener(final String componentName, final IUiRequest request) throws BusinessLogicException {
        LogUtil.d(TAG, "registerListener : ", componentName, ",", request.toString());
        try {
            request.createConversionCallback(true);
            request.setDefaultHandler(mCallbackHandler);
            checkBinder().registerListener(componentName, request.getRequestAidl());
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (BusinessLogicException e) {
            getListenServiceCallbacks().add(new ListenServiceConnectCallback(new Runnable() {
                @Override
                public void run() {
                    try {
                        LogUtil.d(TAG, "after connect service registerListener again : ", componentName, ",",
                                request.toString());
                        checkBinder().registerListener(componentName, request.getRequestAidl());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (BusinessLogicException e) {
                        e.printStackTrace();
                    }
                }
            }));
            bindService();
        }
    }

    @Override
    public void unregisterListener(final String componentName, final IUiRequest request) throws BusinessLogicException {
        LogUtil.d(TAG, "unregisterListener : ", componentName, ",", request.toString());
        try {
            request.createConversionCallback(true);
            request.setDefaultHandler(mCallbackHandler);
            checkBinder().unregisterListener(componentName, request.getRequestAidl());
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (BusinessLogicException e) {
            getListenServiceCallbacks().add(new ListenServiceConnectCallback(new Runnable() {
                @Override
                public void run() {
                    try {
                        LogUtil.d(TAG, "after connect service unregisterListener again : ", componentName, ",",
                                request.toString());
                        checkBinder().unregisterListener(componentName, request.getRequestAidl());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (BusinessLogicException e) {
                        e.printStackTrace();
                    }
                }
            }));
            bindService();
        }
    }

    @Override
    public Object syncGet(final String componentName, final IUiRequest request) throws BusinessLogicException {
        LogUtil.d(TAG, "syncGet : ", componentName, ",", request.toString());
        Map ret = null;
        try {
            request.createConversionCallback(true);
            ret = checkBinder().syncGet(componentName, request.getRequestAidl());
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (BusinessLogicException e) {
            bindService();
            throw new BusinessLogicException(ICallback.ResultCode.PLAT_SERVICE_IS_NULL,
                    ICallback.ResultCode.getResultMessage(ICallback.ResultCode.PLAT_SERVICE_IS_NULL));
        }

        //LogUtil.d(TAG, "syncGet  ret: ", ret.toString());
        Object obj = BusinessDataContainerManager.getResultData(ret);
        LogUtil.d(TAG, "syncGet  obj: ", obj);
        return obj;
    }

    @Override
    public Object syncSet(final String componentName, final IUiRequest request) throws BusinessLogicException {
        LogUtil.d(TAG, "syncSet : ", componentName, ",", request.toString());
        Map ret = null;
        try {
            request.createConversionCallback(true);
            ret = checkBinder().syncSet(componentName, request.getRequestAidl());
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (BusinessLogicException e) {
            bindService();
            throw new BusinessLogicException(ICallback.ResultCode.PLAT_SERVICE_IS_NULL,
                    ICallback.ResultCode.getResultMessage(ICallback.ResultCode.PLAT_SERVICE_IS_NULL));
        }
        //LogUtil.d(TAG, "syncSet  ret: ", ret.toString());
        Object obj = BusinessDataContainerManager.getResultData(ret);
        LogUtil.d(TAG, "syncSet  obj: ", obj);
        return obj;
    }

    @Override
    public void unBindBusinessLogic(final String componentName) throws BusinessLogicException {
        LogUtil.d(TAG, "unBindBusinessLogic(), ", componentName);
        if (null == componentName) {
            throw new BusinessLogicException(ICallback.ResultCode.COMPONENT_NAME_IS_NULL,
                    ICallback.ResultCode.getResultMessage(ICallback.ResultCode.COMPONENT_NAME_IS_NULL));
        }
        try {
            checkBinder().unBindBusinessLogic(componentName);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private IBusinessLogicApiAidl checkBinder() throws BusinessLogicException {
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


    private void checkUiCallbackRecord() {
        Iterator<Map.Entry<String, WeakReference<ICallback>>> it = mUiRequestCallbackRecord.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, WeakReference<ICallback>> entry = it.next();
            if (null == entry.getValue().get()) {
                LogUtil.i(TAG, "checkUiCallbackRecord() mWeakReference.get() is null");
                it.remove();//使用迭代器的remove()方法删除元素
            }
        }
    }
}
