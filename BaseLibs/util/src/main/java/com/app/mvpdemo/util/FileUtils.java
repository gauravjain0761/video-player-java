package com.app.mvpdemo.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;


import com.app.mvpdemo.util.log.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;

/**
 * Created by weiwen.
 * 文件管理
 */

public class FileUtils {
    private static final String TAG = "FileUtils";
    private static final String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static String DATA_PATH = Environment.getDataDirectory().getPath();
    private static final String SD_STATE = Environment.getExternalStorageState();
    public static final String NAME = "msvoicerecorder";
    public static final String AUDIO_FILE_DIR = Environment.DIRECTORY_MUSIC;


    /**
     * 获取音频文件默认存储位置
     *
     * @param context
     * @return
     */
    public static String getAudioDefaultLocation(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return getAppPath(context);
        }
        String path = "";
        if (Environment.MEDIA_MOUNTED.equals(SD_STATE)) {
            //have external storage
            path = SD_PATH + File.separator + getAudioDefaultSubPath();
        } else {
            //do not have external storage
            if (context != null) {
                path = context.getFilesDir().getAbsolutePath() + File.separator + getAudioDefaultSubPath();
            }
        }

        return path;
    }

    public static String getAudioDefaultSubPath() {
        return AUDIO_FILE_DIR + File.separator + NAME + File.separator;
    }

    /**
     * 获取文件输出流
     *
     * @return
     */
    public static OutputStream getAudioFileOutputStream(Context context, String filePath, String subPath, String fileName) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                LogUtil.i(TAG, "subPath==", subPath, "--fileName==", fileName);
                //android 10及以上版本需要通过mediaStore访问公共存储空间
                ContentResolver contentResolver = context.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Audio.AudioColumns.RELATIVE_PATH,
                        subPath);
                // 指定文件名
                contentValues.put(MediaStore.Audio.AudioColumns.DISPLAY_NAME, fileName);
                // 指定文件的 mime（比如 image/jpeg, application/vnd.android.package-archive 之类的）
                contentValues.put(MediaStore.Audio.AudioColumns.MIME_TYPE, "audio/*");
                Uri uri = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues);
                LogUtil.i(TAG, "uri===", uri.getPath());
                return contentResolver.openOutputStream(uri);
            } else {
                return new FileOutputStream(new File(filePath));
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i(TAG, "e===", e.getMessage());
        }
        return null;
    }

    /**
     * 从uri拷贝文件到应用存储目录
     *
     * @param fileUri
     * @param filePath
     * @param fileSubPath
     */
    public static String copyFileFromUri(Context context, Uri fileUri, String filePath, String fileSubPath) {
        String totalFilePath = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(fileUri, null, null, null, null);
            if (cursor.moveToFirst()) {
                @SuppressLint("Range") String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                try {
                    InputStream is = contentResolver.openInputStream(fileUri);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.Audio.AudioColumns.RELATIVE_PATH,
                            fileSubPath);
                    // 指定文件名
                    contentValues.put(MediaStore.Audio.AudioColumns.DISPLAY_NAME, displayName);
                    // 指定文件的 mime（比如 image/jpeg, application/vnd.android.package-archive 之类的）
                    contentValues.put(MediaStore.Audio.AudioColumns.MIME_TYPE, "audio/*");
                    Uri uri = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues);
                    OutputStream fos = contentResolver.openOutputStream(uri);
                    android.os.FileUtils.copy(is, fos);
                    totalFilePath = filePath + displayName;
                    fos.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                String realPath = getRealPath(context, fileUri);
                File originFile = new File(realPath);
                FileInputStream in = new FileInputStream(originFile);
                String sub1 = realPath.substring(realPath.lastIndexOf(File.separator) + 1);
                FileOutputStream out = new FileOutputStream(new File(filePath + sub1));
                byte[] bt = new byte[1024];
                int c;
                while ((c = in.read(bt)) > 0) {
                    out.write(bt, 0, c);
                }
                totalFilePath = filePath + sub1;
                in.close();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return totalFilePath;
    }

    public static String getAppPath(Context context) {
        if (context == null) {
            LogUtil.i(TAG, "context is null");
            return "";
        }
        try {
            File file = getAppDataFile(context);
            if (file != null) {
                return file.getAbsolutePath() + File.separator + NAME + File.separator;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "/data/user/0/" + NAME + "/";
    }

    public static File getAppDataFile(Context context) {
        if (context == null) {
            LogUtil.i(TAG, "context is null");
            return null;
        }
        try {
            File file = context.getExternalFilesDir(null);
            String SD_STATE = null;
            if (file != null) {
                SD_STATE = Environment.getExternalStorageState(file);
            }
            if (Environment.MEDIA_MOUNTED.equals(SD_STATE)) {
                return file;
            } else {
                return context.getFilesDir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 文件删除
     *
     * @param filePath
     * @return
     */
    public static boolean deleteFile(Context context, String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.isFile()) {
                return file.delete();
            } else {
                String[] filePaths = file.list();
                if (filePaths != null) {
                    for (String path : filePaths) {
                        deleteFile(context, filePath + File.separator + path);
                    }
                }
                return file.delete();
            }
        }
        return false;
    }

    /**
     * Safely delete files.Prevents the system from reporting an error when a file is re-created after being deleted. open failed: EBUSY (Device or resource busy)
     *
     * @param file File
     * @return true  false
     */
    public static boolean deleteFileSafely(File file) {
        if (file != null) {
            String tmpPath = file.getParent() + File.separator + System.currentTimeMillis();
            File tmp = new File(tmpPath);
            boolean renameTo = file.renameTo(tmp);
            if (!renameTo) {
                LogUtil.i(TAG, "deleteFileSafely file.renameTo fail!");
            }
            return tmp.delete();
        }
        return false;
    }

    /**
     * 截取文件名字
     *
     * @param filePath
     * @return
     */
    public static String getOrgName(String filePath) {
        String name = "fileApi";
        if (TextUtils.isEmpty(filePath)) {
            return name;
        }
        int lastIndexOf = filePath.lastIndexOf("/");
        if (lastIndexOf > -1) {
            name = filePath.substring(lastIndexOf + 1);
        }
        int dotIndex = name.lastIndexOf(".");
        if (dotIndex > -1) {
            name = name.substring(0, dotIndex);
        }
        return name;
    }

    /**
     * 获取指定文件大小
     *
     * @param
     * @return
     * @throws Exception
     */
    public static long getFileSize(Context context, String s) {
        if (!TextUtils.isEmpty(s)) {
            File file = new File(s);
            long size = 0;
            try {
                if (file.exists()) {
                    FileInputStream fis = null;
                    fis = new FileInputStream(file);
                    size = fis.available();
                } else {
                    file.createNewFile();
                    Log.e("获取文件大小", "文件不存在!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return size;
        }
        return 0;
    }


    /**
     * 获取内存总大小 描述
     *
     * @param
     * @return
     */
    public static String getTotalInternalMemorySize() {
        //获取内部存储根⽬录
        File path = Environment.getDataDirectory();
        //系统的空间描述类
        StatFs stat = new StatFs(path.getPath());
        //每个区块占字节数
        long blockSize = stat.getBlockSizeLong();
        //区块总数
        long totalBlocks = stat.getBlockCountLong();
        return bytes2kb(totalBlocks * blockSize);
    }

    /**
     * 剩余存储空间 描述
     *
     * @param context
     * @return
     */
    public static String getEnvironmentMemoryDesc(Context context) {
        return bytes2kb(getEnvironmentMemory(context));
    }

    /**
     * 获取手机剩余存储空间
     *
     * @return
     */
    public static long getEnvironmentMemory(Context context) {
        File file = getAppDataFile(context);
        if (file != null && file.exists()) {
            try {
                StatFs dataFs = new StatFs(file.getAbsolutePath());
                return dataFs.getFreeBlocksLong() * dataFs.getBlockSizeLong();
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        } else {
            return 0;
        }
    }

    /**
     * 字节转换成kb 或者MB
     *
     * @param bytes
     * @return
     */
    public static String bytes2kb(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal gbByte = new BigDecimal(1024 * 1024 * 1024);
        float returnValue = filesize.divide(gbByte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        if (returnValue > 1)
            return (returnValue + "GB");
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        if (returnValue > 1)
            return (returnValue + "MB");
        BigDecimal kilobyte = new BigDecimal(1024);
        returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        return (returnValue + "KB");
    }

    /**
     * 计算手机剩余内存可录制的音频时长
     *
     * @param sampleRateInHz 采样率  44100Hz 单位Hz
     * @param channelConfig  精度 8 位
     * @return
     */
    public static String getVoiceRecordDuration(Context context, int sampleRateInHz, int channelConfig) {
        int memoryPerSeconds = sampleRateInHz * channelConfig / 8;
        long memory = getEnvironmentMemory(context);
        long seconds = memory / memoryPerSeconds;
        long min = seconds / 60;
        String desc = min + " minutes";
        if (min >= 60) {
            desc = min / 60 + "hours";
        }
        return desc;
    }

    /**
     * mp3格式 存储时长
     *
     * @param mp3Rate rate 32kbps
     * @return
     */
    public static String getVoiceRecordDurationMp3Format(Context context, int mp3Rate) {
        int memoryPerSeconds = mp3Rate * 1024 / 8;
        long memory = getEnvironmentMemory(context);
        long seconds = memory / memoryPerSeconds;
        long min = seconds / 60;
        String desc = min + " minutes";
        if (min >= 60) {
            desc = min / 60 + "hours";
        }
        return desc;
    }

    public static long getVoiceRecordDurationFormatLong(Context context, int mp3Rate) {
        int memoryPerSeconds = mp3Rate * 1024 / 8;
        long memory = getEnvironmentMemory(context);
        long seconds = memory / memoryPerSeconds;
        long min = seconds / 60;
        long desc = min;
        if (min >= 60) {
            desc = min / 60;
        }
        return desc;
    }


    /**
     * 调用文件选择管理器
     */
    public static void showChooseFileSelector(Activity context) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        context.startActivityForResult(intent, 0);
    }


    public static boolean isFile(final File file) {
        return file != null && file.exists() && file.isFile();
    }


    public static String findFullPath(String path) {
        String actualResult = "";
        path = path.substring(5);
        int index = 0;
        StringBuilder result = new StringBuilder("/storage");
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) != ':') {
                result.append(path.charAt(i));
            } else {
                index = ++i;
                result.append('/');
                break;
            }
        }
        for (int i = index; i < path.length(); i++) {
            result.append(path.charAt(i));
        }
        if (result.substring(9, 16).equalsIgnoreCase("primary")) {
            actualResult = result.substring(0, 8) + "/emulated/0/" + result.substring(17);
        } else {
            actualResult = result.toString();
        }
        return actualResult;
    }


    public static String getRealPath(Context context, Uri fileUri) {
        String realPath;
        if (Build.VERSION.SDK_INT < 19) {
            realPath = FileUtils.getRealPathFromURIBelowAPI19(context, fileUri);
        } else {
            realPath = FileUtils.getRealPathFromURIAPI19(context, fileUri);
        }
        return realPath;
    }

    @SuppressLint("NewApi")
    public static String getRealPathFromURIAPI19(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    if (split.length > 1) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    } else {
                        return Environment.getExternalStorageDirectory() + "/";
                    }
                } else {
                    return "storage" + "/" + docId.replace(":", "/");
                }

            } else if (isDownloadsDocument(uri)) {
                String fileName = getFilePath(context, uri);
                if (fileName != null) {
                    return Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
                }

                String id = DocumentsContract.getDocumentId(uri);
                if (id.startsWith("raw:")) {
                    id = id.replaceFirst("raw:", "");
                    File file = new File(id);
                    if (file.exists())
                        return id;
                }

                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    @SuppressLint("NewApi")
    public static String getRealPathFromURIBelowAPI19(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
            cursor.close();
        }
        return result;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public static String getFilePath(Context context, Uri uri) {

        Cursor cursor = null;
        final String[] projection = {
                MediaStore.MediaColumns.DISPLAY_NAME
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, null, null,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * 获取 视频 或 音频 时长
     *
     * @param path 视频 或 音频 文件路径
     * @return 时长 毫秒值
     */
    public static long getDuration(String path) throws IOException {
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        long duration = 0;
        try {
            if (path != null) {
                mmr.setDataSource(path);
            }
            String time = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            duration = Long.parseLong(time);
        } catch (Exception ex) {
        } finally {
            mmr.release();
        }
        return duration;
    }

    //fileName 为文件名称 返回true为存在
    public static boolean isFileExists(String fileName) {
        try {
            File f = new File(fileName);
            if (f.exists()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

}
