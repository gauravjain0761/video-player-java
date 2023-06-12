package com.app.videoplayer.business;


import android.content.Context;

import androidx.annotation.NonNull;

import com.app.videoplayer.db.DaoUtilsStore;
import com.app.videoplayer.entity.SongEntity;
import com.app.mvpdemo.businessframe.base.AbstractLogicModule;
import com.app.mvpdemo.businessframe.base.BusinessLogicException;
import com.app.mvpdemo.businessframe.base.IRequest;
import com.app.mvpdemo.util.log.LogUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * module层
 *
 * @author weiwen
 * @since 2022/9/30 2:39 下午
 */
public class SampleManagerModule extends AbstractLogicModule {

    private static final String TAG = "SampleManagerModule";
    private Map<String, IRequest> mListeners;

    public static final class ErrorCode {

        public static final int UNREGISTER_FAIL = 0;
        public static final int REQUEST_PARAMS_ERROR = 1;
        public static final int REQUEST_DB_OPTION_ERROR = 2;
    }

    @Override
    public void onCreate(Context context) {
        super.onCreate(context);
        mListeners = new HashMap<>();
    }

    /**
     * register launcher update listener
     *
     * @param request
     */
    public void registerRightsUpdateLister(final @NonNull IRequest request) throws BusinessLogicException {
        LogUtil.d(TAG, "registerRightsUpdateLister enter");
        if (!(request.getParam() instanceof String)) {
            throw new BusinessLogicException(0,
                    "AccountLogicModule, " + "registerRightsUpdateLister param is not String");
        }
        mListeners.put((String) request.getParam(), request);
        onSuccess(request, null);
    }

    /**
     * unregister launcher update listener
     *
     * @param request
     */
    public void unregisterRightsUpdateLister(final @NonNull IRequest request) throws BusinessLogicException {
        LogUtil.d(TAG, "unregisterRightsUpdateLister enter");
        if (!(request.getParam() instanceof String)) {
            throw new BusinessLogicException(0,
                    "AccountLogicModule, " + "unregisterRightsUpdateLister param is not String");
        }
        Object obj = mListeners.remove((String) request.getParam());
        if (null == obj) {
            onFailure(request, ErrorCode.UNREGISTER_FAIL, "callback is not " + "registered");
        } else {
            onSuccess(request, true);
        }
    }


    /**
     * Add Song Entity
     *
     * @param request
     */
    public void addUser(IRequest request) {
        LogUtil.i(TAG, "receiveWaveData---");
        Object requestParam = request.getParam();
        if (requestParam instanceof SongEntity) {
            DaoUtilsStore.getInstance().insertSongEntity((SongEntity) requestParam);
            onSuccess(request, requestParam);
        } else {
            onFailure(request, ErrorCode.REQUEST_PARAMS_ERROR, "param is not SongEntity");
        }
    }

    /**
     * query all user
     *
     * @param request
     */
    public void queryAllUser(IRequest request) {
        try {
            List<SongEntity> allUserEntityList = DaoUtilsStore.getInstance().queryAllSongEntity();
            onSuccess(request, allUserEntityList);
        } catch (Exception e) {
            e.printStackTrace();
            onFailure(request, ErrorCode.REQUEST_DB_OPTION_ERROR, e.getMessage());
        }
    }

    /**
     * delete user by id
     *
     * @param request
     */
    public void deleteUserById(IRequest request) {
        Object requestParam = request.getParam();
        if (requestParam instanceof Long) {
            try {
                DaoUtilsStore.getInstance().deleteSongEntityById((Long) requestParam);
                onSuccess(request, requestParam);
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(request, ErrorCode.REQUEST_DB_OPTION_ERROR, e.getMessage());
            }
        } else {
            onFailure(request, ErrorCode.REQUEST_PARAMS_ERROR, "param is not Long");
        }
    }
}
