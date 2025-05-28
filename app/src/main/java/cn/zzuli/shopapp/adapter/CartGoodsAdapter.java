package cn.zzuli.shopapp.adapter;

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

public class CartGoodsAdapter extends RecyclerView.Adapter<CartGoodsAdapter.CartViewHolder> {

    private List<CarResponse.DataBean> cartGoodsData;
    private String Imgstr = "http://115.158.64.84:28019/";
    private boolean showOperation = true;
    public interface OnCartChangeListener {
        void onCartChanged(List<CarResponse.DataBean> updatedCart);
    }
    private OnCartChangeListener cartChangeListener;

    public void setOnCartChangeListener(OnCartChangeListener listener) {
        this.cartChangeListener = listener;
    }

    // 构造方法传入数据
    public CartGoodsAdapter(List<CarResponse.DataBean> cartGoodsData,boolean showOperation) {
        this.cartGoodsData = cartGoodsData;
        this.showOperation = showOperation;
    }

    // 更新数据方法
    public void setcartGoodsData(List<CarResponse.DataBean> cartGoodsData) {
        this.cartGoodsData = cartGoodsData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 加载 item 布局文件（你需要提前准备好 item_goods.xml）,这里是goods list.xml
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.goodslist, parent, false);
        return new CartViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CarResponse.DataBean item = cartGoodsData.get(position);

        holder.tvName.setText(item.getGoodsName());
        holder.tvDesc.setText("暂无描述"); // 如果后期后端返回了描述字段再替换
        holder.tvPrice.setText("￥" + item.getSellingPrice());
        holder.tvNum.setText(String.valueOf(item.getGoodsCount()));
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(item.isChecked());

        //根据传递的showOperation参数不同,选择是否显示选择框和数量加减
        holder.checkBox.setVisibility(showOperation ? View.VISIBLE : View.GONE);
        holder.ivAdd.setVisibility(showOperation ? View.VISIBLE : View.GONE);
        holder.ivMinus.setVisibility(showOperation ? View.VISIBLE : View.GONE);


        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setChecked(isChecked);

            if(cartChangeListener != null){
                cartChangeListener.onCartChanged(cartGoodsData);
            }
        });
        Glide.with(holder.itemView.getContext())
                .load(Imgstr+item.getGoodsCoverImg())
                .into(holder.ivPhoto);

        // 你也可以在此设置 + / - 按钮的监听器来修改数量
        holder.ivAdd.setOnClickListener(v -> {
            int count = item.getGoodsCount();
            if (count < 5) {
                item.setGoodsCount(count + 1);
                holder.tvNum.setText(String.valueOf(item.getGoodsCount()));
                notifyItemChanged(position);
                if (cartChangeListener != null) {
                    cartChangeListener.onCartChanged(cartGoodsData);
                }
            }
        });

        holder.ivMinus.setOnClickListener(v -> {
            int count = item.getGoodsCount();
            if (count > 1) {
                item.setGoodsCount(count - 1);
                holder.tvNum.setText(String.valueOf(item.getGoodsCount()));
                notifyItemChanged(position);
                if (cartChangeListener != null) {
                    cartChangeListener.onCartChanged(cartGoodsData);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return cartGoodsData != null ? cartGoodsData.size() : 0;
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
