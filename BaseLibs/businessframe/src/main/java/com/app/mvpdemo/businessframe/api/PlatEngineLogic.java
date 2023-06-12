package com.app.mvpdemo.businessframe.api;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import com.app.mvpdemo.businessframe.BusinessLogicManager;
import com.app.mvpdemo.businessframe.base.BusinessLogicException;
import com.app.mvpdemo.businessframe.base.IBusinessLogic;
import com.app.mvpdemo.businessframe.base.ICallback;
import com.app.mvpdemo.businessframe.base.IRequest;
import com.app.mvpdemo.businessframe.manager.ComponentManager;
import com.app.mvpdemo.util.log.LogUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlatEngineLogic {

    private static final String TAG = "PlatEngineLogic";
    private ExecutorService mExecutorService = null;
    private HandlerThread mHandlerThread = null;
    private Handler mHandler = null;
    private ComponentManager mComponentManager;

    public PlatEngineLogic(final Context context) throws BusinessLogicException {
        LogUtil.d(TAG, "onCreate");
        if (null == context) {
            throw new BusinessLogicException(0, "context is null");
        }
        //create executors
        mExecutorService = Executors.newFixedThreadPool(BusinessLogicManager.getInstance().getThreadCount());
        //create Handler Thread
        mHandlerThread = new HandlerThread(TAG + "PlatEngine Process Thread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        //create component manager
        mComponentManager = new ComponentManager(context);
    }

    public void onDestroy() {
        // TODO Auto-generated method stub
        if (null != mHandler) {
            mHandlerThread.quit();
            mHandler = null;
            mHandlerThread = null;
        }

        if (null != mExecutorService) {
            mExecutorService.shutdown();
            mExecutorService = null;
        }

        if (mComponentManager != null) {
            mComponentManager.onDestroy();
        }
        LogUtil.d(TAG, "onDestroy");
    }

    /**
     * set data to business logic component
     *
     * @param componentName
     * @param request
     */
    public void set(final String componentName, final IRequest request) {
        try {
            final IBusinessLogic logic = mComponentManager.getBusinessLogic(componentName);
            LogUtil.d(TAG, "set() current thread : ", Thread.currentThread().toString(), "--logic==", logic);
            asyncExecute(new Runnable() {
                @Override
                public void run() {
                    try {
                        logic.set(request);
                    } catch (BusinessLogicException e1) {
                        e1.printStackTrace();
                    }
                }
            });
        } catch (BusinessLogicException e) {
            processWhenLogicIsNull(request, e);
        }
    }

    /**
     * get data from business logic component
     *
     * @param componentName
     * @param request
     */
    public void get(final String componentName, final IRequest request) {
        try {
            final IBusinessLogic logic = mComponentManager.getBusinessLogic(componentName);
            LogUtil.d(TAG, "get() current thread : ", Thread.currentThread().toString());
            asyncExecute(new Runnable() {
                @Override
                public void run() {
                    try {
                        logic.get(request);
                    } catch (BusinessLogicException e1) {
                        e1.printStackTrace();
                    }
                }
            });
        } catch (BusinessLogicException e) {
            processWhenLogicIsNull(request, e);
        }
    }

    /**
     * register listener to business logic component
     *
     * @param componentName
     * @param request
     */
    public void registerListener(final String componentName, final IRequest request) {
        try {
            final IBusinessLogic logic = mComponentManager.getBusinessLogic(componentName);
            LogUtil.d(TAG, "registerListener() current thread : ", Thread.currentThread().toString());
            asyncExecute(new Runnable() {
                @Override
                public void run() {
                    try {
                        logic.registerListener(request);
                    } catch (BusinessLogicException e1) {
                        e1.printStackTrace();
                    }
                }
            });
        } catch (BusinessLogicException e) {
            processWhenLogicIsNull(request, e);
        }
    }

    /**
     * unregister listener from business logic component
     *
     * @param componentName
     * @param request
     */
    public void unregisterListener(final String componentName, final IRequest request) {
        try {
            final IBusinessLogic logic = mComponentManager.getBusinessLogic(componentName);
            LogUtil.d(TAG, "unregisterListener() current thread : ", Thread.currentThread().toString());
            asyncExecute(new Runnable() {
                @Override
                public void run() {
                    try {
                        logic.unregisterListener(request);
                    } catch (BusinessLogicException e1) {
                        e1.printStackTrace();
                    }
                }
            });
        } catch (BusinessLogicException e) {
            processWhenLogicIsNull(request, e);
        }
    }

    /**
     * get data from business logic component synchronously
     *
     * @param componentName
     * @param request
     * @return
     */
    public Object syncGet(final String componentName, final IRequest request) {
        IBusinessLogic logic = null;
        Object ret = null;
        try {
            logic = mComponentManager.getBusinessLogic(componentName);
            LogUtil.d(TAG, "syncGet() current thread : ", Thread.currentThread().toString());
            ret = logic.syncGet(request);
        } catch (BusinessLogicException e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * set data to business logic component synchronously
     *
     * @param componentName
     * @param request
     * @return
     */
    public Object syncSet(final String componentName, final IRequest request) {
        IBusinessLogic logic = null;
        Object ret = null;
        try {
            logic = mComponentManager.getBusinessLogic(componentName);
            LogUtil.d(TAG, "syncSet() current thread : ", Thread.currentThread().toString());
            ret = logic.syncSet(request);
        } catch (BusinessLogicException e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * unbind the business logic component when you never need it
     *
     * @param componentName
     * @throws BusinessLogicException
     */
    public void unBindBusinessLogic(final String componentName) {
        try {
            mComponentManager.unBindLogic(componentName);
            LogUtil.d(TAG, "unBindBusinessLogic() logic name:", componentName);
            LogUtil.d(TAG, "unBindBusinessLogic() current thread : ", Thread.currentThread().toString());
        } catch (BusinessLogicException e) {
            e.printStackTrace();
        }
    }

    private void asyncExecute(Runnable runnable) throws BusinessLogicException {
        LogUtil.d(TAG, "asyncExecute() : ");
        if (null == mExecutorService) {
            throw new BusinessLogicException(ICallback.ResultCode.PLAT_SERVICE_EXECUTOR_IS_NULL,
                    ICallback.ResultCode.getResultMessage(ICallback.ResultCode.PLAT_SERVICE_EXECUTOR_IS_NULL));
        }
        mExecutorService.execute(runnable);
    }

    private void processWhenLogicIsNull(final IRequest request, final BusinessLogicException e) {
        try {
            request.getCallback().onFailure(e.getCode(), e.getMessage(), request);
        } catch (BusinessLogicException e1) {
            e1.printStackTrace();
        }
    }

}
