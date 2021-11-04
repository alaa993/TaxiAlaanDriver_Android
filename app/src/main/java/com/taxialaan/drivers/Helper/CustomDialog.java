package com.taxialaan.drivers.Helper;

import android.app.Dialog;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.taxialaan.drivers.R;

public class CustomDialog extends Dialog {

    public CustomDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
    }

    public CustomDialog(Context context, String strMessage) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setIndeterminate(true);
        //setMessage(strMessage);
        //  setContentView(R.layout.custom_dialog);
    }
}
