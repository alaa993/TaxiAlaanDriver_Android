package com.taxialaan.drivers.Api;

import android.content.Context;
import com.taxialaan.drivers.Api.interfaces.CallBack;
import com.taxialaan.drivers.Api.response.Default;
import com.taxialaan.drivers.Api.response.PaymentCheck;
import com.taxialaan.drivers.Api.response.Profile;
import com.taxialaan.drivers.Api.response.ResponseCheckPushy;
import com.taxialaan.drivers.Api.response.ResponseUpdateTokenPushy;
import com.taxialaan.drivers.Api.response.TransactionItem;
import com.taxialaan.drivers.Api.response.UserWalletDetail;
import com.taxialaan.drivers.Api.utils.RequestException;
import com.taxialaan.drivers.G;
import com.taxialaan.drivers.Helper.SharedHelper;
import com.taxialaan.drivers.Helper.URLHelper;
import com.taxialaan.drivers.R;
import com.taxialaan.drivers.Utilities.NotificationCenter;
import java.util.List;


public class Repository implements NetworkProvider {

    private static Repository INSTANCE;
    private NetworkRepository networkRepository;


    public static Repository getInstance() {
        if (INSTANCE == null) {
            synchronized (Repository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Repository();
                }
            }
        }
        return INSTANCE;
    }

    private Repository() {
        networkRepository = NetworkRepository.getInstance();
    }

    @Override
    public void chargeCode(String charge_code, CallBack<Default> call) {
        networkRepository.chargeCode(charge_code, call);
    }

    @Override
    public void requestMoney(int amount, CallBack<Default> call) {
        networkRepository.requestMoney(amount, call);
    }

    @Override
    public void transferCharge(int wallet_id, int amount, CallBack<Default> call) {
        networkRepository.transferCharge(wallet_id, amount, call);
    }

    @Override
    public void getTransactionList(CallBack<List<TransactionItem>> call) {
        networkRepository.getTransactionList(call);
    }

    @Override
    public void getUserWalletDetail(int wallet_id, CallBack<UserWalletDetail> call) {
        networkRepository.getUserWalletDetail(wallet_id, call);
    }

    @Override
    public void getProfile(final CallBack<Profile> call) {
        networkRepository.getProfile(new CallBack<Profile>() {
            @Override
            public void onSuccess(Profile profile) {
                super.onSuccess(profile);
                if (call != null) {
                    call.onSuccess(profile);
                }
                Context context = G.getInstance().getApplicationContext();
                SharedHelper.putKey(context, "id", profile.getId() + "");
                SharedHelper.putKey(context, "first_name", profile.getFirstName());
                SharedHelper.putKey(context, "last_name", profile.getLastName());
                SharedHelper.putKey(context, "email", profile.getEmail());
                SharedHelper.putKey(context, "sos", profile.getSos());
                // SharedHelper.putKey(context, "language", response.optString("language"));
                if (profile.getAvatar() != null && profile.getAvatar().startsWith("http"))
                    SharedHelper.putKey(context, "picture", profile.getAvatar());
                else
                    SharedHelper.putKey(context, "picture", URLHelper.base + "storage/" + profile.getAvatar());
                SharedHelper.putKey(context, "mobile", profile.getMobile());
                SharedHelper.putKey(context, "balance", profile.getBalance());
                SharedHelper.putKey(context, "wallet_id", profile.getWalletId());
                SharedHelper.putKey(context, "approval_status", profile.getStatus());
//                    SharedHelper.putKey(context, "wallet_balance", response.optString("wallet_balance"));
//                    SharedHelper.putKey(context, "payment_mode", response.optString("payment_mode"));
                SharedHelper.putKey(context, "loggedIn", context.getString(R.string.True));
                if (profile.getService() != null) {
                    try {
                        if (profile.getService().getServiceType() != null) {
                            SharedHelper.putKey(context, "service", profile.getService().getServiceType().getName());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateWalletAmount);
            }

            @Override
            public void onFail(RequestException e) {
                super.onFail(e);
                if (call != null) {
                    call.onFail(e);
                }

            }
        });
    }

    @Override
    public void paymentCheck(String amount, String request_id, CallBack<PaymentCheck> call) {

        networkRepository.paymentCheck(amount,request_id,call);
    }

    @Override
    public void checkPushy(CallBack<ResponseCheckPushy> callBack) {

        networkRepository.checkPushy(callBack);
    }

    @Override
    public void updateTokenPushy(String token, CallBack<ResponseUpdateTokenPushy> callBack) {

        networkRepository.updateTokenPushy(token,callBack);

    }



}
