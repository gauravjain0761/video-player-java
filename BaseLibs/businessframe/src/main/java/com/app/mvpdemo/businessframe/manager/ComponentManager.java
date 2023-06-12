package com.app.mvpdemo.businessframe.manager;

import android.content.Context;
import android.text.TextUtils;

import com.app.mvpdemo.businessframe.base.BusinessLogicException;
import com.app.mvpdemo.businessframe.base.IBusinessLogic;
import com.app.mvpdemo.businessframe.base.ICallback;
import com.app.mvpdemo.util.log.LogUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 类功能描述
 *
 * @author weiwen
 * @since  2022/9/30 12:15 下午
 */
public class ComponentManager {

    private static final String TAG = "ComponentManager";
    private Map<String, IBusinessLogic> mLogicMap = new HashMap<String, IBusinessLogic>();
    //private List<ComponentEntity> mComponentList;
    private Context mContext;
    private ModuleManager mModuleManager;

    public ComponentManager(Context context) {
        mContext = context;
        LogUtil.d(TAG, "ComponentManager() mComponentConfigure : ");
        //create module manager
        mModuleManager = ModuleManager.getInstance().init(context);
    }

    /**
     * process the function when PlatEngineService destroy
     */
    public void onDestroy() {
        for (Map.Entry<String, IBusinessLogic> entry : mLogicMap.entrySet()) {
            removeLogic(entry.getValue());
        }
        mLogicMap.clear();
        LogUtil.d(TAG, "onDestroy()");
    }

    /**
     * get business logic
     *
     * @param componentName
     * @return
     * @throws BusinessLogicException
     */
    public IBusinessLogic getBusinessLogic(String componentName) throws BusinessLogicException {
        IBusinessLogic logic = getComponent(componentName);
        if (null == logic) {
            throw new BusinessLogicException(ICallback.ResultCode.NOT_FIND_COMPONENT,
                    ICallback.ResultCode.getResultMessage(ICallback.ResultCode.NOT_FIND_COMPONENT));
        }
        return logic;
    }

    /**
     * un bind logic
     *
     * @param name
     * @throws BusinessLogicException
     */
    public void unBindLogic(String name) throws BusinessLogicException {
        IBusinessLogic logic = mLogicMap.remove(name);
        if (null == logic) {
            throw new BusinessLogicException(ICallback.ResultCode.NOT_FIND_COMPONENT,
                    ICallback.ResultCode.getResultMessage(ICallback.ResultCode.NOT_FIND_COMPONENT));
        }
        removeLogic(logic);
    }

    private void removeLogic(IBusinessLogic logic) {
        if (null == logic) {
            return;
        }
        logic.onDestroy();
    }

    private IBusinessLogic getComponent(String componentName) {
        IBusinessLogic logic = mLogicMap.get(componentName);
        if (null == logic) {
            logic = create(componentName);
            if (null != logic) {
                mLogicMap.put(componentName, logic);
            }
        }
        return logic;
    }

    /**
     * must process thread synchronized
     *
     * @param name
     * @return
     */
    private synchronized IBusinessLogic create(String name) {
        if (TextUtils.isEmpty(name)){
            return null;
        }
        IBusinessLogic logic = mLogicMap.get(name);
        if (null != logic) {
            return logic;
        }
        try {
            Class classType = Class.forName(name);
            Object obj = classType.newInstance();
            if (obj instanceof IBusinessLogic) {
                logic = (IBusinessLogic) obj;
                logic.setModuleManager(mModuleManager);
                logic.onCreate(mContext);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return logic;
    }
}
