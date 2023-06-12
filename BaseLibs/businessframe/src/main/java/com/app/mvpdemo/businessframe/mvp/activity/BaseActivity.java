package com.app.mvpdemo.businessframe.mvp.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.app.mvpdemo.businessframe.base.IPresenter;
import com.app.mvpdemo.ui.dialog.LoadingDialog;
import com.app.mvpdemo.util.AppManager;
import com.app.mvpdemo.util.log.LogUtil;


/**
 * 所有Activity公共的父类
 *
 * @author weiwen
 * @created
 */
abstract public class BaseActivity<T extends IPresenter> extends AppCompatActivity {

    private final static String TAG = "BaseActivity";
    private T mPresenter;
    protected Bundle mBundle;
    protected LoadingDialog mProgressDialog;
    protected RelativeLayout mBaseContent, mBtnBack, mRightBaseBtn1Img, mRightBaseBtn2Img, mRightBtnTextLayout;
    protected TextView mTvBaseTitle, mRightBtnText;
    protected ImageView mRightBaseBtn1, mRightBaseBtn2;
    private View mBottomLine;

    ViewBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = getIntent().getExtras();
        AppManager.getInstance().addActivity(this);
        if (initBundle(mBundle)) {
            if (isNeedSetStatusBar()) {
                setStatusBar();
            }
            if (isSupportHeadLayout()) {
                setContentView(com.app.mvpdemo.ui.R.layout.bs_activity_base);
                initHeadLayout();
            } else {
                binding = getContentView();
                setContentView(binding.getRoot());
            }
            mPresenter = createPresenter(this);
            if (null != mPresenter) {
                mPresenter.initialize();
            } else {
                LogUtil.e(TAG, "onCreate(), mPresenter is null");
            }

            initWindow();
            initWidget();
            initData();
            registerEvent();
        } else {
            finish();
        }
    }

    protected boolean isNeedSetStatusBar() {
        return false;
    }

    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(com.app.mvpdemo.ui.R.color.color_bs_ui_FFFFFF));//设置状态栏颜色
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//实现状态栏图标和文字颜色为暗色
        }
    }

    protected abstract ViewBinding getContentView();

    protected boolean initBundle(Bundle bundle) {
        return true;
    }

    /**
     * 是否展示公共标题栏
     *
     * @return
     */
    protected boolean isSupportHeadLayout() {
        return false;
    }

    private void initHeadLayout() {
        mBtnBack = findViewById(com.app.mvpdemo.ui.R.id.img_back_container);
        mRightBaseBtn1Img = findViewById(com.app.mvpdemo.ui.R.id.right_btn1);
        mRightBaseBtn1 = findViewById(com.app.mvpdemo.ui.R.id.right_btn1_img);
        mRightBaseBtn2Img = findViewById(com.app.mvpdemo.ui.R.id.right_btn2);
        mRightBaseBtn2 = findViewById(com.app.mvpdemo.ui.R.id.right_btn2_img);
        mRightBtnText = findViewById(com.app.mvpdemo.ui.R.id.right_btn_text);
        mRightBtnTextLayout = findViewById(com.app.mvpdemo.ui.R.id.right_btn_layout);
        mTvBaseTitle = findViewById(com.app.mvpdemo.ui.R.id.title);
        mBaseContent = findViewById(com.app.mvpdemo.ui.R.id.rl_content);
        mBottomLine = findViewById(com.app.mvpdemo.ui.R.id.bottom_line);
        mBaseContent.addView(binding.getRoot(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mBottomLine.setVisibility(headerBottomLineVisible());
    }

    protected int headerBottomLineVisible() {
        return View.GONE;
    }

    /**
     * 设置header标题
     *
     * @param textId
     */
    protected void setHeaderTitle(int textId) {
        if (mTvBaseTitle != null) {
            mTvBaseTitle.setText(getResources().getString(textId));
        }
    }

    /**
     * 设置header标题
     *
     * @param text
     */
    protected void setHeaderTitle(String text) {
        if (mTvBaseTitle != null) {
            mTvBaseTitle.setText(text);
        }
    }

    /**
     * 设置右侧文本 及按钮点击
     *
     * @param text
     * @param onClickListener
     */
    protected void setRightButtonText(int text, View.OnClickListener onClickListener) {
        mRightBtnText.setText(getResources().getString(text));
        mRightBtnTextLayout.setOnClickListener(onClickListener);
    }

    protected void initProgressDialog() {
        LogUtil.d(TAG, "initProgressDialog()");
        if (mProgressDialog == null) {
            mProgressDialog = new LoadingDialog(this);
        }
    }

    public void showLoading(String title) {
        if (isFinishing()) {
            return;
        }
        initProgressDialog();
        mProgressDialog.setCancelable(true);
        mProgressDialog.setTitle(title);
        mProgressDialog.loading();
    }

    public void dismissLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * 展示加载成功对话框
     */
    protected void successLoading() {
        if (isFinishing()) {
            return;
        }
        initProgressDialog();
        mProgressDialog.loadingSuccess();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy(),", this);
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = null;
        AppManager.getInstance().removeActivity(this);

        try {
            unRegisterEvent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LogUtil.d(TAG, "onDetachedFromWindow(),", this);
        if (null != mPresenter) {
            mPresenter.destroy();
        } else {
            LogUtil.e(TAG, "onDetachedFromWindow(), mPresenter is null");
        }
    }

    public T getPresenter() {
        return mPresenter;
    }

    abstract protected T createPresenter(Context context);

    /**
     * 主要进行窗体初始化，一般集成类用不到
     */
    protected void initWindow() {
    }

    /**
     * 控件初始化，子类实现后进行相关view的实例化
     */
    protected abstract void initWidget();

    /**
     * 数据初始化，进行数据获取等相关操作
     */
    protected abstract void initData();

    protected abstract void registerEvent();

    protected abstract void unRegisterEvent();
}
