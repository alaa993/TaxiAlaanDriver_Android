package com.taxialaan.drivers.adapters;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.taxialaan.drivers.Api.response.TransactionItem;
import com.taxialaan.drivers.Constants.ItemClickListener;
import com.taxialaan.drivers.G;
import com.taxialaan.drivers.R;
import com.taxialaan.drivers.Utilities.MyBoldTextView;
import com.taxialaan.drivers.Utilities.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder> {

    private List<TransactionItem> items = new ArrayList<>();
    private ItemClickListener<TransactionItem> listener;

    public void setListener(ItemClickListener<TransactionItem> listener) {
        this.listener = listener;
    }

    public List<TransactionItem> getItems() {
        return items;
    }

    public void putAndClear(List<TransactionItem> items) {
        this.items.clear();
        if (items != null) {
            this.items.addAll(items);
        }
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_transaction, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TransactionItem item = items.get(position);
        holder.bind(item);

    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imgIcon;
        MyBoldTextView txtAmount;
        MyBoldTextView txtDate;
        MyBoldTextView txtDesc;
        MyBoldTextView txtStatus;
        MyBoldTextView txtCommission, txtCash, txtDiscount;
        MyBoldTextView txtRemainBalance;
        MyBoldTextView txtType;


        MyViewHolder(View view) {
            super(view);
            imgIcon = view.findViewById(R.id.imgIcon);
            txtAmount = view.findViewById(R.id.txtAmount);
            txtDate = view.findViewById(R.id.txtDate);
            txtDesc = view.findViewById(R.id.txtDesc);
            txtStatus = view.findViewById(R.id.txtStatus);
            txtType = view.findViewById(R.id.txtType);
            txtCommission = view.findViewById(R.id.txtCommission);
            txtCash = view.findViewById(R.id.txtCash);
            txtDiscount = view.findViewById(R.id.txtDiscount);
            txtRemainBalance = view.findViewById(R.id.txtRemainBalance);
        }

        TransactionItem item;

        void bind(TransactionItem item) {
            this.item = item;
            String am = String.format(Locale.ENGLISH, "%,d", item.getAmount());
            am = item.getAmount() > 0 ? ("+" + am) : am.replace("-", "");
            txtAmount.setText(am);
            txtAmount.setTextColor(Utils.getColor(item.getAmount() > 0 ? R.color.green_500 : R.color.red_500));
            txtAmount.setTypeface(txtAmount.getTypeface(), Typeface.BOLD);
            txtDate.setText(item.getCreatedAt());

            String type = item.getType();
            switch (item.getType()) {
                case "TRIP":
                    type = Utils.getString(R.string.trip);
                    break;
                case "CHARGE_BY_CASH":
                    type = Utils.getString(R.string.charge_by_cash);
                    break;
                case "CHARGE_BY_CODE":
                    type = Utils.getString(R.string.charge_by_code);
                    break;
                case "TRANSFERED_BY_USER":
                    type = Utils.getString(R.string.transfered_by_user);
                    break;
                case "TRANSFERED_TO_USER":
                    type = Utils.getString(R.string.transfered_to_user);
                    break;
                case "TRANSFERED_BY_PROVIDER":
                    type = Utils.getString(R.string.transfered_by_provider);
                    break;
                case "TRANSFERED_TO_PROVIDER":
                    type = Utils.getString(R.string.transfered_to_provider);
                    break;
                case "WITHDRAW":
                    type = Utils.getString(R.string.withdraw);
                    break;
            }
            txtType.setText(type);

            txtDesc.setText(item.getDescription());
            String status = item.getStatus();
            switch (item.getType()) {
                case "CONFIRMED":
                    status = Utils.getString(R.string.confirmed);
                    break;
                case "PENDING":
                    status = Utils.getString(R.string.pending);
                    break;
                case "REJECTED":
                    status = Utils.getString(R.string.rejected);
                    break;
            }

            txtStatus.setText(status);

            txtCommission.setText(Utils.getString(R.string.wallet_commission));
            txtCommission.append(" : ");
            txtCommission.append(String.format(Locale.ENGLISH, "%,d", item.getCommission()));


            txtDiscount.setText(Utils.getString(R.string.Discount));
            txtDiscount.append(" : ");
            txtDiscount.append("" + item.getDiscount());

            txtCash.setText(Utils.getString(R.string.cash));
            txtCash.append(" : ");
            txtCash.append("" + item.getCash());


            txtRemainBalance.setText(G.getInstance().getString(R.string.remain_balance));
            txtRemainBalance.append(" : ");
            txtRemainBalance.append(String.format(Locale.ENGLISH, "%,d", item.getRemainBalance()));
            imgIcon.setImageResource(item.getAmount() > 0 ? R.drawable.ic_trending_up_green_500_24dp : R.drawable.ic_trending_down_red_500_24dp);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.itemClicked(view, item);
            }
        }
    }


}
