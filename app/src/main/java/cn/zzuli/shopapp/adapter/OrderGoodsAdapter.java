package cn.zzuli.shopapp.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.zzuli.shopapp.OrdersDetailsActivity;
import cn.zzuli.shopapp.R;
import cn.zzuli.shopapp.entity.OrderGoods;

public class OrderGoodsAdapter extends RecyclerView.Adapter<OrderGoodsAdapter.GoodsViewHolder> {
    private List<OrderGoods> goodsList;
    private String str = "http://115.158.64.84:28019/";
    private String orderNo;
    private ActivityResultLauncher<Intent> myOrderLauncher;

    public OrderGoodsAdapter(List<OrderGoods> goodsList, String orderNo, ActivityResultLauncher<Intent> myOrderLauncher) {
        this.goodsList = goodsList;
        this.orderNo = orderNo;
        this.myOrderLauncher = myOrderLauncher;
    }

    @NonNull
    @Override
    public GoodsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_orders, parent, false);
        return new GoodsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoodsViewHolder holder, int position) {
        OrderGoods goods = goodsList.get(position);

        holder.tvGoodsName.setText(goods.getGoodsName());
        holder.tvCount.setText(String.format("x%d", goods.getGoodsCount()));
        holder.tvPrice.setText(String.format("¥%d", goods.getSellingPrice()));

        Glide.with(holder.itemView.getContext())
                .load(str+goods.getGoodsCoverImg())
                .into(holder.ivCover);
        holder.itemView.setOnClickListener(v->{
            Intent intent = new Intent(holder.itemView.getContext(), OrdersDetailsActivity.class);
            intent.putExtra("orderNo", orderNo);
            myOrderLauncher.launch(intent);
        });
    }

    @Override
    public int getItemCount() {
        return goodsList.size();
    }

    static class GoodsViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvGoodsName, tvPrice, tvCount;

        public GoodsViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_good_image);
            tvGoodsName = itemView.findViewById(R.id.tv_good_name);
            tvPrice = itemView.findViewById(R.id.tv_total_price);
            tvCount = itemView.findViewById(R.id.tv_good_number);
        }
    }
}
