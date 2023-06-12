package com.app.videoplayer.db;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import com.app.videoplayer.entity.SongEntity;
import com.app.videoplayer.utils.FileUtils1;

import java.util.ArrayList;
import java.util.List;

public class FetchSongsFromLocal {

    public static List<SongEntity> getAllSongs(Context context) {
        List<SongEntity> songs = new ArrayList();
        SongCursorWrapper cursor = queryAllSong(context, null, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    SongEntity song = cursor.getSong(context);
                    song.setAlbumArt("" + getAlbumUri(song.getAlbumId()).toString());
                    songs.add(song);
                } while (cursor.moveToNext());
            } else {
                Log.e("TAG", "cursor else called ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return songs;
    }

    static SongCursorWrapper queryAllSong(Context context, String whereClause, String[] whereArgs) {
        String sortOrder = MediaStore.Video.Media.TITLE + " ASC";
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, whereArgs, sortOrder);
        if (cursor == null) Log.e("TAG", "cursor is null called ");
        return new SongCursorWrapper(cursor);
    }

    static Uri getAlbumUri(int albumId) {
        Uri albumArtUri = Uri.parse("content://media/external/video/albumart");
        return ContentUris.withAppendedId(albumArtUri, albumId);
    }


    public static List<SongEntity> getSelectedSongs(Context context, String path) {
        List<SongEntity> songs = new ArrayList();
        SongCursorWrapper cursor = querySelectedSong(context, path);
        try {
            if (cursor.moveToFirst()) {
                do {
                    SongEntity song = cursor.getSong(context);
                    song.setAlbumArt("" + getAlbumUri(song.getAlbumId()).toString());
                    songs.add(song);
                } while (cursor.moveToNext());
            } else {
                Log.e("TAG", "cursor else called ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return songs;
    }

    public static List<SongEntity> getSelectedSongs(Context context, Uri uri) {
        List<SongEntity> songs = new ArrayList();
        SongCursorWrapper cursor = null;
        try {
            cursor = new SongCursorWrapper(context.getContentResolver().query(uri, null, null, null, ""));
            if (cursor.moveToFirst()) {
                do {
                    SongEntity song = cursor.getSong(context);
                    song.setAlbumArt("" + getAlbumUri(song.getAlbumId()).toString());
                    songs.add(song);
                } while (cursor.moveToNext());
            } else {
                Log.e("TAG", "cursor else called ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return songs;
    }

    static SongCursorWrapper querySelectedSong(Context context, String path) {
        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }
        MergeCursor cursor = new MergeCursor(new Cursor[]{
                context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Video.Media.DATA + " = ?", new String[]{path}, ""),
                context.getContentResolver().query(MediaStore.Video.Media.INTERNAL_CONTENT_URI, null, MediaStore.Video.Media.DATA + " = ?", new String[]{path}, ""),
                context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Audio.Media.DATA + " = ?", new String[]{path}, ""),
                context.getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, null, MediaStore.Audio.Media.DATA + " = ?", new String[]{path}, "")});
        //Cursor cursor = context.getContentResolver().query(collection, null, MediaStore.Video.Media.DATA + " = ?", new String[]{path}, "");
        if (cursor == null) Log.e("TAG", "cursor is null called ");
        return new SongCursorWrapper(cursor);
    }
}