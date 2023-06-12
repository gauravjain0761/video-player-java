package com.app.mvpdemo.businessframe.base;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类功能描述
 *
 * @author weiwen
 * @since  2022/9/19 12:42 PM
 */
public class BusinessDataContainerManager {

    private final static String RESULT_DATA = "RESULT_DATA";
    private final static String REQUEST_PARAMETER = "REQUEST_PARAMETER";
    private final static String REQUEST_TAG = "REQUEST_TAG";

    /**
     * assemble data for aidl
     *
     * @param data
     * @return
     * @throws BusinessLogicException
     */
    public synchronized static Map assembleResult(Object data) throws BusinessLogicException {
        if (null == data || data instanceof Serializable || data instanceof Parcelable || data instanceof List ||
                data instanceof Map) {
            Map<String, Object> container = new HashMap<>();
            container.put(RESULT_DATA, data);
            return container;
        } else {
            throw new BusinessLogicException(0, "data is not serializable or parcelable");
        }
    }

    /**
     * assemble bundle for aidl
     *
     * @param param
     * @param tag
     * @return
     */
    public synchronized static Map assembleRequest(Object param, Object tag) {
        Map<String, Object> container = new HashMap<>();
        if (null == param || param instanceof Serializable || param instanceof Parcelable || param instanceof List ||
                param instanceof Map) {
            container.put(REQUEST_PARAMETER, param);
        }

        if (null == tag || tag instanceof Serializable || tag instanceof Parcelable) {
            container.put(REQUEST_TAG, tag);

        }

        return container;
    }

    public synchronized static Object getResultData(Map container) throws BusinessLogicException {
        if (null == container) {
            throw new BusinessLogicException(0, "container is null");
        }
        return container.get(RESULT_DATA);
    }

    public synchronized static Object getRequestParameter(Map container) throws BusinessLogicException {
        if (null == container) {
            throw new BusinessLogicException(0, "container is null");
        }
        return container.get(REQUEST_PARAMETER);
    }

    public synchronized static Object getRequestTag(Map container) throws BusinessLogicException {
        if (null == container) {
            throw new BusinessLogicException(0, "container is null");
        }
        return container.get(REQUEST_TAG);
    }
}
