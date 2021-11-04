package com.taxialaan.drivers.Activity.wallet;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.taxialaan.drivers.Api.Repository;
import com.taxialaan.drivers.Api.interfaces.CallBack;
import com.taxialaan.drivers.Api.response.Default;
import com.taxialaan.drivers.Api.utils.RequestException;
import com.taxialaan.drivers.G;
import com.taxialaan.drivers.R;

public class ActivityRequestMoney extends AppCompatActivity {
    ImageView backArrow;
    EditText edtAmount;
    Button apply_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_wallet_request_money);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        readView();
        functionView();
    }


    private void readView() {
        backArrow = findViewById(R.id.backArrow);
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
                String amount = edtAmount.getText().toString();
                if (amount.isEmpty()) {
                    return;
                }
                int amounti = Integer.parseInt(amount);
                send(amounti);
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

    private void send(final int amount) {
        showLoading();
        Repository.getInstance().requestMoney(amount, new CallBack<Default>() {
            @Override
            public void onSuccess(Default aDefault) {
                super.onSuccess(aDefault);
                hideLoading();
                edtAmount.getText().clear();
                G.toast(getString(R.string.successful_operation));
                Repository.getInstance().getProfile(null);
            }

            @Override
            public void onFail(RequestException e) {
                super.onFail(e);
                hideLoading();
                if (e.getResponseCode() == 402) {
                    G.toast(getString(R.string.balance_is_low));
                } else {
                    G.toast(getString(R.string.please_try_again));
                }
            }
        });
    }
}
