package com.taxialaan.drivers.Activity.wallet;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.taxialaan.drivers.Api.Repository;
import com.taxialaan.drivers.Api.interfaces.CallBack;
import com.taxialaan.drivers.Api.response.Default;
import com.taxialaan.drivers.Api.response.UserWalletDetail;
import com.taxialaan.drivers.Api.utils.RequestException;
import com.taxialaan.drivers.G;
import com.taxialaan.drivers.Helper.AppHelper;
import com.taxialaan.drivers.R;

public class ActivitySendMoney extends AppCompatActivity {
    ImageView backArrow;
    EditText edtPhoneNumber, edtAmount;
    Button apply_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_wallet_send_money);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        readView();
        functionView();
    }


    private void readView() {
        backArrow = findViewById(R.id.backArrow);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtAmount = findViewById(R.id.edtAmount);
        apply_button = findViewById(R.id.apply_button);
    }

    private void functionView() {
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        apply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = edtPhoneNumber.getText().toString();
                String amount = edtAmount.getText().toString();

                if (phone.isEmpty()) {
                    return;
                }
                if (amount.isEmpty()) {
                    return;
                }
                int walletId = Integer.parseInt(phone);
                int amounti = Integer.parseInt(amount);

                check(walletId, amounti);
            }
        });
    }

    ProgressDialog progressDialog;

    private void showLoading() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
    }

    private void hideLoading() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    private void check(final int walletId, final int amount) {
        showLoading();
        Repository.getInstance().getUserWalletDetail(walletId, new CallBack<UserWalletDetail>() {
            @Override
            public void onSuccess(UserWalletDetail userWalletDetail) {
                super.onSuccess(userWalletDetail);
                hideLoading();
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySendMoney.this);
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        send(walletId, amount);
                    }
                });
                String s = getString(R.string.are_you_sure_for_send_money_to) +
                           " " +
                           userWalletDetail.getFirst_name() +
                           " " +
                           userWalletDetail.getLast_name() +
                           "?";
                AppHelper.ShowMessageBox(builder, "User info", s);
            }

            @Override
            public void onFail(RequestException e) {
                super.onFail(e);
                hideLoading();
                if (e.getResponseCode() == 404) {
                    G.toast(getString(R.string.wallet_not_found));
                } else {
                    G.toast(getString(R.string.please_try_again));
                }
            }
        });
    }

    private void send(int walletId, int amount) {
        showLoading();
        Repository.getInstance().transferCharge(walletId, amount, new CallBack<Default>() {
            @Override
            public void onSuccess(Default s) {
                super.onSuccess(s);
                hideLoading();
                G.toast(getString(R.string.successful_operation));
                edtAmount.getText().clear();
                edtPhoneNumber.getText().clear();
                Repository.getInstance().getProfile(null);
            }

            @Override
            public void onFail(RequestException e) {
                super.onFail(e);
                hideLoading();
                if (e.getResponseCode() == 402) {
                    G.toast(getString(R.string.balance_is_not_enough));
                } else if (e.getResponseCode() == 404) {
                    G.toast(getString(R.string.wallet_not_found));
                } else {
                    G.toast(getString(R.string.please_try_again));
                }
            }
        });
    }
}
