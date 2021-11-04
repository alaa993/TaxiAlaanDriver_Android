package com.taxialaan.drivers.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.taxialaan.drivers.Constants.ItemClickListener;

import java.util.ArrayList;
import java.util.List;


public class CellSettingAdapter extends RecyclerView.Adapter<CellSettingAdapter.MyViewHolder> {

    private List<String> items = new ArrayList<>();
    private String selected = null;
    private ItemClickListener<String> listener;

    public void setListener(ItemClickListener<String> listener) {
        this.listener = listener;
    }

    public String getSelected() {
        return selected;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        for (String item : items) {
            if (!this.items.contains(item)) {
                this.items.add(item);
            }
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      /*  View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_badge, parent, false);*/
        return new MyViewHolder(null);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // String item = items.get(position);
        holder.bind(null);

    }

    @Override
    public int getItemCount() {
        return 10;/*items == null ? 0 : items.size();*/
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgLock;
        TextView txtTitle;


        MyViewHolder(View view) {
            super(view);
         /*   avatar = view.findViewById(R.id.avatar);
            imgLock = view.findViewById(R.id.imgLock);
            txtTitle = view.findViewById(R.id.txtTitle);*/
        }
        String item;
        void bind(String item) {
            this.item = item;
           /* Picasso.get()
                   .load(FixturesData.getRandomBadgesA())
                   .into(avatar);
            avatar.setAlpha(getAdapterPosition() > 4 ? 0.5f : 1.0f);
            imgLock.setVisibility(getAdapterPosition() > 4 ? View.VISIBLE : View.INVISIBLE);
            txtTitle.setText(FixturesData.getRandomShopName());
            itemView.setOnClickListener(this);*/

        }

        @Override
        public void onClick(View view) {
           /* if (listener != null) {
                listener.itemClicked(view, item);
            }*/
        }
    }


    public static class CellItem{

    }

}
