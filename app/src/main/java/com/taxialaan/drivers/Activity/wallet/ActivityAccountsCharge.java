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


public class ActivityAccountsCharge extends AppCompatActivity {
    ImageView backArrow;
    EditText edtCode;
    Button apply_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_wallet_accounts_charge);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        readView();
        functionView();
    }


    private void readView() {
        backArrow = findViewById(R.id.backArrow);
        edtCode = findViewById(R.id.edtCode);
        apply_button = findViewById(R.id.apply_button);
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
                String code = edtCode.getText().toString();
                if (code.isEmpty()) {
                    return;
                }
                showLoading();
                Repository.getInstance().chargeCode(code, new CallBack<Default>() {
                    @Override
                    public void onSuccess(Default s) {
                        super.onSuccess(s);
                        G.toast(getString(R.string.charge_code_applied_successfully));
                        edtCode.getText().clear();
                        Repository.getInstance().getProfile(null);
                        hideLoading();
                    }
                    @Override
                    public void onFail(RequestException e) {
                        super.onFail(e);
                        if (e.getResponseCode() == 404) {
                            G.toast(getString(R.string.invalid_charge_code));
                        } else {
                            G.toast(getString(R.string.please_try_again));
                        }
                        hideLoading();
                    }
                });
            }
        });


    }
}
