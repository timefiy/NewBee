package cn.zzuli.shopapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import cn.zzuli.shopapp.R;
import cn.zzuli.shopapp.entity.CarResponse;
import java.util.List;

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.CartViewHolder> {

    private List<CarResponse.DataBean> goodsList;

    // 构造方法传入数据
    public GoodsAdapter(List<CarResponse.DataBean> goodsList) {
        this.goodsList = goodsList;
    }

    // 更新数据方法
    public void setGoodsList(List<CarResponse.DataBean> goodsList) {
        this.goodsList = goodsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 加载 item 布局文件（你需要提前准备好 item_goods.xml）,这里是goodslist.xml
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.goodslist, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CarResponse.DataBean item = goodsList.get(position);

        holder.tvName.setText(item.getGoodsName());
        holder.tvDesc.setText("暂无描述"); // 如果后期后端返回了描述字段再替换
        holder.tvPrice.setText("￥" + item.getSellingPrice());
        holder.tvNum.setText(String.valueOf(item.getGoodsCount()));

        Glide.with(holder.itemView.getContext())
                .load(item.getGoodsCoverImg())
                .into(holder.ivPhoto);

        // 你也可以在此设置 + / - 按钮的监听器来修改数量
        holder.ivAdd.setOnClickListener(v -> {
            int count = item.getGoodsCount();
            item.setGoodsCount(count + 1);
            holder.tvNum.setText(String.valueOf(item.getGoodsCount()));
            notifyItemChanged(position); // 或回调接口通知更新服务器
        });

        holder.ivMinus.setOnClickListener(v -> {
            int count = item.getGoodsCount();
            if (count > 1) {
                item.setGoodsCount(count - 1);
                holder.tvNum.setText(String.valueOf(item.getGoodsCount()));
                notifyItemChanged(position); // 或回调接口通知更新服务器
            }
        });
    }


    @Override
    public int getItemCount() {
        return goodsList != null ? goodsList.size() : 0;
    }

    // 内部 ViewHolder 类
    public static class CartViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        ImageView ivPhoto, ivAdd, ivMinus;
        TextView tvName, tvDesc, tvPrice, tvNum;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.radio);
            ivPhoto = itemView.findViewById(R.id.photo);
            ivAdd = itemView.findViewById(R.id.photo_add);
            ivMinus = itemView.findViewById(R.id.ptoto_down);
            tvName = itemView.findViewById(R.id.text1);
            tvDesc = itemView.findViewById(R.id.test2);
            tvPrice = itemView.findViewById(R.id.price);
            tvNum = itemView.findViewById(R.id.num);
        }
    }

}
