package com.app.mvpdemo.businessframe.manager;


import com.app.mvpdemo.businessframe.base.BusinessLogicException;
import com.app.mvpdemo.businessframe.config.ModuleEntity;
import com.app.mvpdemo.util.log.LogUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * module生命周期管理
 *
 * @author weiwen
 * @since  2022/10/3 12:03 下午
 */
public class ModuleLifeCycleManager {

    private static final String TAG = "ModuleLifeCycleManager";
    //the first String is module key,the second String is logic id
    private Map<String, Map<String, Boolean>> mReferenceMap;

    public ModuleLifeCycleManager() {
        this.mReferenceMap = new HashMap<>();
    }

    public void onDestroy() {
        mReferenceMap.clear();
        mReferenceMap = null;
        LogUtil.d(TAG, "onDestroy()");
    }

    /**
     * is destroy
     *
     * @param key
     * @param lifeCycleType
     * @return
     */
    public boolean isDestroy(String key, String lifeCycleType) {
        if (ModuleEntity.LifeCycleTypes.SINGLE_INSTANCE.equals(lifeCycleType)) {
            return false;
        }
        LogUtil.d(TAG, "isDestroy() key : ", key);
        Map<String, Boolean> map = mReferenceMap.get(key);
        if (null == map) {
            LogUtil.d(TAG, "isDestroy() map is null");
            return true;
        }

        for (Map.Entry<String, Boolean> entry : map.entrySet()) {
            LogUtil.d(TAG, "isDestroy() logicId : ", entry.getKey(), "   value : ", entry.getValue());
            if (entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    /**
     * get the key of module
     *
     * @param moduleClassName
     * @param logicId
     * @param lifeCycleType
     * @return
     */
    public String getKey(String moduleClassName, String logicId, String lifeCycleType) {
        String ret = moduleClassName;
        if (ModuleEntity.LifeCycleTypes.DEFAULT.equals(lifeCycleType)) {
            ret = moduleClassName + "-" + logicId;
        }
        return ret;
    }

    /**
     * register reference
     *
     * @param key
     * @param logicId
     */
    public void registerReference(String key, String logicId) {
        Map<String, Boolean> map = mReferenceMap.get(key);
        LogUtil.d(TAG, "registerReference() key : ", key, "   map : ", map);
        if (null == map) {
            map = new HashMap<String, Boolean>();
            mReferenceMap.put(key, map);
        }
        map.put(logicId, true);
    }

    /**
     * unregister reference
     *
     * @param key
     * @param logicId
     * @throws BusinessLogicException
     */
    public void unregisterReference(String key, String logicId) throws BusinessLogicException {
        Map<String, Boolean> map = mReferenceMap.get(key);
        LogUtil.d(TAG, "unregisterReference() key : ", key, "   map : ", map);
        if (null == map) {
            throw new BusinessLogicException(0, "when sub ,not find the reference " + key);
        }
        map.put(logicId, false);
    }
}
