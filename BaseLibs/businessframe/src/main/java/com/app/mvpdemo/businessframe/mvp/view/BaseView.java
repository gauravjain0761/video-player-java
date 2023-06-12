package com.app.mvpdemo.businessframe.mvp.view;

public interface BaseView<P> {
    void setPresenter(P presenter);

    void showToast(CharSequence msg);
}
