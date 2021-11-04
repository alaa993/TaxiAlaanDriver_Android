package com.taxialaan.drivers.Api;

import com.taxialaan.drivers.Api.interfaces.CallBack;
import com.taxialaan.drivers.Api.request.LoginRequest;
import com.taxialaan.drivers.Api.request.RegisterRequest;
import com.taxialaan.drivers.Api.response.Default;
import com.taxialaan.drivers.Api.response.LoginResponse;
import com.taxialaan.drivers.Api.response.OTPResponse;
import com.taxialaan.drivers.Api.response.PaymentCheck;
import com.taxialaan.drivers.Api.response.Profile;
import com.taxialaan.drivers.Api.response.RegisterResponse;
import com.taxialaan.drivers.Api.response.ResponseCheckPushy;
import com.taxialaan.drivers.Api.response.ResponseUpdateTokenPushy;
import com.taxialaan.drivers.Api.response.TrackResponse;
import com.taxialaan.drivers.Api.response.TransactionItem;
import com.taxialaan.drivers.Api.response.UpdateResponse;
import com.taxialaan.drivers.Api.response.UserWalletDetail;
import com.taxialaan.drivers.Api.response.VerifyOTP;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {


    @FormUrlEncoded
    @POST("api/provider/charge_codes/apply")
    Call<Default> chargeCode(@Field("charge_code") String charge_code);

    @FormUrlEncoded
    @POST("api/provider/requests/withdraw")
    Call<Default> requestMoney(@Field("amount") int amount);

    @FormUrlEncoded
    @POST("api/provider/transfer")
    Call<Default> transferCharge(@Field("wallet_id") int wallet_id, @Field("amount") int amount);


    @POST("api/provider/reports/transactions")
    Call<List<TransactionItem>> getTransactionList();

    @FormUrlEncoded
    @POST("api/provider/wallet/details")
    Call<UserWalletDetail> getUserWalletDetail(@Field("wallet_id") int wallet_id);

    @GET("api/provider/profile")
    Call<Profile> getProfile();

    @FormUrlEncoded
    @POST("api/provider/verify")
    Call<OTPResponse> getOTP(@Field("mobile") String mobile_number);

    @FormUrlEncoded
    @POST("api/provider/verify/pin")
    Call<VerifyOTP> verifyOTP(@Field("otp") String otp, @Field("request_id") String request_id);

    @POST("api/provider/login")
    Call<LoginResponse> Login(@Body LoginRequest request);

    @POST("api/provider/register")
    Call<RegisterResponse> Register(@Body RegisterRequest request);

    @FormUrlEncoded
    @POST("api/provider/update_location")
    Call<TrackResponse> updateLocation(@Field("latitude") Double latitude, @Field("longitude") Double longitude);


    @FormUrlEncoded
    @POST("api/update/check/android")
    Call<UpdateResponse> updateVersionApp(@Field("app") String app);

    @FormUrlEncoded
    @POST("api/provider/payment/check")
    Call<PaymentCheck>paymentCheck(@Field("cash_amount") String amount,@Field("request_id") String request_id);

    @GET("api/provider/push_token/check")
    Call<ResponseCheckPushy> checkPushy();


    @FormUrlEncoded
    @POST("api/provider/push_token/update")
    Call<ResponseUpdateTokenPushy> updateTokenPushy(@Field("token") String token);

}
