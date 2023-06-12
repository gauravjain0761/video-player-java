package com.app.mvpdemo.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.mvpdemo.ui.R;


/**
 * 类功能描述
 *
 * @author weiwen
 * @created 2022/5/7 3:59 下午
 */
public class LoadingDialog extends Dialog {

    private static final String TAG = "LoadingDialog";

    //    private ImageView mSuccessImg;
//    private LottieAnimationView mAnimationView;
    private TextView mTitle;

    public LoadingDialog(@NonNull Context context) {
        this(context, R.style.bs_fullScreenDialog);
    }

    public LoadingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    private void init(Context context) {
        View content = LayoutInflater.from(context).inflate(
                R.layout.bs_dialog_loading, null);
        setContentView(content);
        initWidget();
    }

    private void initWidget() {
        mTitle = findViewById(R.id.loading_txt);
    }

    /**
     * 设置标题
     *
     * @param text
     */
    public void setTitle(String text) {
        mTitle.setText(text);
        if (TextUtils.isEmpty(text)) {
            mTitle.setVisibility(View.GONE);
        } else {
            mTitle.setVisibility(View.VISIBLE);
        }
    }

    /**
     * loading
     */
    public void loading() {
        show();
    }

    /**
     * 加载成功
     */
    public void loadingSuccess() {
        dismiss();
    }
}