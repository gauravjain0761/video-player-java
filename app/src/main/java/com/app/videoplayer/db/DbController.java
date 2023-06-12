package com.app.videoplayer.db;

import android.database.sqlite.SQLiteDatabase;

import com.app.videoplayer.AppController;
import com.app.videoplayer.entity.DaoMaster;
import com.app.videoplayer.entity.DaoSession;


/**
 * Database tool class
 */
public class DbController {

    /**
     * Helper
     */
    private DbHelper mHelper;
    /**
     * Database
     */
    private SQLiteDatabase db;
    /**
     * DaoMaster
     */
    private DaoMaster mDaoMaster;
    /**
     * DaoSession
     */
    private DaoSession mDaoSession;

    public static final String DB_NAME = "demo.db";

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    /**
     * Create a singleton when accessing
     */
    private static class SingletonHolder {
        private static final DbController INSTANCE = new DbController();
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static DbController getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 初始化
     */
    public DbController() {
        mHelper = new DbHelper(AppController.getInstance(), DB_NAME);
        mDaoMaster = new DaoMaster(getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
    }

    /**
     * 获取可读数据库
     */
    private SQLiteDatabase getReadableDatabase() {
        if (mHelper == null) {
            mHelper = new DbHelper(AppController.getInstance(), DB_NAME);
        }
        SQLiteDatabase db = mHelper.getReadableDatabase();
        return db;
    }

    /**
     * 获取可写数据库
     *
     * @return
     */
    private SQLiteDatabase getWritableDatabase() {
        if (mHelper == null) {
            mHelper = new DbHelper(AppController.getInstance(), DB_NAME);
        }
        SQLiteDatabase db = mHelper.getWritableDatabase();
        return db;
    }
}
