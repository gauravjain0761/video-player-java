package com.app.videoplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.StrictMode;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.app.videoplayer.entity.DaoMaster;
import com.app.videoplayer.entity.DaoSession;
import com.app.videoplayer.utils.SPUtils;
import com.app.mvpdemo.businessframe.BusinessLogicManager;
import com.app.mvpdemo.businessframe.config.BusinessFrameConfig;

import org.greenrobot.greendao.database.Database;

public class AppController extends MultiDexApplication {
    static final String TAG = AppController.class.getSimpleName();

    private static AppController mApplication;
    private static final int THREAD_COUNT = 9;
    static SharedPreferences sp_searchSongInfo;
    static SharedPreferences sp_songInfo;
    public static DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            MultiDex.install(this);
            mApplication = this;

            initDataBase();
            initSharedPreferences();
            initBusiness();
            initStrictMode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initDataBase() {
        try {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "videoplayer-db");
            Database db = helper.getWritableDb();
            daoSession = new DaoMaster(db).newSession();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化StrictMode 防止系统分享文件报错
     */
    private void initStrictMode() {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化框架层参数
     */
    private void initBusiness() {
        BusinessFrameConfig businessFrameConfig = new BusinessFrameConfig.Builder().context(this).appName(getResources().getString(R.string.app_name)).threadCount(THREAD_COUNT).build(this);
        BusinessLogicManager.getInstance().initConstants(businessFrameConfig);
    }

    public static AppController getInstance() {
        return mApplication;
    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }

    private void initSharedPreferences() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                sp_searchSongInfo = EncryptedSharedPreferences.create(getApplicationContext(), SPUtils.SEARCH_SONG_INFO, new MasterKey.Builder(getApplicationContext()).setKeyGenParameterSpec(new KeyGenParameterSpec.Builder(MasterKey.DEFAULT_MASTER_KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).setKeySize(256).build()).build(), EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
            } else {
                sp_searchSongInfo = getApplicationContext().getSharedPreferences(SPUtils.SEARCH_SONG_INFO, Context.MODE_PRIVATE);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                sp_songInfo = EncryptedSharedPreferences.create(getApplicationContext(), SPUtils.SONG_INFO, new MasterKey.Builder(getApplicationContext()).setKeyGenParameterSpec(new KeyGenParameterSpec.Builder(MasterKey.DEFAULT_MASTER_KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).setKeySize(256).build()).build(), EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
            } else {
                sp_songInfo = getApplicationContext().getSharedPreferences(SPUtils.SONG_INFO, Context.MODE_PRIVATE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized SharedPreferences getSpSearchSongInfo() {
        return sp_searchSongInfo;
    }

    public static synchronized SharedPreferences getSpSongInfo() {
        return sp_songInfo;
    }
}