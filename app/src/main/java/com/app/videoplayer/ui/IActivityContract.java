package com.app.videoplayer.ui;


import com.app.videoplayer.entity.SongEntity;
import com.app.mvpdemo.businessframe.base.IBasePresenter;
import com.app.mvpdemo.businessframe.base.IBasePresenterView;

import java.util.List;

/**
 * 播放契约类
 *
 * @author weiwen
 * @created 2022/5/16 3:57 下午
 */
public interface IActivityContract {
    interface IActivityPresenter extends IBasePresenter<IActivityView> {

        /**
         * @param songEntity
         */
        void addSong(SongEntity songEntity);

        void deleteSongById(long id);

        void selectSongsList();

    }

    interface IActivityView extends IBasePresenterView<IActivityPresenter> {
        void updateSongsList(List<SongEntity> list);

        void addSongs(SongEntity entity);

        void refreshView();

        void showListView();

        void showNoDataView();

        void notifyAdapter();

        void setListView();
    }
}
