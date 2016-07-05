package xunao.fq_mobile.util;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

/** 布局调整类 */
public class Layout {
    /** ListView调整高度,在ListView添加新内容后使用,使ListView调整自身高度 */
    public static void adjustListView(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public static void setRelativeLayout(View view, int width, int height) {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
        view.setLayoutParams(lp);
    }

    public static void setRelativeLayout(View view, int width, int height, int left, int top, int right, int bottom) {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
        lp.setMargins(left, top, right, bottom);
        view.setLayoutParams(lp);
    }

    public static void setRelativeLayout(View view, int width, int height, int[] rules) {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
        for (int i = 0; i < rules.length; i++) {
            lp.addRule(rules[i]);
        }
        view.setLayoutParams(lp);
    }

    public static void setRelativeLayout(View view, int width, int height, int left, int top, int right, int bottom, int[] rules) {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
        lp.setMargins(left, top, right, bottom);
        for (int i = 0; i < rules.length; i++) {
            lp.addRule(rules[i]);
        }
        view.setLayoutParams(lp);
    }

    public static void setLinearLayout(View view, int width, int height, float weight) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height, weight);
        view.setLayoutParams(lp);
    }

    public static void setLinearLayout(View view, int width, int height, float weight, int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height, weight);
        lp.setMargins(left, top, right, bottom);
        view.setLayoutParams(lp);
    }

    public static void setFrameLayout(View view, int width, int height, int left, int top, int right, int bottom, int gravity) {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, height, gravity);
        lp.setMargins(left, top, right, bottom);
        view.setLayoutParams(lp);
    }

    public static void setAbsListView(View view, int width, int height) {
        AbsListView.LayoutParams ap = new AbsListView.LayoutParams(width, height);
        view.setLayoutParams(ap);
    }

    /** -1表示自适应 */
    public static void adjustImgInLinearLayout(ImageView iv, Bitmap bm, int limitWidth, int limitHeight, int gravity) {
        int bmWidth = bm.getWidth();
        int bmHeight = bm.getHeight();
        int trueWidth = limitWidth;
        int trueHeight = limitHeight;
        double bmRate = bmWidth * 1.0 / bmHeight;
        double limitRate = limitWidth * 1.0 / limitHeight;
        if (bmRate >= limitRate || limitHeight == -1) {
            trueHeight = (int) (limitWidth * bmHeight * 1.0 / bmWidth);
        } else if (bmRate < limitRate || limitWidth == -1) {
            trueWidth = (int) (limitHeight * bmWidth * 1.0 / bmHeight);
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(trueWidth, trueHeight, 0);
        lp.gravity = gravity;
        iv.setLayoutParams(lp);
    }

    public static void adjustImgRelativeLayout(ImageView iv, Bitmap bm, int limitWidth, int limitHeight, int[] rules) {
        int bmWidth = bm.getWidth();
        int bmHeight = bm.getHeight();
        int trueWidth = limitWidth;
        int trueHeight = limitHeight;
        double bmRate = bmWidth * 1.0 / bmHeight;
        double limitRate = limitWidth * 1.0 / limitHeight;
        if (bmRate >= limitRate || limitHeight == -1) {
            trueHeight = (int) (limitWidth * bmHeight * 1.0 / bmWidth);
        } else if (bmRate < limitRate || limitWidth == -1) {
            trueWidth = (int) (limitHeight * bmWidth * 1.0 / bmHeight);
        }
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(trueWidth, trueHeight);
        for (int i = 0; i < rules.length; i++) {
            lp.addRule(rules[i]);
        }
        iv.setLayoutParams(lp);
    }
}