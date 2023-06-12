// IRequestAidl.aidl
package com.microsingle.plat.businessframe.base;

// Declare any non-default types here with import statements
//import android.os.Bundle;
//import java.util.Map;
import com.microsingle.plat.businessframe.base.ICallbackAidl;

interface IRequestAidl {

    /**
     * get request code
     **/
    int getRequestCode();

    /**
     * get the parameter of request
     **/
    Map getParam();

    /**
     * get callback function
     *
     * @return
     * @throws BusinessLogicException
     */
    ICallbackAidl getCallback();
}
