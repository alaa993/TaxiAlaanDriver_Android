package com.taxialaan.drivers.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.taxialaan.drivers.R;
import com.taxialaan.drivers.Utilities.MyBoldTextView;


public class CellSettingView extends LinearLayout {
    public CellSettingView(Context context) {
        super(context);
        init(context, null);
    }

    public CellSettingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CellSettingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CellSettingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    String text;
    int icon;

    ImageView imgIcon;
    MyBoldTextView txtName;
    LinearLayout parent;

    private void init(Context context, @Nullable AttributeSet attrs) {
        View view = inflate(context, R.layout.cell_setting, this);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CellSettingView, 0, 0);
            text = a.getString(R.styleable.CellSettingView_cell_text);
            icon = a.getResourceId(R.styleable.CellSettingView_cell_icon, 0);
            a.recycle();
        }
        imgIcon = view.findViewById(R.id.imgIcon);
        txtName = view.findViewById(R.id.txtName);
        parent = view.findViewById(R.id.parent);

        setIcon(icon);
        setText(text);

    }

    public void setText(String text) {
        txtName.setText(text);
    }

    public void setIcon(int icon) {
        imgIcon.setImageResource(icon);
    }

    public void setListener(OnClickListener listener) {
        parent.setOnClickListener(listener);
    }
}
