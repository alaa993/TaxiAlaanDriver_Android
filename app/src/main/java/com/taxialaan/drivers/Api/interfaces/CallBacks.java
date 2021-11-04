package com.taxialaan.drivers.Api.interfaces;


import com.taxialaan.drivers.Api.utils.RequestException;

public interface CallBacks<T> {
    void onSuccess(T t);

    void onFail(RequestException e);
}