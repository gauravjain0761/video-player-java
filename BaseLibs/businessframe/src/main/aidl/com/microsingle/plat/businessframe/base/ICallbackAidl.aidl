// ICallbackAidl.aidl
package com.microsingle.plat.businessframe.base;
import android.os.Bundle;
//import android.os.Parcelable;
//import java.lang.Object;
//import java.util.Map;
import com.microsingle.plat.businessframe.base.IRequestAidl;
//import com.ju.plat.businessframe.base.CarrierDeclare any non-default types here with import statements

interface ICallbackAidl {
    /**
     * call back when succeed
     *
     * @param data
     * @param request
     */
    void onSuccess(in Map data, IRequestAidl request);

    /**
     * call back when fail
     *
     * @param errorCode
     * @param errorMsg
     * @param request
     */
    void onFailure(int errorCode, String errorMsg, IRequestAidl request);

    /**
     * call back the data of progress
     *
     * @param data
     * @param request
     */
    void onProgress(in Map data, IRequestAidl request);
}
