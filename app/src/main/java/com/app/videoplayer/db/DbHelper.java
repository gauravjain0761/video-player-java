package com.app.videoplayer.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.app.videoplayer.entity.DaoMaster;
import com.app.videoplayer.entity.SongEntityDao;
import com.app.mvpdemo.util.log.LogUtil;

import org.greenrobot.greendao.database.Database;

/**
 * 数据库操作类
 *
 * @author wei
 * @date 2022/12/13 3:09 PM
 */
public class DbHelper extends DaoMaster.OpenHelper {

    private static final String TAG = "DbHelper";
    private static final int DB_VERSION_0 = 1000;
    private static final int DB_VERSION_1 = 1001;

    public DbHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        LogUtil.i(TAG, "onUpgrade----", oldVersion, "---", newVersion);
        if (oldVersion == DB_VERSION_0 && newVersion == DB_VERSION_1) {
            MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
                @Override
                public void onCreateAllTables(Database db, boolean ifNotExists) {
                    DaoMaster.createAllTables(db, ifNotExists);
                }

                @Override
                public void onDropAllTables(Database db, boolean ifExists) {
                    DaoMaster.dropAllTables(db, ifExists);
                }
            }, SongEntityDao.class);
        }
    }

    /**
     * 检查表字段是否存在
     *
     * @param db
     * @param tableName
     * @param column
     * @return
     */
    private boolean hasColumn(SQLiteDatabase db, String tableName, String column) {

        if (TextUtils.isEmpty(tableName) || TextUtils.isEmpty(column)) {
            return false;
        }
        Cursor cursor = null;
        try {
            cursor = db.query(tableName, null, null, null, null, null, null);
            if (null != cursor && cursor.getColumnIndex(column) != -1) {
                return true;
            }
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return false;
    }
}
