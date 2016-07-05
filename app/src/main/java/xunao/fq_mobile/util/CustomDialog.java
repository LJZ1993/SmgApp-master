package xunao.fq_mobile.util;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import xunao.fq_mobile.R;

public class CustomDialog extends Dialog {

    public CustomDialog(Context context, int layout) {
        this(context, layout, R.style.Theme_Dialog);
    }

    public CustomDialog(Context context, int layout, int style) {
        super(context, style);
        setContentView(layout);
    }

    public void onClick(View v) {
        this.dismiss();
    }
}
