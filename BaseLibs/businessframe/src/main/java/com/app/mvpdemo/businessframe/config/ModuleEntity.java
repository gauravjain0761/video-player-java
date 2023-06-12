package com.app.mvpdemo.businessframe.config;


import java.io.Serializable;

/**
 * 类功能描述
 *
 * @author weiwen
 * @since  2022/10/3 12:06 下午
 */
public class ModuleEntity implements Serializable {

    public static final class LifeCycleTypes {

        public static final String DEFAULT = "default";
        public static final String SINGLE_TASK = "singleTask";
        public static final String SINGLE_INSTANCE = "singleInstance";
    }

    public static final class ExecuteTypes {

        public static final String SYNC = "sync";
        public static final String ASYNC = "async";
    }

    private String className;
    private String lifeCycleType;
    private String executeType;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getLifeCycleType() {
        return lifeCycleType;
    }

    public void setLifeCycleType(String lifeCycleType) {
        this.lifeCycleType = lifeCycleType;
    }

    public String getExecuteType() {
        return executeType;
    }

    public void setExecuteType(String executeType) {
        this.executeType = executeType;
    }

    @Override
    public String toString() {
        return "ModuleEntity{" + "className='" + className + '\'' + ", lifeCycleType='" + lifeCycleType + '\''
                + ", executeType='" + executeType + '\'' + '}';
    }
}
