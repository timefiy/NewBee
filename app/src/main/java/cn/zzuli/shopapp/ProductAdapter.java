package cn.zzuli.shopapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cn.zzuli.shopapp.R;
import cn.zzuli.shopapp.entity.j_11;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private Context context;
    private List<j_11> productList;

    // 保留您原有的数组
    int img[] = new int[]{R.drawable.b1,R.drawable.b1,R.drawable.b1,R.drawable.b1,R.drawable.b1,R.drawable.b1,R.drawable.b1,R.drawable.b1,
            R.drawable.b1,R.drawable.b1,R.drawable.b1,R.drawable.b1,R.drawable.b1
            ,R.drawable.b1,R.drawable.b1,R.drawable.b1,R.drawable.b1,R.drawable.b1};
    int id[] = new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18};
    int price[] = new int[]{5399,3999,6799,9999,999,5488,1246,3199,2599,1246,65,5399,165,129,8499,85,250,70};
    String text1[]=new String[]{"HUAWEI Mate 30 Pro 双4000万徕卡电影四摄","Apple iPhone 11 (A2223)","Apple iPhone 11 Pro","荣耀8X 千元屏霸 91%屏占比 2000万AI双摄","华为 HUAWEI P30 Pro","Apple AirPods 配充电盒",
            "华为 HUAWEI Mate 20","索尼 WH-1000XM3 头戴式耳机","Apple AirPods 配充电盒","MUJI 羽毛 靠垫","HUAWEI Mate 30 Pro",
            "MAC 雾面丝绒哑光子弹头口红","小米 Redmi AirDots","Apple 2019款 MacBook Air 13.3","无印良品 MUJI 塑料浴室座椅","无印良品 MUJI 小型超声波香薰机","无印良品 女式粗棉线条纹长袖T恤"};
    public ProductAdapter(Context context, List<j_11> productList) {
        this.context = context;
        this.productList = productList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {//填充数据
        j_11 product = productList.get(position);

        // 使用您提供的图片数组
        holder.ivProduct.setImageResource(img[position % img.length]);
        // 设置商品ID（虽然不可见，但仍可存储数据）
        holder.idProduct.setText(String.valueOf(id[position % id.length]));
        // 设置商品名称（使用j_11中的商品名称）
        holder.tvName.setText(String.valueOf(text1[position%text1.length]));
        // 使用您提供的价格数组
        holder.tvPrice.setText("¥" + String.valueOf(price[position % price.length]));



        // 点击事件
        holder.itemView.setOnClickListener(v -> { // 为整个itemView设置点击监听器
            Intent intent = new Intent(context, ProductAdapter.class);// 创建跳转到商品详情页的Intent
            intent.putExtra("goodsId", product.getGoodsId());// 将当前商品的ID作为额外数据放入Intent
            context.startActivity(intent);// 启动详情页Activity
        });
    }

    @Override
    public int getItemCount() {
        return Math.min(productList.size(), 18); // 最多显示18个商品
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView idProduct;
        TextView tvName;
        TextView tvPrice;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.iv_product);
            idProduct = itemView.findViewById(R.id.id_product); // 使用XML中的id1
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvPrice = itemView.findViewById(R.id.tv_product_price);

            // 需要在XML中添加这个ID

        }
    }
}
