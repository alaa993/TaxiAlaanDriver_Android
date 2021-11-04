package com.taxialaan.drivers.Api.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentCheck {

    @SerializedName("error")
    @Expose
    private String error;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("need_amount")
    @Expose
    private String need_amount;


    @SerializedName("pay_method")
    @Expose
    private String pay_method;


    @SerializedName("trip_price")
    @Expose
    private String trip_price;

    @SerializedName("user")
    @Expose
    private String user;

    @SerializedName("amount_by_cash")
    @Expose
    private String amount_by_cash;


    @SerializedName("amount_by_wallet")
    @Expose
    private String amount_by_wallet;


    @SerializedName("transfer")
    @Expose
    private String transfer;

    public String getTransfer() {
        return transfer;
    }

    public void setTransfer(String transfer) {
        this.transfer = transfer;
    }


    public String getAmount_by_cash() {
        return amount_by_cash;
    }

    public void setAmount_by_cash(String amount_by_cash) {
        this.amount_by_cash = amount_by_cash;
    }

    public String getAmount_by_wallet() {
        return amount_by_wallet;
    }

    public void setAmount_by_wallet(String amount_by_wallet) {
        this.amount_by_wallet = amount_by_wallet;
    }


    public String getTrip_price() {
        return trip_price;
    }

    public void setTrip_price(String trip_price) {
        this.trip_price = trip_price;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }





    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNeed_amount() {
        return need_amount;
    }

    public void setNeed_amount(String need_amount) {
        this.need_amount = need_amount;
    }

    public String getPay_method() {
        return pay_method;
    }

    public void setPay_method(String pay_method) {
        this.pay_method = pay_method;
    }


}
