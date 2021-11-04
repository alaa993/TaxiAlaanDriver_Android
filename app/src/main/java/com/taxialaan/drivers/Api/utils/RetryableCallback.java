package com.taxialaan.drivers.Api.utils;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class RetryableCallback<T> implements Callback<T> {

    private static final String TAG = RetryableCallback.class.getSimpleName();

    private int maxRetry      = 0;
    private int maxTimerRetry = 4;

    public static final int DEFAULT       = 2;
    private             int timerLoopTime = 3000;

    private int retryCount    = 0;
    private int timerTryCount = 0;

    private Timer   timer;
    private Call<T> call;


    public RetryableCallback() {

    }


    public RetryableCallback(int maxRetry) {
        this.maxRetry = maxRetry;
    }


    public RetryableCallback(int maxRetry, int maxTimerRetry) {
        this.maxRetry = maxRetry;
        this.maxTimerRetry = maxTimerRetry;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        timeOutFun(null);
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onFailure(final Call<T> call, Throwable t) {
        Log.e(TAG, t.getMessage() + "");
        timeOutFun(t);
        this.call = call;
        if (retryCount++ < maxRetry) {
            Log.v(TAG, "Retrying... (" + retryCount + " out of " + maxRetry + ")");
            retry();
        } else {
            if (maxRetry > 0 && timer == null) {
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        timerTryCount++;
                        if (timerTryCount < maxTimerRetry) {
                            Log.v(TAG, "Retrying Timer ... (" + timerTryCount + ")");
                            retry();
                        } else {
                            if (timer != null) {
                                timer.cancel();
                                timer = null;
                            }
                        }
                    }
                }, timerLoopTime, timerLoopTime);
            }
        }
    }

    private void retry() {
        call.clone().enqueue(this);
    }

    private void timeOutFun(final Throwable t) {
        /*Utils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (t == null) {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.DisconnectNet, false);
                    return;
                }
                if (t instanceof java.net.SocketException || t instanceof java.net.SocketTimeoutException || t instanceof UnknownHostException) {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.DisconnectNet, true);
                }
            }
        });*/
    }


}