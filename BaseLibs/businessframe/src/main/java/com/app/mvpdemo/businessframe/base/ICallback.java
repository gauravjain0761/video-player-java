package com.app.mvpdemo.businessframe.base;

import java.util.HashMap;
import java.util.Map;

/**
 * 类功能描述
 *
 * @author weiwen
 * @since  2022/9/19 10:52 AM
 */
public interface ICallback<T> {

    /**
     * call back when succeed
     *
     * @param data
     * @param request
     */
    void onSuccess(T data, IRequest request);

    /**
     * call back when fail
     *
     * @param errorCode
     * @param errorMsg
     * @param request
     */
    void onFailure(int errorCode, String errorMsg, IRequest request);

    /**
     * call back the data of progress
     *
     * @param data
     * @param request
     */
    void onProgress(Object data, IRequest request);

    public static final class ResultCode {

        public static final int SUCCESS = 0;
        public static final int PARAMETERS_IS_NULL = 10;
        public static final int PLAT_SERVICE_IS_NULL = 11;
        public static final int COMPONENT_NAME_IS_NULL = 12;
        public static final int REQUEST_IS_NULL = 13;
        public static final int CALLBACK_IS_NULL = 14;
        public static final int MAIN_THREAD_HANDLER_IS_NULL = 15;
        public static final int NOT_FIND_COMPONENT = 16;
        public static final int PLAT_SERVICE_INTENT_PARAMETER_INVALID = 17;
        public static final int PLAT_SERVICE_EXECUTOR_IS_NULL = 18;
        public static final int COMPONENT_EXECUTE_TYPE_INVALID = 19;
        public static final int REQUEST_CODE_INVALID = 20;
        public static final int PARAMETERS_INVALID = 21;
        public static final int NOT_FIND_DATA = 22;
        public static final int NETWORK_ERROR = 23;
        public static final int PLAT_SERVICE_DISCONNECTED = -1999;

        private static Map<Integer, String> messageMap = new HashMap<Integer, String>();

        public static String getResultMessage(int resultCode) {
            return messageMap.get(resultCode);
        }

        static {
            messageMap.put(SUCCESS, "Success");
            messageMap.put(PARAMETERS_IS_NULL, "parameters is null");
            messageMap.put(PLAT_SERVICE_IS_NULL, "plat service is null");
            messageMap.put(COMPONENT_NAME_IS_NULL, "component name is null");
            messageMap.put(REQUEST_IS_NULL, "request is null");
            messageMap.put(CALLBACK_IS_NULL, "callback is null");
            messageMap.put(MAIN_THREAD_HANDLER_IS_NULL, "main thread handler is null");
            messageMap.put(NOT_FIND_COMPONENT, "not find component");
            messageMap.put(PLAT_SERVICE_INTENT_PARAMETER_INVALID, "plat service intent parameter is invalid");
            messageMap.put(PLAT_SERVICE_EXECUTOR_IS_NULL, "plat service mExecutorService is null");
            messageMap.put(COMPONENT_EXECUTE_TYPE_INVALID, "component execute type is invalid");
            messageMap.put(REQUEST_CODE_INVALID, "request code is invalid");
            messageMap.put(PARAMETERS_INVALID, "request parameter is invalid");
            messageMap.put(NOT_FIND_DATA, "not find data");
            messageMap.put(NETWORK_ERROR, "network error");
            messageMap.put(PLAT_SERVICE_DISCONNECTED, "plat service disconnected");
        }
    }
}
