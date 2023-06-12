package com.app.mvpdemo.ui.base;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * draw绘制view 公共父类
 *
 * @author weiwen
 * @created 2022/5/23 11:23 上午
 */
public abstract class BaseDrawView extends View {

    protected int mWidth = 0, mHeight = 0;

    public BaseDrawView(Context context) {
        super(context);
    }

    public BaseDrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseDrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initView(canvas);
    }

    protected abstract void initView(Canvas canvas);
}
