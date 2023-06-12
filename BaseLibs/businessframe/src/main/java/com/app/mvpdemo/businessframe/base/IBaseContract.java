package com.app.mvpdemo.businessframe.base;


import androidx.annotation.IntDef;

/**
 * Created by weiwen.
 */

public interface IBaseContract {

    public class Constant {

        public static final int ANDROID_FRAMEWORK_MANAGE = 1;
        public static final int CUSTOMER_MANAGE = 2;

        // 自定义一个注解MyState
        @IntDef({ ANDROID_FRAMEWORK_MANAGE, CUSTOMER_MANAGE }) public @interface LifeCycleManageMode {

        }
    }

    interface IView {

        /**
         * on the time when the view has been visible
         */
        void onVisible();

        /**
         * on the time when the view has been invisible
         */
        void onInvisible();

        /**
         * on the time when the view will be visible
         */
        void onWillVisible();

        /**
         * on the time when the view will be invisible
         */
        void onWillInvisible();

        /**
         * @return current view pause refresh
         */
        boolean isPauseRefresh();

        /**
         * set the parameter when changing current fragment
         *
         * @param parameter
         */
        void setParameter(Object parameter);

        /**
         * callback on created
         */
        void onCreated();

        /**
         * callback on will destroy
         */
        void onWillDestroy();

        /**
         * set lifecycle manage mode
         *
         * @param mode
         */
        void setLifeCycleManageMode(@Constant.LifeCycleManageMode int mode);
    }

    /**
     *
     */
    interface IPresenter {

        /**
         * cache the data from callback maybe because the fragment will be invisible
         *
         * @param type
         * @param obj
         * @return if the view is showing and must not cache data ,return false;otherwise return true.
         */
        boolean cacheDataOnWillInvisible(int type, Object obj);

        /**
         * resume process data maybe because the fragment has been visible
         */
        void resumeProcessCacheDataOnVisible();

        /**
         * cancel process cache data on invisible
         */
        void cancelProcessCacheDataOnInvisible();
    }
}
