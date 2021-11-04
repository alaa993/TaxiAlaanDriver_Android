package com.taxialaan.drivers.Constants;

import android.view.View;

public interface ItemClickListener<T> {
    void itemClicked(View view, T t);
}
