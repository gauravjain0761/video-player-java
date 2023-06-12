package com.app.mvpdemo.businessframe.api;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.app.mvpdemo.businessframe.base.BusinessLogicException;
import com.app.mvpdemo.businessframe.base.IRequest;
import com.app.mvpdemo.util.log.LogUtil;

/**
 * 主进程service，用于调用Module的通信服务
 *
 * @author weiwen
 * @since 2022/9/19 10:42 AM
 */
public class PlatNativeEngineService extends Service {

    private static final String TAG = "PlatNativeEngineService";
    private final PlatEngineBinder mBinder = new PlatEngineBinder();
    private PlatEngineLogic mPlatEngineLogic = null;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "onCreate");
        try {
            mPlatEngineLogic = new PlatEngineLogic(this);
        } catch (BusinessLogicException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        LogUtil.d(TAG, "onBind() intent : ", intent.toString());
        return mBinder;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (null != mPlatEngineLogic) {
            mPlatEngineLogic.onDestroy();
            mPlatEngineLogic = null;
        }
        LogUtil.d(TAG, "onDestroy");
    }

    public class PlatEngineBinder extends Binder implements INativeBindApi {

        @Override
        public void set(String componentName, final IRequest request) {
            try {
                getPlatEngineLogic().set(componentName, request);
            } catch (BusinessLogicException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void get(String componentName, final IRequest request) {
            try {
                getPlatEngineLogic().get(componentName, request);
            } catch (BusinessLogicException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void registerListener(String componentName, final IRequest request) {
            try {
                getPlatEngineLogic().registerListener(componentName, request);
            } catch (BusinessLogicException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void unregisterListener(String componentName, final IRequest request) {
            try {
                getPlatEngineLogic().unregisterListener(componentName, request);
            } catch (BusinessLogicException e) {
                e.printStackTrace();
            }
        }

        @Override
        public Object syncGet(String componentName, IRequest request) {
            try {
                return getPlatEngineLogic().syncGet(componentName, request);
            } catch (BusinessLogicException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public Object syncSet(String componentName, IRequest request) {
            try {
                return getPlatEngineLogic().syncSet(componentName, request);
            } catch (BusinessLogicException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void unBindBusinessLogic(final String componentName) {
            try {
                getPlatEngineLogic().unBindBusinessLogic(componentName);
            } catch (BusinessLogicException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void destroy() {

        }
    }

    private PlatEngineLogic getPlatEngineLogic() throws BusinessLogicException {
        if (null == mPlatEngineLogic) {
            throw new BusinessLogicException(0, "mPlatEngineLogic is null");
        }
        return mPlatEngineLogic;
    }
}
