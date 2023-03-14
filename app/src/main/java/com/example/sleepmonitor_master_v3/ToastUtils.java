package com.example.sleepmonitor_master_v3;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;


/**
 * @author：frank
 * @date: 2018/7/4
 */
public class ToastUtils {
    private static Context context = MyApp.context();
    private static Toast toast;

    public static void showToast(CharSequence text) {
        try {
            text = TextUtils.isEmpty(text == null ? "" : text.toString()) ? "请检查您的网络！" : text;
            if (toast == null) {
                toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
            } else {
                toast.setText(text);
            }
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showToast2(CharSequence text) {
        try {
            text = TextUtils.isEmpty(text == null ? "" : text.toString()) ? "请检查您的网络！" : text;
            if (toast == null) {
                toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            } else {
                toast.setText(text);
            }
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
