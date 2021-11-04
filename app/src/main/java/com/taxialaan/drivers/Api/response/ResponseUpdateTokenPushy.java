package com.taxialaan.drivers.Api.response;

import com.google.gson.annotations.SerializedName;

public class ResponseUpdateTokenPushy {

    @SerializedName("message")
    private String message;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
