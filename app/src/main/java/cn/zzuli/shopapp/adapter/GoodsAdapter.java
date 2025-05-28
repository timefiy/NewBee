package cn.zzuli.shopapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.zzuli.shopapp.R;
import cn.zzuli.shopapp.ProductDetailActivity;
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
//            Log.e("GoodsAdapter", "创建新的convertView: " + position);
        } else {
            holder = (ViewHolder) convertView.getTag();
//            Log.e("GoodsAdapter", "复用convertView: " + position);
        }

        Goods goods = goodsList.get(position);
//        Log.e("GoodsAdapter", "处理商品: " + position + ", ID: " + goods.getGoodsId());

        // 使用 Glide 加载图片
        Glide.with(context)
             .load("http://115.158.64.84:28019" + goods.getGoodsCoverImg())
             .placeholder(R.drawable.ic_category)
             .error(R.drawable.ic_menu)
             .into(holder.goodsImage);

        // 设置商品名称和价格
        holder.goodsName.setText(goods.getGoodsName());
        holder.goodsPrice.setText("¥ " + goods.getSellingPrice());

        // 给整个商品项添加点击事件
        convertView.setClickable(true);  // 确保视图可点击
        convertView.setFocusable(true);  // 确保视图可获得焦点
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.e("GoodsAdapter", "点击事件触发 - 商品ID: " + goods.getGoodsId());
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("goodsId", String.valueOf(goods.getGoodsId()));
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        ImageView goodsImage;
        TextView goodsName;
        TextView goodsPrice;
    }
}
