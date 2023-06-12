package com.app.mvpdemo.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * web相关工具类
 *
 * @author weiwen
 * @created 2022/3/28 6:40 下午
 */
class WebUtils {

    /**
     * 打开网页
     *
     * @param context
     * @param url
     */
    public static void openUrl(Context context, String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
