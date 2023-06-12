package com.app.mvpdemo.businessframe.base;

import android.os.Handler;
import android.os.RemoteException;


import com.app.mvpdemo.businessframe.base.BusinessLogicException;
import com.app.mvpdemo.businessframe.base.ICallback;
import com.app.mvpdemo.util.log.LogUtil;
import com.microsingle.plat.businessframe.base.ICallbackAidl;
import com.microsingle.plat.businessframe.base.IRequestAidl;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 统一module层调用参数
 *
 * @author weiwen
 * @since 2022/9/19 11:19 AM
 */
public class BusinessRequest implements IUiRequest {
    private static final String TAG = "BusinessRequest";

    private int mRequestCode;
    private Object mParameter = null;
    private WeakReference<ICallback> mCallback;
    private WeakReference<Handler> mCustomerHandler;
    private Object mTag = null;
    private Handler mDefaultHandler;
    private IRequestAidl mRequestAidl;
    private ICallback mNativeCallback;
    private Object mObject;
    private Method mMethod;

    /**
     * @param mRequestCode
     * @param mParameter
     * @param tag
     * @param mCustomerHandler
     * @param mCallback        if mCustomerHandler is not null, mCallback will be posted in it , otherwise the
     *                         mCallback will be posted in the default child thread handler of plat.
     */
    public BusinessRequest(int mRequestCode, Object mParameter, Object tag, Handler mCustomerHandler,
                           ICallback mCallback) {
        this.mRequestCode = mRequestCode;
        this.mParameter = mParameter;
        this.mCallback = null == mCallback ? null : new WeakReference<ICallback>(mCallback);
        this.mCustomerHandler = null == mCustomerHandler ? null : new WeakReference<Handler>(mCustomerHandler);
        this.mTag = tag;
    }

    /**
     * @param mRequestCode
     * @param mParameter
     * @param tag
     * @param mCallback    mCallback will be posted in the default child thread handler of plat
     */
    public BusinessRequest(int mRequestCode, Object mParameter, Object tag, ICallback mCallback) {
        this.mRequestCode = mRequestCode;
        this.mParameter = mParameter;
        this.mCallback = null == mCallback ? null : new WeakReference<ICallback>(mCallback);
        this.mTag = tag;
    }

    /**
     * @param mRequestCode
     * @param mParameter
     */
    public BusinessRequest(int mRequestCode, Object mParameter) {
        this.mRequestCode = mRequestCode;
        this.mParameter = mParameter;
    }

    @Override
    public int getRequestCode() {
        return mRequestCode;
    }

    public void setRequestCode(int requestCode) {
        this.mRequestCode = requestCode;
    }

    @Override
    public Object getParam() {
        return mParameter;
    }

    public void setParameter(Object mParameter) {
        this.mParameter = mParameter;
    }

    @Override
    public ICallback getCallback() throws BusinessLogicException {

        return mNativeCallback;
    }

    @Override
    public Handler getHandler() throws BusinessLogicException {
        Handler handler;
        if (null != mCustomerHandler) {
            handler = mCustomerHandler.get();
            if (null == handler) {
                throw new BusinessLogicException(0, "the mCustomerHandler Reference of BusinessRequest is null");
            }
            if (handler instanceof BusinessHandler && ((BusinessHandler) handler).isInvalid()) {
                throw new BusinessLogicException(0, "the Activity of mCustomerHandler is destroyed");
            }
        } else {
            if (null == mDefaultHandler) {
                throw new BusinessLogicException(1, "mDefaultHandler of BusinessRequest is null");
            }
            handler = mDefaultHandler;
        }

        return handler;
    }

    @Override
    public void setDefaultHandler(Handler handler) {
        mDefaultHandler = handler;
    }

    @Override
    public Object getTag() {
        return mTag;
    }

    @Override
    public void setTag(Object obj) {
        mTag = obj;
    }

    @Override
    public IRequestAidl getRequestAidl() {
        return mRequestAidl;
    }

    @Override
    public void createConversionCallback(boolean remoteProcess) {
        if (remoteProcess) {
            createRequestAidl();
        } else {
            createNativeCallback();
        }
    }

    /**
     *
     */
    private void createNativeCallback() {
        mNativeCallback = new ICallback() {
            @Override
            public void onSuccess(final Object data, final IRequest request) {
                try {
                    BusinessRequest.this.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                BusinessRequest.this.getUiCallback().onSuccess(data, request);
                            } catch (BusinessLogicException e1) {
                                e1.printStackTrace();
                                LogUtil.d(TAG, "request : ", request);
                            }
                        }
                    });
                } catch (BusinessLogicException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(final int errorCode, final String errorMsg, final IRequest request) {
                try {
                    BusinessRequest.this.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                BusinessRequest.this.getUiCallback().onFailure(errorCode, errorMsg, request);
                            } catch (BusinessLogicException e1) {
                                e1.printStackTrace();
                                LogUtil.d(TAG, "request : ", request);
                            }
                        }
                    });
                } catch (BusinessLogicException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onProgress(final Object data, final IRequest request) {
                try {
                    BusinessRequest.this.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                BusinessRequest.this.getUiCallback().onProgress(data, request);
                            } catch (BusinessLogicException e1) {
                                e1.printStackTrace();
                                LogUtil.d(TAG, "request : ", request);
                            }
                        }
                    });
                } catch (BusinessLogicException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * create request Aidl
     */
    private void createRequestAidl() {
        mRequestAidl = new IRequestAidl.Stub() {
            private ICallbackAidl mCallbackAidl = new ICallbackAidl.Stub() {
                @Override
                public void onSuccess(final Map data, final IRequestAidl request) throws
                        RemoteException {
                    try {
                        LogUtil.i(TAG, "onSuccess==");
                        BusinessRequest.this.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    BusinessRequest.this.getUiCallback()
                                            .onSuccess(BusinessDataContainerManager.getResultData(data),
                                                    BusinessRequest.this);
                                } catch (BusinessLogicException e1) {
                                    e1.printStackTrace();
                                    LogUtil.d(TAG, "request : ", request);
                                }
                            }
                        });
                    } catch (BusinessLogicException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(final int errorCode, final String errorMsg, final
                IRequestAidl request)
                        throws RemoteException {
                    try {
                        BusinessRequest.this.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    BusinessRequest.this.getUiCallback()
                                            .onFailure(errorCode, errorMsg, BusinessRequest.this);
                                } catch (BusinessLogicException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        });
                    } catch (BusinessLogicException e) {
                        e.printStackTrace();
                        LogUtil.d(TAG, "request : ", request);
                    }
                }

                @Override
                public void onProgress(final Map data, final IRequestAidl request) throws
                        RemoteException {
                    try {
                        BusinessRequest.this.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    BusinessRequest.this.getUiCallback()
                                            .onProgress(BusinessDataContainerManager.getResultData(data),
                                                    BusinessRequest.this);
                                } catch (BusinessLogicException e1) {
                                    e1.printStackTrace();
                                    LogUtil.d(TAG, "request : ", request);
                                }
                            }
                        });
                    } catch (BusinessLogicException e) {
                        e.printStackTrace();
                    }
                }
            };

            @Override
            public int getRequestCode() throws RemoteException {
                return mRequestCode;
            }

            @Override
            public Map getParam() throws RemoteException {
                return BusinessDataContainerManager.assembleRequest(mParameter, mTag);
            }

            @Override
            public ICallbackAidl getCallback() throws RemoteException {
                return mCallbackAidl;
            }
        };
    }

    @Override
    public String toString() {
        return "BusinessRequest{" + "mRequestCode=" + mRequestCode + ", mParameter=" + mParameter + ", mCallback="
                + mCallback + ", mCustomerHandler=" + mCustomerHandler + ", mTag=" + mTag + ", mDefaultHandler="
                + mDefaultHandler + ", mRequestAidl=" + mRequestAidl + '}';
    }

    /**
     * get ui callback
     *
     * @return
     * @throws BusinessLogicException
     */
    public ICallback getUiCallback() throws BusinessLogicException {
        if (null == mCallback) {
            throw new BusinessLogicException(0, "the mCallback is null");
        }

        if (null != mCallback && null == mCallback.get()) {
            throw new BusinessLogicException(1, "the mCallback Reference of BusinessRequest is " + "null");
        }

        return mCallback.get();
    }

    public Object getObject() {
        return mObject;
    }

    public void setObject(Object object) {
        this.mObject = object;
    }

    public Method getMethod() {
        return mMethod;
    }

    public void setMethod(Method method) {
        this.mMethod = method;
    }
}
