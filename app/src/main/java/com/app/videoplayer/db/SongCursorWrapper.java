package com.app.videoplayer.db;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import com.app.videoplayer.R;
import com.app.videoplayer.entity.SongEntity;
import com.app.videoplayer.utils.ImageUtil;

public class SongCursorWrapper extends CursorWrapper {
    public SongCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public SongEntity getSong(Context context) {
        SongEntity songEntity = new SongEntity();
        try {
            int id = getInt(getColumnIndex(MediaStore.Video.Media._ID));
            String title = getString(getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
            String artistName = getString(getColumnIndex(MediaStore.Video.Media.ARTIST));
            String albumName = getString(getColumnIndex(MediaStore.Video.Media.ALBUM));
            String composer = getString(getColumnIndex(MediaStore.Video.Media.COMPOSER));
            String data = getString(getColumnIndex(MediaStore.Video.Media.DATA));
            int year = getInt(getColumnIndex(MediaStore.Video.Media.YEAR));
            long duration = getLong(getColumnIndex(MediaStore.Video.Media.DURATION));
            long dateModified = getLong(getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED));
            long dateAdded = getLong(getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
            long bookmark = getLong(getColumnIndex(MediaStore.Video.Media.BOOKMARK));
            long size = getLong(getColumnIndex(MediaStore.Video.Media.SIZE));
            String author = "", genreName = "";
            int genreId = 0;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    author = getString(getColumnIndex(MediaStore.Video.Media.AUTHOR));
                    genreName = getString(getColumnIndex(MediaStore.Video.Media.GENRE));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Log.e("TAG", "id " + id);
            Log.e("TAG", "Data : " + data);
            Log.e("VIDEO", "title " + title);

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            Bitmap songCoverBitmap = null;
            byte[] coverBytes = null;
            try {
                retriever.setDataSource(data);
                coverBytes = retriever.getEmbeddedPicture();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (coverBytes != null && coverBytes.length > 0)
                    songCoverBitmap = BitmapFactory.decodeByteArray(coverBytes, 0, coverBytes.length);
                else
                    songCoverBitmap = MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(), id,
                            MediaStore.Images.Thumbnails.FULL_SCREEN_KIND, null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (songCoverBitmap == null) {
                songCoverBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icv_songs);
            }

            songEntity.setGenreId(genreId);
            songEntity.setGenreName(genreName);
            songEntity.setSongId(id);
//            songEntity.setTrackNumber(trackNumber);
            songEntity.setYear(year);
            //          songEntity.setAlbumId(albumId);
            //        songEntity.setArtistId(artistId);
            songEntity.setDuration(duration);
            songEntity.setDateModified(dateModified);
            songEntity.setDateAdded(dateAdded);
            songEntity.setBookmark(bookmark);
            songEntity.setTitle("" + title);
            songEntity.setArtistName("" + artistName);
            songEntity.setComposer("" + composer);
            songEntity.setAlbumName("" + albumName);
            songEntity.setData("" + data);
            songEntity.setSize(size);
            songEntity.setBitmapCover(ImageUtil.convertToString(songCoverBitmap));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return songEntity;
    }
}
