package com.taxialaan.drivers.Activity.wallet;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;


import com.taxialaan.drivers.Api.Repository;
import com.taxialaan.drivers.Api.interfaces.CallBack;
import com.taxialaan.drivers.Api.response.TransactionItem;
import com.taxialaan.drivers.Api.utils.RequestException;
import com.taxialaan.drivers.R;
import com.taxialaan.drivers.adapters.TransactionAdapter;

import java.util.List;

public class ActivityTransactionList extends AppCompatActivity {
    ImageView backArrow;
    RecyclerView recyclerView;
    TransactionAdapter transactionAdapter = new TransactionAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_wallet_transaction_list);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        readView();
        functionView();
    }


    private void readView() {
        backArrow = findViewById(R.id.backArrow);
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void functionView() {
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(transactionAdapter);
        getData();
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

    private void getData() {
        showLoading();
        Repository.getInstance().getTransactionList(new CallBack<List<TransactionItem>>() {
            @Override
            public void onSuccess(List<TransactionItem> transactionItems) {
                super.onSuccess(transactionItems);
                transactionAdapter.putAndClear(transactionItems);
                transactionAdapter.notifyDataSetChanged();
                hideLoading();
            }

            @Override
            public void onFail(RequestException e) {
                super.onFail(e);
                hideLoading();
            }
        });
    }
}
