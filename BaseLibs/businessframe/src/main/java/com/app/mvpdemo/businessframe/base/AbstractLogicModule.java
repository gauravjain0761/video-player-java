package com.app.mvpdemo.businessframe.base;


import android.content.Context;
import android.os.Handler;

import com.app.mvpdemo.util.log.LogUtil;

import java.util.concurrent.ExecutorService;

public class AbstractLogicModule implements ILogicModule {

    private static final String TAG = "AbstractLogicModule";

    private Context mContext;
    private Handler mHandler;
    private ExecutorService mExecutorService;


    @Override
    public void onCreate(Context context) {
        LogUtil.d(TAG, "onCreate() name : ", this.getClass().getName());
        this.mContext = context;
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "onDestroy() name : ", this.getClass().getName());
    }


    @Override
    public void setExecutorService(ExecutorService executorService) {
        mExecutorService = executorService;
    }

    @Override
    public ExecutorService getExecutorService() {
        return mExecutorService;
    }

    public Context getContext() throws BusinessLogicException {
        if (null == mContext) {
            throw new BusinessLogicException(0, "AbstractLogicModule,context is null");
        }
        return mContext;
    }

//    @Override
//    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
//        if (objects == null || objects.length == 0 || !(objects[0] instanceof BusinessRequest)) {
//            LogUtil.i(TAG, "method param invalid");
//            return null;
//        }
//        BusinessRequest requestParam = (BusinessRequest) objects[0];
//        requestParam.setMethod(method);
//        requestParam.setObject(o);
//        if (method.isAnnotationPresent(Async.class)) {
//            //异步,此处先不用service，太重
//            BusinessLogicManager.getInstance().asyncExecute(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        method.invoke(AbstractLogicModule.this, objects);
//                    } catch (IllegalAccessException | InvocationTargetException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//            return null;
//        } else if (method.isAnnotationPresent(Remote.class)) {
//            //跨进程
//            BusinessLogicManager.getInstance().getBusinessLogicApi(true);
//            return null;
//        } else {
//            return method.invoke(this, objects);
//        }
//    }


    @Override
    public void setChildThreadHandler(Handler handler) {
        mHandler = handler;
    }

    @Override
    public Handler getChildThreadHandler() {
        return mHandler;
    }


    /**
     * process success of callback
     *
     * @param request
     * @param param
     */
    public void onSuccess(final IRequest request, final Object param) {
        if (null == request) {
            return;
        }

        try {
            request.getCallback().onSuccess(param, request);
        } catch (BusinessLogicException e) {
            e.printStackTrace();
        }
    }

    /**
     * process success of callback
     *
     * @param request
     * @param param
     * @param tag
     */
    public void onSuccess(final IRequest request, final Object param, final Object tag) {
        if (null == request) {
            return;
        }

        try {
            request.setTag(tag);
            request.getCallback().onSuccess(param, request);
        } catch (BusinessLogicException e) {
            e.printStackTrace();
        }
    }

    /**
     * process failure of callback
     *
     * @param request
     * @param errorCode
     * @param errorMessage
     */
    public void onFailure(final IRequest request, final int errorCode, final String errorMessage) {
        if (null == request) {
            return;
        }

        try {
            request.getCallback().onFailure(errorCode, errorMessage, request);
        } catch (BusinessLogicException e) {
            e.printStackTrace();
        }
    }

    /**
     * process failure of callback
     *
     * @param request
     * @param errorCode
     * @param errorMessage
     * @param tag
     */
    public void onFailure(final IRequest request, final int errorCode, final String errorMessage, final Object tag) {
        if (null == request) {
            return;
        }

        try {
            request.setTag(tag);
            request.getCallback().onFailure(errorCode, errorMessage, request);
        } catch (BusinessLogicException e) {
            e.printStackTrace();
        }
    }

    /**
     * process progress of callback
     *
     * @param request
     * @param param
     */
    public void onProgress(final IRequest request, final Object param) {
        if (null == request) {
            return;
        }

        try {
            request.getCallback().onProgress(param, request);
        } catch (BusinessLogicException e) {
            e.printStackTrace();
        }
    }

    /**
     * process progress of callback
     *
     * @param request
     * @param param
     * @param tag
     */
    public void onProgress(final IRequest request, final Object param, final Object tag) {
        if (null == request) {
            return;
        }

        try {
            request.setTag(tag);
            request.getCallback().onProgress(param, request);
        } catch (BusinessLogicException e) {
            e.printStackTrace();
        }
    }
}
