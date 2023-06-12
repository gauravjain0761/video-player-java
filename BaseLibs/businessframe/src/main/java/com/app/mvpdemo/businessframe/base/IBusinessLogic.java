package com.app.mvpdemo.businessframe.base;

import android.content.Context;

import com.app.mvpdemo.businessframe.manager.ModuleManager;


/**
 * 类功能描述
 *
 * @author weiwen
 * @since  2022/9/30 12:00 下午
 */
public interface IBusinessLogic {

    /**
     * @param context
     */
    void onCreate(Context context);

    /**
     **/
    void onDestroy();

    /**
     * read data from business logic
     *
     * @param request
     **/
    void get(IRequest request) throws BusinessLogicException;

    /**
     * write data to business logic
     *
     * @param request
     **/
    void set(IRequest request) throws BusinessLogicException;

    /**
     * read data from business logic synchronously
     *
     * @param request
     * @return
     * @throws BusinessLogicException
     */
    Object syncGet(IRequest request) throws BusinessLogicException;

    /**
     * write data to business logic synchronously
     *
     * @param request
     * @return
     * @throws BusinessLogicException
     */
    Object syncSet(IRequest request) throws BusinessLogicException;

    /**
     * register listener to business logic
     *
     * @param request
     **/
    void registerListener(IRequest request) throws BusinessLogicException;

    /**
     * unregister listener from business logic
     *
     * @param request
     **/
    void unregisterListener(IRequest request) throws BusinessLogicException;


    /**
     * set module manager
     *
     * @param moduleManager
     */
    void setModuleManager(ModuleManager moduleManager);
}
