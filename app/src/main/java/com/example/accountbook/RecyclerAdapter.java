package com.example.accountbook;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {

    // adpater에 들어갈 list
    private ArrayList<TodayData> listData = new ArrayList<>();
    private Context context;

    // Item의 클릭 상태를 저장할 array 객체
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    // 직전에 클릭됐던 Item의 position
    private int prePosition = -1;

    // 클릭 이벤트 처리 관련 사용자 정의
    private AdapterCallback mAdapterCallback;

    public RecyclerAdapter(Context context) {
        try {
            this.mAdapterCallback = ((AdapterCallback) context);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        // LayoutInflater를 이용해서 todaylist.xml을 inflate
        // return 인자는 VIewHolder
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todaylist, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position){
        // Item을 하나, 하나 보여주는 (bind 되는) 함수
        holder.onBind(listData.get(position), position);

        // 입출금에 따른 글자 색 입히기
        String color = listData.get(position).getPrice().substring(0,1);

        if(color.equalsIgnoreCase("-")){
            holder.price.setTextColor(Color.rgb(192,50,90));
        } else {
            holder.price.setTextColor(Color.rgb(76,152,94));
        }


        // 펼치기 접기 이미지 변경
        //clickImg.setImageResource(R.drawable.close);
        if(selectedItems.get(position, false)){
            holder.clickImg.setImageResource(R.drawable.close);
        } else {
            holder.clickImg.setImageResource(R.drawable.open);
        }

        // marquee 적용 (TextView가 물 흐르는듯한 효과)
        holder.price.setSelected(true);

        // simple example, call interface here
        // not complete
        holder.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapterCallback.onMethodCallback(holder.price.getText().toString(), holder.time.getText().toString(), holder.uesdPlace.getText().toString(), holder.usage.getText().toString());

                holder.uesdPlace.setHint(listData.get(position).getPlace());
                holder.usage.setHint(holder.usage.getText().toString());

                listData.get(position).setPlace(holder.uesdPlace.getText().toString());
                listData.get(position).setUsage(holder.usage.getText().toString());
                Log.d("홀더에 내용이 저장 됏나요?", listData.get(position).getPlace()+"됏습니다만");
            }
        });
    }

    //리스너 관련
    public static interface AdapterCallback {
        void onMethodCallback(String price, String time, String place, String usage);
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
    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView price;
        private TextView time;
        private TextView uesdPlaceTextView;
        private TextView usageTextView;
        private EditText uesdPlace;
        private EditText usage;
        private Button saveBtn;
        private TodayData data;
        private ImageView clickImg;
        private int position;

        ItemViewHolder(View itemView){
            super(itemView);

            price = itemView.findViewById(R.id.priceTextView);
            time = itemView.findViewById(R.id.timeTextView);
            uesdPlaceTextView = itemView.findViewById(R.id.uesdPlaceTextView);
            usageTextView = itemView.findViewById(R.id.usageTextView);
            uesdPlace = itemView.findViewById(R.id.uesdPlace);
            usage = itemView.findViewById(R.id.usage);
            clickImg = itemView.findViewById(R.id.cilckImg);
            saveBtn = itemView.findViewById(R.id.saveBtn);
        }

        void onBind(TodayData data, int position){
            this.data = data;
            this.position = position;

            price.setText(data.getPrice());
            time.setText(data.getTime());
            uesdPlace.setText(data.getPlace());
            usage.setText(data.getUsage());

            changeVisibility(selectedItems.get(position));

            itemView.setOnClickListener(this);
            //uesdPlaceTextView.setOnClickListener(this);
            usageTextView.setOnClickListener(this);
            uesdPlace.setOnClickListener(this);
            usage.setOnClickListener(this);
            clickImg.setOnClickListener(this);
            saveBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            switch (v.getId()){
                case R.id.linearItem:
                case R.id.cilckImg:
                    if (selectedItems.get(position)){
                        // 펼쳐진 Item을 클릭 시
                        selectedItems.delete(position);
                    } else {
                        // 직전의 클릭됐던 Item의 클릭상태를 지움
                        selectedItems.delete(prePosition);
                        // 클릭한 Item의 position을 저장
                        selectedItems.put(position, true);
                    }
                    // 해당 포지션의 변화를 알림
                    if (prePosition != -1) notifyItemChanged(prePosition);
                    notifyItemChanged(position);
                    // 클릭된 position 저장
                    prePosition = position;
                    break;
            }
        }

        // 클릭된 Item의 상태 변경
        // @param isExpanded Item을 펼칠 것인지 여부

        private void changeVisibility(final boolean isExpanded){
            int dpValue = 150;
            float d = context.getResources().getDisplayMetrics().density;
            int height = (int) (dpValue * d);

            // ValueAnimator.of Int(int... values)는 View가 변할 값을 지정, 인자는 int 배열
            ValueAnimator va = isExpanded ? ValueAnimator.ofInt(0, height) : ValueAnimator.ofInt(height, 0);
            // Animation이 실행되는 시간, n/1000초
            va.setDuration(600);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    // imageView가 실제로 사라지게하는 부분
                    uesdPlaceTextView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                    usageTextView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                    uesdPlace.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                    usage.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                    saveBtn.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                }
            });
            // Animation start
            va.start();
        }
    }
}
