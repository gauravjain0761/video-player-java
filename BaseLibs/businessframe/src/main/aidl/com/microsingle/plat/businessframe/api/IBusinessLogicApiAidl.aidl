// IBusinessLogicApiAidl.aidl
package com.microsingle.plat.businessframe.api;

// Declare any non-default types here with import statements
import com.microsingle.plat.businessframe.base.IRequestAidl;
//import android.os.Bundle;
//import java.util.Map;

interface IBusinessLogicApiAidl {

    /**
     * read data from business logic
     *
     * @param componentName
     * @param request
     * @throws BusinessLogicException
     **/
    void get(String componentName, IRequestAidl request);

    /**
     * write data to business logic
     *
     * @param componentName
     * @param request
     * @throws BusinessLogicException
     **/
    void set(String componentName, IRequestAidl request);

    /**
     * read data from business logic synchronously
     *
     * @param componentName
     * @param request
     * @return
     * @throws BusinessLogicException
     */
    Map syncGet(String componentName, IRequestAidl request);

    /**
     * write data to business logic logic synchronously
     *
     * @param componentName
     * @param request
     * @return
     * @throws BusinessLogicException
     */
    Map syncSet(String componentName, IRequestAidl request);

    /**
     * register listener to business logic
     *
     * @param componentName
     * @param request
     * @throws BusinessLogicException
     **/
    void registerListener(String componentName, IRequestAidl request);

    /**
     * unregister listener from business logic
     *
     * @param componentName
     * @param request
     * @throws BusinessLogicException
     **/
    void unregisterListener(String componentName, IRequestAidl request);

    /**
     * you must unbind the business logic component when you never need it
     *
     * @param componentName
     * @throws BusinessLogicException
     */
    void unBindBusinessLogic(String componentName);

}
