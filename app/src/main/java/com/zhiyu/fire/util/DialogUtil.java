package com.zhiyu.fire.util;

import android.content.Context;
import android.support.v7.app.AlertDialog;

/**
 * Created by Administrator on 2017/6/14 0014.
 * Dialog工具类
 */

public class DialogUtil {

    public static void showDialog(final Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("确定", null);
        builder.create().show();
    }

}