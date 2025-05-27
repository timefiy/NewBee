package cn.zzuli.shopapp;



import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import cn.zzuli.shopapp.R;

public class ProductDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_product_1);
        String goodsId = getIntent().getStringExtra("goodsId");
        // 根据goodsId加载商品详情数据
        // 这里可以添加您的商品详情逻辑
    }
}