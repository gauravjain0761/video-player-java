package com.app.videoplayer.db;

import com.app.videoplayer.entity.SongEntity;
import com.app.videoplayer.entity.SongEntityDao;

import java.util.List;

public class DaoUtilsStore {

    private static final String TAG = "DaoUtilsStore";
    private CommonDaoUtils<SongEntity> msongEntityDaoUtils;

    /**
     * 在访问时创建单例
     */
    private static class SingletonHolder {
        private static final DaoUtilsStore INSTANCE = new DaoUtilsStore();
    }

    public static DaoUtilsStore getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private DaoUtilsStore() {
        SongEntityDao _ScanInfoDao = DbController.getInstance().getDaoSession().getSongEntityDao();
        msongEntityDaoUtils = new CommonDaoUtils<>(SongEntity.class, _ScanInfoDao);
    }

    public CommonDaoUtils<SongEntity> getScanDaoUtils() {
        return msongEntityDaoUtils;
    }

    /**
     * insert new user entity to db
     *
     * @param songEntity
     */
    public void insertSongEntity(SongEntity songEntity) {
        msongEntityDaoUtils.insert(songEntity);
    }

    public void deleteSongEntityById(long id) {
        msongEntityDaoUtils.deleteByKey(id);
    }

    public List<SongEntity> queryAllSongEntity() {
        return msongEntityDaoUtils.queryAll();
    }
}
