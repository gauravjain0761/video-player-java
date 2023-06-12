package com.app.mvpdemo.businessframe.mvp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.app.mvpdemo.businessframe.base.IBaseContract;
import com.app.mvpdemo.businessframe.base.IBasePresenter;
import com.app.mvpdemo.ui.dialog.LoadingDialog;
import com.app.mvpdemo.util.log.LogUtil;


/**
 * Created by weiwen.
 */

abstract public class BaseFragment<T extends IBasePresenter> extends Fragment implements IBaseContract.IView {

    private final static String TAG = "BaseFragment";
    private T mPresenter;
    protected Bundle mBundle;
    protected View mRoot;
    protected Context mContext;
    protected LoadingDialog mProgressDialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = getArguments();
        initBundle(mBundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot != null) {
            ViewGroup parent = (ViewGroup) mRoot.getParent();
            if (parent != null) parent.removeView(mRoot);
        } else {
            mRoot = getContentView().getRoot();
            // Do something
            onBindViewBefore(mRoot);
            // Get savedInstanceState
            if (savedInstanceState != null) onRestartInstance(savedInstanceState);
        }
        return mRoot;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = createPresenter(this.getContext());
        if (null != mPresenter) {
            mPresenter.initialize();
        } else {
            LogUtil.e(TAG, "onViewCreated(), mPresenter is null");
        }
        // Init
        initWidget(view);
        initData();
        registerEvent();
    }

    protected void onBindViewBefore(View root) {
        // ...
    }

    protected void onRestartInstance(Bundle bundle) {

    }

    protected abstract ViewBinding getContentView();

    protected void initBundle(Bundle bundle) {

    }

    protected void initProgressDialog() {
        LogUtil.d(TAG, "initProgressDialog()");
        if (mProgressDialog == null && getActivity() != null) {
            mProgressDialog = new LoadingDialog(getActivity());
        }
    }

    /**
     * show loading dialog
     *
     * @param title loading title
     */
    public void showLoading(String title) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        initProgressDialog();
        mProgressDialog.setCancelable(true);
        mProgressDialog.setTitle(title);
        mProgressDialog.loading();
    }

    public void showLoadingWithCancelable(String title, boolean isCancelable) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        initProgressDialog();
        mProgressDialog.setCancelable(false);
        mProgressDialog.setTitle(title);
        mProgressDialog.loading();
    }

    /**
     * dismiss loading dialog
     */
    public void dismissLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != mPresenter) {
            mPresenter.destroy();
        } else {
            LogUtil.e(TAG, "onDestroyView(), mPresenter is null");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterEvent();
    }


    @Override
    public void onWillDestroy() {

    }

    @Override
    public void setParameter(Object parameter) {

    }


    @Override
    public void onVisible() {

    }

    @Override
    public void onInvisible() {

    }

    @Override
    public void onWillVisible() {

    }

    @Override
    public void onWillInvisible() {

    }

    @Override
    public void onCreated() {

    }

    @Override
    public boolean isPauseRefresh() {
        return false;
    }


    @Override
    public void setLifeCycleManageMode(@IBaseContract.Constant.LifeCycleManageMode int mode) {

    }

    public T getPresenter() {
        return mPresenter;
    }

    /**
     * create the presenter of the fragment
     *
     * @param context
     * @return
     */
    abstract protected T createPresenter(Context context);

    protected abstract void initWidget(View root);

    protected abstract void initData();

    protected abstract void registerEvent();

    protected abstract void unRegisterEvent();
}
