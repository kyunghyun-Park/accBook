package com.example.accountbook;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ItemViewHolder> {

    // adpater에 들어갈 list
    private ArrayList<TodayData> listData = new ArrayList<>();

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        // LayoutInflater를 이용해서 todaylist.xml을 inflate
        // return 인자는 VIewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detaillist, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position){
        // Item을 하나, 하나 보여주는 (bind 되는) 함수
        holder.onBind(listData.get(position), position);

        // 입출금에 따른 글자 색 입히기
        String color = listData.get(position).getPrice().substring(0,1);

        if(color.equalsIgnoreCase("-")){
            holder.detailPriceTextView.setTextColor(Color.rgb(192,50,90));
        } else {
            holder.detailPriceTextView.setTextColor(Color.rgb(76,152,94));
        }

        // marquee 적용 (TextView가 물 흐르는듯한 효과)
        holder.detailPriceTextView.setSelected(true);
    }

    @Override
    public int getItemCount(){
        // RecyelrView의 총 개수
        return  listData.size();
    }

    void addItem(TodayData data){
        //외부에서 item을 추가시킬 함수
        listData.add(data);
    }

    public void updateData() {
        listData.clear();
    }

    // RecyclerView의 핵심인 ViewHolder
    // subView를 setting
    class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView detailUsageTextView;
        private TextView detailPriceTextView;
        private TodayData data2;
        private int position2;

        ItemViewHolder(View itemView){
            super(itemView);

            detailUsageTextView = itemView.findViewById(R.id.detailUsage);
            detailPriceTextView = itemView.findViewById(R.id.detailPrice);
        }

        void onBind(TodayData data, int position){
            this.data2 = data;
            this.position2 = position;

            detailUsageTextView.setText(data2.getUsage());
            detailPriceTextView.setText(data2.getPrice());
        }
    }
}
