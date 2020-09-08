package com.example.accountbook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter2 extends RecyclerView.Adapter<RecyclerAdapter2.ItemViewHolder>{

    private ArrayList<TodayData> listData = new ArrayList<>();

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout,parent,false);
        return new ItemViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position){
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount(){
        return listData.size();
    }

    void addItem(TodayData data){
        listData.add(data);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView usage;
        private TextView money;

        ItemViewHolder(View itemView){
            super(itemView);

            usage = itemView.findViewById(R.id.usage);
            money = itemView.findViewById(R.id.money);
        }

        void onBind(TodayData data){
            usage.setText(data.getUsage());
            money.setText(data.getPrice());
        }
    }
}
