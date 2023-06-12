package com.app.mvpdemo.businessframe.api;


import com.app.mvpdemo.businessframe.base.BusinessLogicException;
import com.app.mvpdemo.businessframe.base.ICallback;
import com.app.mvpdemo.businessframe.base.IUiRequest;

/**
 * 类功能描述
 *
 * @author weiwen
 * @since  2022/9/19 10:59 AM
 */
public interface IBusinessLogicApi {

    /**
     * read data from business logic
     *
     * @param componentName
     * @param request
     * @throws BusinessLogicException
     **/
    void get(final String componentName, final IUiRequest request) throws BusinessLogicException;

    /**
     * write data to business logic
     *
     * @param componentName
     * @param request
     * @throws BusinessLogicException
     **/
    void set(final String componentName, final IUiRequest request) throws BusinessLogicException;

    /**
     * read data from business logic synchronously
     *
     * @param componentName
     * @param request
     * @return
     * @throws BusinessLogicException
     */
    Object syncGet(final String componentName, final IUiRequest request) throws BusinessLogicException;

    /**
     * write data to business logic logic synchronously
     *
     * @param componentName
     * @param request
     * @return
     * @throws BusinessLogicException
     */
    Object syncSet(final String componentName, final IUiRequest request) throws BusinessLogicException;

    /**
     * register listener to business logic
     *
     * @param componentName
     * @param request
     * @throws BusinessLogicException
     **/
    void registerListener(final String componentName, final IUiRequest request) throws BusinessLogicException;

    /**
     * unregister listener from business logic
     *
     * @param componentName
     * @param request
     * @throws BusinessLogicException
     **/
    void unregisterListener(final String componentName, final IUiRequest request) throws BusinessLogicException;

    /**
     * you must unbind the business logic component when you never need it
     *
     * @param componentName
     * @throws BusinessLogicException
     */
    void unBindBusinessLogic(final String componentName) throws BusinessLogicException;

    /**
     * process when destroying business logic api
     */
    void onDestroy();

    /**
     * initialize business logic
     */
    void initialize();

    /**
     * is initialization successful
     *
     * @return
     */
    boolean isInitializationSuccessful();

    /**
     * observe initialization successful
     *
     * @param callback
     * @return
     */
    void observeInitializationSuccessful(ICallback callback);
}
