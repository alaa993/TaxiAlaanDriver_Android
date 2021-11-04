package com.taxialaan.drivers.Api;


import com.taxialaan.drivers.Api.interfaces.CallBack;
import com.taxialaan.drivers.Api.response.Default;
import com.taxialaan.drivers.Api.response.PaymentCheck;
import com.taxialaan.drivers.Api.response.Profile;
import com.taxialaan.drivers.Api.response.ResponseCheckPushy;
import com.taxialaan.drivers.Api.response.ResponseUpdateTokenPushy;
import com.taxialaan.drivers.Api.response.TransactionItem;
import com.taxialaan.drivers.Api.response.UserWalletDetail;

import java.util.List;

public interface NetworkProvider {

    void chargeCode(String charge_code, CallBack<Default> call);
    void requestMoney(int amount, CallBack<Default> call);

    void transferCharge(int wallet_id, int amount, CallBack<Default> call);

    void getTransactionList(CallBack<List<TransactionItem>> call);

    void getUserWalletDetail(int wallet_id, CallBack<UserWalletDetail> call);

    void getProfile(CallBack<Profile> call);

    void paymentCheck(String amount,String request_id,CallBack<PaymentCheck> call);

    void checkPushy(CallBack<ResponseCheckPushy> callBack);

    void updateTokenPushy(String token,CallBack<ResponseUpdateTokenPushy> callBack);

}
