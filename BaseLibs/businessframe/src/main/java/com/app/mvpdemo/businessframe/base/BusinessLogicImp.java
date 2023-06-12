package com.app.mvpdemo.businessframe.base;

import android.content.Context;

import com.app.mvpdemo.businessframe.manager.ModuleManager;
import com.app.mvpdemo.util.log.LogUtil;

/**
 * 类功能描述
 *
 * @author weiwen
 * @since  2022/9/30 12:00 下午
 */
abstract public class BusinessLogicImp implements IBusinessLogic {

    private static final String TAG = "BusinessLogicImp";
    private Context context;
    private ModuleManager mModuleManager;

    @Override
    public void onCreate(Context context) {
        this.context = context;
    }


    public Context getContext() {
        return context;
    }

    @Override
    public void setModuleManager(ModuleManager moduleManager) {
        LogUtil.d(TAG, "setModuleManager() moduleManager : ", moduleManager);
        mModuleManager = moduleManager;
    }


    /**
     * unbind the module
     *
     * @param module
     * @throws BusinessLogicException
     */
    public void unBindModule(ILogicModule module) throws BusinessLogicException {
        if (null == mModuleManager) {
            throw new BusinessLogicException(0, "mModuleManager is null");
        }
        mModuleManager.unBindModule(module);
    }

    /**
     * bind the module
     *
     * @return
     * @throws BusinessLogicException
     */
    public <T extends ILogicModule> T bindModule(Class<T> moduleClass)
            throws BusinessLogicException {
        if (null == mModuleManager) {
            throw new BusinessLogicException(0, "mModuleManager is null");
        }
        return mModuleManager.bindModule(moduleClass);
    }

    /**
     * get the unique id
     * @return
     */
    private String getId() {
        return String.valueOf(this.hashCode());
    }
}
