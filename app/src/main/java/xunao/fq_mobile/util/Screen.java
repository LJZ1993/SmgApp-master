package xunao.fq_mobile.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;

/** 屏幕相关类 */
public class Screen {
	/* ldpi 240 0.75 */
	/* mdpi 320 1 */
	/* hdpi 480 1.5 */
	/* xhdpi 720 2 */
	/* dp：无视分辨率把屏幕分成宽320份，高480份，1dp占1份 */
	/* px：按分辨率比例显示 */

    public static class Data {
        public int px;
        public float dp;

        public Data(int px, float density) {
            this.px = px;
            this.dp = px / density;
        }
    }

    /** 获取屏幕 */
    public static Display getDisplay(Context context) {
        Display d = ((Activity) context).getWindowManager().getDefaultDisplay();
        return d;
    }

    /** 屏幕宽度 */
    public static Data width(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        getDisplay(context).getMetrics(dm);
        return new Data(dm.widthPixels, dm.density);
    }

    /** 屏幕高度 */
    public static Data height(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        getDisplay(context).getMetrics(dm);
        return new Data(dm.heightPixels, dm.density);
    }

    /** 根据屏幕尺寸改变数据，传入变量为720p下px值 */
    public static int rate(Context context, float num) {
        return dip2px(context, (num / 2 + 0.5f));
    }

    /** 将dp值转化为px */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /** px转dp */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static void show(Context context) {
        Out.showToast(context, "width:" + Screen.width(context).dp + "(dp) ," + Screen.width(context).px + "(px)\nheight:" + Screen.height(context).dp + "(dp) ," + Screen.height(context).px + "(px)");
        Out.log("width:" + Screen.width(context).dp + "(dp) ," + Screen.width(context).px + "(px)");
        Out.log("height:" + Screen.height(context).dp + "(dp) ," + Screen.height(context).px + "(px)");
    }
}
