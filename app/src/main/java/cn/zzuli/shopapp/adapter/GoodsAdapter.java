package cn.zzuli.shopapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.zzuli.shopapp.R;
import cn.zzuli.shopapp.entity.Goods;

public class GoodsAdapter extends BaseAdapter {

    private Context context;
    private List<Goods> goodsList;
    private LayoutInflater inflater; // 布局填充器（将 XML 转换为 View 对象）

    public GoodsAdapter(Context context, List<Goods> goodsList) {
        this.context = context;
        this.goodsList = goodsList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return goodsList.size();
    }

    @Override
    public Object getItem(int position) {
        return goodsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.home_goods, parent, false);
            holder = new ViewHolder();
            holder.goodsImage = convertView.findViewById(R.id.iv_goods_image);
            holder.goodsName = convertView.findViewById(R.id.tv_goods_name);
            holder.goodsPrice = convertView.findViewById(R.id.tv_goods_price);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Goods goods = goodsList.get(position);

        // 使用 Glide 加载图片
        Glide.with(context)
             .load(goods.getGoodsCoverImg()) // 假设 Goods 类有 getGoodsCoverImg() 方法
             .placeholder(R.drawable.ic_category) // 可选：占位图
             .error(R.drawable.ic_menu) // 可选：错误图
             .into(holder.goodsImage);

        // 设置商品名称和价格
        holder.goodsName.setText(goods.getGoodsName()); // 假设 Goods 类有 getGoodsName() 方法
        holder.goodsPrice.setText("¥ " + goods.getSellingPrice()); // 假设 Goods 类有 getSellingPrice() 方法

        return convertView;
    }

    static class ViewHolder {
        ImageView goodsImage;
        TextView goodsName;
        TextView goodsPrice;
    }
}
