package com.app.videoplayer.business;

import android.content.Context;

import com.app.mvpdemo.businessframe.base.BusinessLogicException;
import com.app.mvpdemo.businessframe.base.BusinessLogicImp;
import com.app.mvpdemo.businessframe.base.IRequest;
import com.app.mvpdemo.util.log.LogUtil;

public class SampleManagerBusinessLogic extends BusinessLogicImp {

    private static final String TAG = "AudioManagerBusinessLogic";

    private SampleManagerModule mManagerModule;

    /**
     * the request code of set
     */
    public static final class SetRequestCode {

        public static final int SET_ADD_USER = 0;
        public static final int SET_DELETE_USER = 1;

    }

    public static final class GetRequestCode {

        public static final int GET_USER_DATA = 200;

    }

    public static final class RegisterRequestCode {

        public static final int REGISTER_LISTENER = 100;

    }

    public static final class SyncSetRequestCode {

        public static final int SYNC_SET_DATA = 1000;


    }

    public static final class SyncGetRequestCode {

        public static final int GET_GET_DATA = 2000;


    }

    @Override
    public void onCreate(Context context) {
        super.onCreate(context);
        try {
            mManagerModule = bindModule(SampleManagerModule.class);
        } catch (BusinessLogicException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void get(IRequest request) throws BusinessLogicException {
        LogUtil.i(TAG, "get===", request.getRequestCode());
        switch (request.getRequestCode()) {
            case GetRequestCode.GET_USER_DATA:
                mManagerModule.queryAllUser(request);
                break;
            default:
                break;
        }
    }

    @Override
    public void set(IRequest request) throws BusinessLogicException {
        LogUtil.i(TAG, "set===", request.getRequestCode());
        switch (request.getRequestCode()) {
            case SetRequestCode.SET_ADD_USER:
                mManagerModule.addUser(request);
                break;
            case SetRequestCode.SET_DELETE_USER:
                mManagerModule.deleteUserById(request);
            default:
                break;
        }
    }

    @Override
    public Object syncGet(IRequest request) throws BusinessLogicException {
        LogUtil.i(TAG, "syncGet===", request.getRequestCode());
        switch (request.getRequestCode()) {
            case SyncGetRequestCode.GET_GET_DATA:
                //todo
                return null;
            default:
                break;
        }
        return null;
    }

    @Override
    public Object syncSet(IRequest request) throws BusinessLogicException {
        LogUtil.i(TAG, "syncSet===", request.getRequestCode());
        switch (request.getRequestCode()) {
            case SyncSetRequestCode.SYNC_SET_DATA:
                //todo
                break;
            default:
                break;
        }
        return null;
    }

    @Override
    public void registerListener(IRequest request) throws BusinessLogicException {
        LogUtil.i(TAG, "registerListener===", request.getRequestCode());
        switch (request.getRequestCode()) {
            case RegisterRequestCode.REGISTER_LISTENER:
                mManagerModule.registerRightsUpdateLister(request);
                break;
            default:
                break;
        }
    }

    @Override
    public void unregisterListener(IRequest request) throws BusinessLogicException {
        LogUtil.i(TAG, "unregisterListener===", request.getRequestCode());
        switch (request.getRequestCode()) {
            case RegisterRequestCode.REGISTER_LISTENER:
                mManagerModule.unregisterRightsUpdateLister(request);
                break;
            default:
                break;
        }
    }
}
