package com.app.mvpdemo.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import java.util.UUID;

/**
 * common util
 */
public class CommonUtils {

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
            context.startActivity(intent);
        }
    }

    /**
     * 打开google play 进行评价
     *
     * @param context
     */
    public static void openGooglePlayForRating(Context context) {
        if (context == null) {
            return;
        }
        try {
            final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + appPackageName));
            intent.setPackage("com.android.vending");
            if (intent.resolveActivity(context.getPackageManager()) == null) {
                intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
            }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开gmail发送邮件
     *
     * @param context
     * @param title
     * @param content
     */
    public static void openGmail(Context context, String title, String content, String chooseTitle) {
        if (context == null) {
            return;
        }
        Uri uri = Uri.parse("mailto:support@microsingle.com");
        String[] tos = {"support@microsingle.com"};
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.setPackage("com.google.android.gm");
        intent.putExtra(Intent.EXTRA_EMAIL, tos);
        if (!TextUtils.isEmpty(title)) {
            intent.putExtra(Intent.EXTRA_SUBJECT, title);
        }
        if (!TextUtils.isEmpty(content)) {
            intent.putExtra(Intent.EXTRA_TEXT, content);
        }
        try {
            if (intent.resolveActivity(context.getPackageManager()) == null) {
                intent.setPackage(null);
                intent.setData(uri);
                intent = Intent.createChooser(intent, chooseTitle);
            }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 获取UUID字符串
     */
    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        String s = "";
        if (uuid != null) {
            s = uuid.toString().replace("-", "");
        }
        return s;
    }

}
