package com.app.mvpdemo.businessframe.api;


import com.app.mvpdemo.businessframe.base.IBusinessLogic;
import com.app.mvpdemo.businessframe.base.IRequest;

/**
 * 类功能描述
 *
 * @author weiwen
 * @since  2022/9/19 10:42 AM
 */
public interface INativeBindApi<T extends IBusinessLogic> {

    /**
     * write data to business logic asynchronously
     *
     * @param componentName
     * @param request
     */
    void set(String componentName, final IRequest request);

    /**
     * read data from business logic asynchronously
     *
     * @param componentName
     * @param request
     */
    void get(String componentName, final IRequest request);

    /**
     * register listener to business logic
     *
     * @param componentName
     * @param request
     */
    void registerListener(String componentName, final IRequest request);

    /**
     * unregister listener from business logic
     *
     * @param componentName
     * @param request
     */
    void unregisterListener(String componentName, final IRequest request);

    Object syncGet(String componentName, IRequest request);

    Object syncSet(String componentName, IRequest request);

    void unBindBusinessLogic(final String componentName);

    /**
     *
     */
    void destroy();
}
