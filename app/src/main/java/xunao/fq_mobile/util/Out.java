package xunao.fq_mobile.util;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import xunao.fq_mobile.R;

/** 提示类 */
@SuppressLint("NewApi")
@SuppressWarnings("deprecation")
public class Out {
    public static int defaultColor = Color.parseColor("#33b5e5");

    public static void log(Object obj) {
        Log.i(Info.getProjectName(), obj.toString());
    }

    public static void showToast(Context context, String title) {
        Toast myToast = Toast.makeText(context, title, Toast.LENGTH_LONG);
        myToast.setGravity(Gravity.CENTER, 0, 0);
        myToast.show();
    }

    public static void showMessgae(final Context context, String message) {
        final CustomDialog dialog = new CustomDialog(context, R.layout.dialog_message);
        Layout.setRelativeLayout(dialog.findViewById(R.id.body), Screen.width(context).px / 4 * 3, LayoutParams.WRAP_CONTENT, new int[] { RelativeLayout.CENTER_IN_PARENT });
        TextView diashow = (TextView) dialog.findViewById(R.id.diashow);
        diashow.setText(message);
        if (!((Activity) context).isFinishing()) {
            dialog.show();
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!((Activity) context).isFinishing()) {
                        dialog.dismiss();
                    }
                }
            }.start();
        }
    }

    public static void showByHandler(Handler handler, final Context context, final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                showMessgae(context, message);
            }
        });
    }
}