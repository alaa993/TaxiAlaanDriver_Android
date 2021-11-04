package com.taxialaan.drivers.Api.interfaces;


public interface DownloadCallBacks<T> {
    void onSuccess(T t);

    void onProgress(int percent);

    void onFail(Exception e);
}