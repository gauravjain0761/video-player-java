package com.app.videoplayer.ui.presenter;

import android.content.Context;

import com.app.videoplayer.R;
import com.app.videoplayer.business.SampleManagerBusinessLogic;
import com.app.videoplayer.databinding.ActivitySearchScanBinding;
import com.app.videoplayer.entity.SongEntity;
import com.app.videoplayer.ui.IActivityContract;
import com.app.mvpdemo.businessframe.BusinessLogicManager;
import com.app.mvpdemo.businessframe.api.IBusinessLogicApi;
import com.app.mvpdemo.businessframe.base.BusinessLogicException;
import com.app.mvpdemo.businessframe.base.BusinessRequest;
import com.app.mvpdemo.businessframe.base.ICallback;
import com.app.mvpdemo.businessframe.base.IRequest;
import com.app.mvpdemo.businessframe.base.IUiRequest;
import com.app.mvpdemo.businessframe.mvp.presenter.BasePresenter;
import com.app.mvpdemo.util.log.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchTrashedActivityPresenter extends BasePresenter<IActivityContract.IActivityView> implements IActivityContract.IActivityPresenter, ICallback {

    private static final String TAG = SearchTrashedActivityPresenter.class.getSimpleName();
    private IBusinessLogicApi mIBusinessLogicApi;

    private String mSampleLogicName = SampleManagerBusinessLogic.class.getName();
    Context context;
    ActivitySearchScanBinding binding;

    public SearchTrashedActivityPresenter(Context context, IActivityContract.IActivityView view) {
        super(context, view);
        this.context = context;
    }

    public void setBinding(ActivitySearchScanBinding bind) {
        this.binding = bind;
    }

    @Override
    public void initialize() {
        /**
         * logic api获取，参数代表是否跨进程
         * 传true，后面执行的方法均会在子进程中执行
         */
        mIBusinessLogicApi = BusinessLogicManager.getInstance().getBusinessLogicApi(false);
        registerListener();

    }

    private void registerListener() {
        IUiRequest iRequest = new BusinessRequest(SampleManagerBusinessLogic.RegisterRequestCode.REGISTER_LISTENER, null, null, getAsyncHandler(), this);
        try {
            mIBusinessLogicApi.registerListener(mSampleLogicName, iRequest);
        } catch (BusinessLogicException e) {
            e.printStackTrace();
        }
    }

    private void unRegisterListener() {
        IUiRequest iRequest = new BusinessRequest(SampleManagerBusinessLogic.RegisterRequestCode.REGISTER_LISTENER, TAG, null, getMainHandler(), this);
        try {
            mIBusinessLogicApi.unregisterListener(mSampleLogicName, iRequest);
        } catch (BusinessLogicException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理逻辑层 统一回调
     *
     * @param data
     * @param request
     */
    @Override
    public void onSuccess(Object data, IRequest request) {
        LogUtil.i(TAG, "onSuccess===", request.getRequestCode());
        switch (request.getRequestCode()) {
            case SampleManagerBusinessLogic.SetRequestCode.SET_ADD_USER:
                runRunnableInMain(() -> {
                    dismissLoading();
                    if (data instanceof SongEntity) {
                        getPresenterView().addSongs((SongEntity) data);
                    }
                });
                break;
            case SampleManagerBusinessLogic.SetRequestCode.SET_DELETE_USER:
                runRunnableInMain(() -> {
                    dismissLoading();
                });
                break;
            case SampleManagerBusinessLogic.GetRequestCode.GET_USER_DATA:
                runRunnableInMain(() -> {
                    dismissLoading();
                    if (data instanceof ArrayList && ((ArrayList<?>) data).size() > 0 && data instanceof ArrayList && ((ArrayList<?>) data).get(0) instanceof SongEntity) {
                        getPresenterView().updateSongsList((List<SongEntity>) data);
                    }
                });
            default:
                break;
        }
    }

    @Override
    public void onFailure(int errorCode, String errorMsg, IRequest request) {

    }

    @Override
    public void onProgress(Object data, IRequest request) {

    }


    @Override
    public void addSong(SongEntity songEntity) {
        IUiRequest iRequest = new BusinessRequest(SampleManagerBusinessLogic.SetRequestCode.SET_ADD_USER, songEntity, null, getAsyncHandler(), this);
        try {
            showLoading(getContext().getString(R.string.inserting));
            mIBusinessLogicApi.set(mSampleLogicName, iRequest);
        } catch (BusinessLogicException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteSongById(long id) {
        IUiRequest iRequest = new BusinessRequest(SampleManagerBusinessLogic.SetRequestCode.SET_DELETE_USER, id, null, getAsyncHandler(), this);
        try {
            showLoading(getContext().getString(R.string.deleting));
            mIBusinessLogicApi.set(mSampleLogicName, iRequest);
        } catch (BusinessLogicException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void selectSongsList() {
        IUiRequest iRequest = new BusinessRequest(SampleManagerBusinessLogic.GetRequestCode.GET_USER_DATA, null, null, getAsyncHandler(), this);
        showLoading(getContext().getString(R.string.initializing));
        //Test show loading, delay select data 1s
        runRunnableInMainDelay(() -> {
            try {
                mIBusinessLogicApi.get(mSampleLogicName, iRequest);
            } catch (BusinessLogicException e) {
                e.printStackTrace();
            }
        }, 1000);

    }

    @Override
    public void destroy() {
        unRegisterListener();
        super.destroy();
    }
}