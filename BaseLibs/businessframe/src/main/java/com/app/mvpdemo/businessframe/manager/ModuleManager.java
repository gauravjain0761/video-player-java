package com.app.mvpdemo.businessframe.manager;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import com.app.mvpdemo.businessframe.base.BusinessLogicException;
import com.app.mvpdemo.businessframe.base.ILogicModule;
import com.app.mvpdemo.util.log.LogUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * module管理器
 *
 * @author weiwen
 * @since 2022/10/3 12:02 下午
 */
public class ModuleManager {

    private static final String TAG = "ModuleManager";
    private static final int MAX_COUNT_OF_EXECUTORS = 9;
    private Context mContext;
    private Map<String, ILogicModule> mModuleMap;
    private ModuleLifeCycleManager mLifeCycleManager;
    private HandlerThread mHandlerThread = null;
    private Handler mHandler = null;
    private ExecutorService mExecutorService = null;

    /**
     * 在访问时创建单例
     */
    private static class SingletonHolder {
        private static final ModuleManager INSTANCE = new ModuleManager();
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static ModuleManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public ModuleManager init(Context context) {
        mContext = context;
        //create callback Handler Thread
        mHandlerThread = new HandlerThread(TAG + "PlatEngine Process Module Callback Thread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        //new module map
        mModuleMap = new HashMap<>();
        //new Life cycle manager
        mLifeCycleManager = new ModuleLifeCycleManager();
        //create executors
        int count = 3;
        if (count > MAX_COUNT_OF_EXECUTORS) {
            count = MAX_COUNT_OF_EXECUTORS;
        } else {
            count = 1;
        }
        mExecutorService = Executors.newFixedThreadPool(count);
        return this;
    }

    /**
     *
     */
    public void onDestroy() {
        if (null != mHandler) {
            mHandlerThread.quit();
            mHandler = null;
            mHandlerThread = null;
        }
        mLifeCycleManager.onDestroy();

        for (Map.Entry<String, ILogicModule> entry : mModuleMap.entrySet()) {
            removeModule(entry.getValue());
        }
        mModuleMap.clear();
        LogUtil.d(TAG, "onDestroy()");
    }

    private void removeModule(ILogicModule module) {
        if (null == module) {
            return;
        }
        module.onDestroy();
    }

    /**
     * unbind the module
     *
     * @param module
     * @throws BusinessLogicException
     */
    public void unBindModule(ILogicModule module) throws BusinessLogicException {
        if (null == module) {
            throw new BusinessLogicException(0, "not find the module");
        }
        module.onDestroy();
        mModuleMap.remove(module.getClass().getName());
    }

    /**
     * bind the module
     *
     * @param moduleClass
     * @return
     * @throws BusinessLogicException
     */
    public <T extends ILogicModule> T bindModule(Class<T> moduleClass)
            throws BusinessLogicException {
        String moduleClassName = moduleClass.getName();
        LogUtil.d(TAG, "bindModule() moduleClassName : ", moduleClassName);
        ILogicModule module = mModuleMap.get(moduleClassName);
        if (null != module) {
            return (T) module;
        }

        T moduleNew = create(moduleClass);
        if (null == moduleNew) {
            throw new BusinessLogicException(0, "not find the module : " + moduleClassName);
        }

        return moduleNew;
    }

    /**
     * must process thread synchronized
     *
     * @param moduleClass
     * @return
     */
    private synchronized <T extends ILogicModule> T create(Class<T> moduleClass) {
        String className = moduleClass.getName();
        ILogicModule module = mModuleMap.get(className);
        if (null != module) {
            return (T) module;
        }
        T moduleNew = null;
        try {
            moduleNew = moduleClass.newInstance();
//                module.setModuleEntity(entity);
//                module.setRelyModules(relyModules);
            moduleNew.setChildThreadHandler(mHandler);
            moduleNew.setExecutorService(mExecutorService);
            moduleNew.onCreate(mContext);
            mModuleMap.put(className, moduleNew);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return moduleNew;
    }
}
