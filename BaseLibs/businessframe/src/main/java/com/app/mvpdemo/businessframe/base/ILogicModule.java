package com.app.mvpdemo.businessframe.base;


import android.content.Context;
import android.os.Handler;

import java.util.concurrent.ExecutorService;

/**
 * module接口
 *
 * @author weiwen
 * @since  2022/9/19 10:13 AM
 */
public interface ILogicModule {

    /**
     * process the function after new object
     *
     * @param context
     */
    void onCreate(Context context);

    /**
     * process the function before exit
     */
    void onDestroy();

    /**
     * set executorService
     *
     * @param executorService
     */
    void setExecutorService(ExecutorService executorService);

    /**
     * get executorService
     *
     * @return
     */
    ExecutorService getExecutorService();

    /**
     * set child thread handler
     *
     * @param handler
     */
    void setChildThreadHandler(Handler handler);

    /**
     * get child thread handler
     *
     * @return
     */
    Handler getChildThreadHandler();
}
