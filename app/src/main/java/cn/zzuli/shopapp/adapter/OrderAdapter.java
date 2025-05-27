package cn.zzuli.shopapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.zzuli.shopapp.R;
import cn.zzuli.shopapp.entity.OrderGoods;
import cn.zzuli.shopapp.entity.Orders;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Orders> orders;
    private List<OrderGoods> orderGoods;
    private Context context;
    private ActivityResultLauncher<Intent> myOrderLauncher;

    public OrderAdapter(List<Orders> orders, Context context,ActivityResultLauncher<Intent> myOrderLauncher) {
        this.orders = orders;
        this.context = context;
        this.myOrderLauncher = myOrderLauncher;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_orders_two, parent, false); // 外层订单项布局
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Orders order = orders.get(position);
        orderGoods = order.getOrderGoods();
        String orderNo = order.getOrderNo();

        holder.createTime.setText(order.getCreateTime());
        holder.tvStatus.setText(order.getOrderStatusString());

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        holder.innerRecyclerView.setLayoutManager(layoutManager);
        OrderGoodsAdapter goodsAdapter = new OrderGoodsAdapter(orderGoods, orderNo, myOrderLauncher);
        holder.innerRecyclerView.setAdapter(goodsAdapter);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView createTime, tvStatus;
        RecyclerView innerRecyclerView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            createTime = itemView.findViewById(R.id.order_create_time);
            tvStatus = itemView.findViewById(R.id.tv_status);
            innerRecyclerView = itemView.findViewById(R.id.rl_good);
        }
    }
}
