package com.app.videoplayer.db;

import com.app.videoplayer.AppController;
import com.app.videoplayer.entity.SongEntity;
import com.app.videoplayer.entity.SongEntityDao;

import org.greenrobot.greendao.Property;

import java.util.List;

public class DBUtils {

    public interface TaskComplete {
        void onTaskComplete();
    }

    public static void insertSingleSongs(SongEntity song, TaskComplete taskComplete) {
        try {
            song.setIsChecked(false);
            song.setIsTrashed(false);
            if (!checkSongIsExistInDB(song.getSongId())) {
                AppController.getDaoSession().getSongEntityDao().save(song);
            } else {
                restoreSongIsExistInDB(song.getSongId());
            }
            taskComplete.onTaskComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertMultipleSongs(List<SongEntity> songs, TaskComplete taskComplete) {
        try {
            for (SongEntity song : songs) {
                song.setIsChecked(false);
                song.setIsTrashed(false);
                if (!checkSongIsExistInDB(song.getSongId())) {
                    AppController.getDaoSession().getSongEntityDao().save(song);
                } else {
                    restoreSongIsExistInDB(song.getSongId());
                }
            }
            taskComplete.onTaskComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateSingleSongs(SongEntity song, TaskComplete taskComplete) {
        try {
            AppController.getDaoSession().getSongEntityDao().update(song);
            taskComplete.onTaskComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateMultipleSongs(List<SongEntity> songs, TaskComplete taskComplete) {
        try {
            for (SongEntity song : songs) {
                AppController.getDaoSession().getSongEntityDao().update(song);
            }
            taskComplete.onTaskComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void trashMultipleSongs(List<SongEntity> songs, TaskComplete taskComplete) {
        try {
            for (SongEntity song : songs) {
                song.setIsChecked(false);
                song.setIsTrashed(true);
                AppController.getDaoSession().getSongEntityDao().update(song);
            }
            taskComplete.onTaskComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void trashSingleSongs(SongEntity song, TaskComplete taskComplete) {
        try {
            song.setIsChecked(false);
            song.setIsTrashed(true);
            AppController.getDaoSession().getSongEntityDao().delete(song);
            taskComplete.onTaskComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void restoreMultipleSongs(List<SongEntity> songs, TaskComplete taskComplete) {
        try {
            for (SongEntity song : songs) {
                song.setIsChecked(false);
                song.setIsTrashed(false);
                AppController.getDaoSession().getSongEntityDao().update(song);
            }
            taskComplete.onTaskComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void restoreSingleSongs(SongEntity song, TaskComplete taskComplete) {
        try {
            song.setIsChecked(false);
            song.setIsTrashed(false);
            AppController.getDaoSession().getSongEntityDao().update(song);
            taskComplete.onTaskComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteMultipleSongs(List<SongEntity> songs, TaskComplete taskComplete) {
        try {
            for (SongEntity song : songs) {
                AppController.getDaoSession().getSongEntityDao().delete(song);
            }
            taskComplete.onTaskComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteSingleSongs(SongEntity song, TaskComplete taskComplete) {
        try {
            AppController.getDaoSession().getSongEntityDao().delete(song);
            taskComplete.onTaskComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteAllSongs(SongEntity song, TaskComplete taskComplete) {
        try {
            AppController.getDaoSession().getSongEntityDao().deleteAll();
            taskComplete.onTaskComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean checkSongIsTrashedInDB(int songId) {
        if (AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.SongId.eq(songId), SongEntityDao.Properties.IsTrashed.eq(false)).list().size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkSongIsExistInDB(int songId) {
        if (AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.SongId.eq(songId)).list().size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static void restoreSongIsExistInDB(int songId) {
        restoreMultipleSongs(AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.SongId.eq(songId)).list(), new TaskComplete() {
            @Override
            public void onTaskComplete() {

            }
        });
    }

    public static List<SongEntity> getAllSongByNameAsc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(false)).orderAsc(SongEntityDao.Properties.Title).list();
    }

    public static List<SongEntity> getAllSongs() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(false)).list();
    }

    public static List<SongEntity> getSearchSongsByName(String searchText, String searchType, Property property) {
        if (searchType.equals("ASC")) {
            return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.Title.like("%" + searchText + "%"), SongEntityDao.Properties.IsTrashed.eq(false)).orderAsc(property).list();
        } else {
            return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.Title.like("%" + searchText + "%"), SongEntityDao.Properties.IsTrashed.eq(false)).orderDesc(property).list();
        }
    }

    public static List<SongEntity> getSearchTrashSongsByName(String searchText) {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.Title.like("%" + searchText + "%"), SongEntityDao.Properties.IsTrashed.eq(true)).list();
    }

    public static List<SongEntity> getAllSongByNameDesc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(false)).orderDesc(SongEntityDao.Properties.Title).list();
    }

    public static List<SongEntity> getAllSongByDateAsc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(false)).orderAsc(SongEntityDao.Properties.DateAdded).list();
    }

    public static List<SongEntity> getAllSongByDateDesc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(false)).orderDesc(SongEntityDao.Properties.DateAdded).list();
    }

    public static List<SongEntity> getAllSongByDurationAsc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(false)).orderAsc(SongEntityDao.Properties.Duration).list();
    }

    public static List<SongEntity> getAllSongByDurationDesc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(false)).orderDesc(SongEntityDao.Properties.Duration).list();
    }

    public static List<SongEntity> getAllSongBySizeAsc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(false)).orderAsc(SongEntityDao.Properties.Size).list();
    }

    public static List<SongEntity> getAllSongBySizeDesc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(false)).orderDesc(SongEntityDao.Properties.Size).list();
    }

    public static List<SongEntity> getAllTrashSongs() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(true)).list();
    }

    public static List<SongEntity> getTrashedSongByNameAsc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(true)).orderAsc(SongEntityDao.Properties.Title).list();
    }

    public static List<SongEntity> getTrashedSongByNameDesc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(true)).orderDesc(SongEntityDao.Properties.Title).list();
    }

    public static List<SongEntity> getTrashedSongByDateAsc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(true)).orderAsc(SongEntityDao.Properties.DateAdded).list();
    }

    public static List<SongEntity> getTrashedSongByDateDesc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(true)).orderDesc(SongEntityDao.Properties.DateAdded).list();
    }

    public static List<SongEntity> getTrashedSongByDurationAsc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(true)).orderAsc(SongEntityDao.Properties.Duration).list();
    }

    public static List<SongEntity> getTrashedSongByDurationDesc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(true)).orderDesc(SongEntityDao.Properties.Duration).list();
    }

    public static List<SongEntity> getTrashedSongBySizeAsc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(true)).orderAsc(SongEntityDao.Properties.Size).list();
    }

    public static List<SongEntity> getTrashedSongBySizeDesc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(true)).orderDesc(SongEntityDao.Properties.Size).list();
    }
}