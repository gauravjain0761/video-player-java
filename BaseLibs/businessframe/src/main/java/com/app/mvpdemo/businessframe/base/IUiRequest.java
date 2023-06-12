package com.app.mvpdemo.businessframe.base;

import android.os.Handler;

import com.microsingle.plat.businessframe.base.IRequestAidl;

/**
 * 类功能描述
 *
 * @author weiwen
 * @since  2022/9/19 10:52 AM
 */

public interface IUiRequest extends IRequest {

    /**
     * get the handler,callback will be posted in it
     *
     * @return
     * @throws BusinessLogicException
     */
    Handler getHandler() throws BusinessLogicException;

    /**
     * set default handler
     *
     * @param handler
     */
    void setDefaultHandler(Handler handler);

    /**
     * get the request aidl
     *
     * @return
     */
    IRequestAidl getRequestAidl();

    /**
     * create conversion callback
     *
     * @param remoteProcess true: create aidl callback; false: create native process callback;
     */
    void createConversionCallback(boolean remoteProcess);
}
