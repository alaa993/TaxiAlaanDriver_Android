package com.taxialaan.drivers.Api.response;

import com.google.gson.annotations.SerializedName;

public class ResponseCheckPushy {

    @SerializedName("token")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
