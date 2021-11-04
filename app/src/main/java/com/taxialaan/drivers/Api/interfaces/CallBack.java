package com.taxialaan.drivers.Api.interfaces;


import com.taxialaan.drivers.Api.utils.RequestException;

public abstract class CallBack<T> implements CallBacks<T> {
    @Override
    public void onSuccess(T t) {

    }

    @Override
    public void onFail(RequestException e) {

    }
}
