package com.app.mvpdemo.businessframe.base;

import java.lang.reflect.Method;

/**
 * 类功能描述
 *
 * @author weiwen
 * @since 2022/9/19 10:55 AM
 */
public interface IRequest {

    /**
     * get request code
     **/
    public int getRequestCode();

    /**
     * get the parameter of request
     **/
    public Object getParam();

    /**
     * get callback function
     *
     * @return
     * @throws BusinessLogicException
     */
    public ICallback getCallback() throws BusinessLogicException;

    /**
     * get tag
     *
     * @return
     */
    public Object getTag();

    /**
     * set tag
     *
     * @param obj
     */
    public void setTag(Object obj);

    public Method getMethod();

    public Object getObject();
}
