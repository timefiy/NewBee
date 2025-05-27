package cn.zzuli.shopapp;



import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.zzuli.shopapp.R;
import cn.zzuli.shopapp.entity.j_11;

public class ProductDetailActivity extends AppCompatActivity {
    int img[] ={R.drawable.b1,R.drawable.b1,R.drawable.b1,R.drawable.b1,R.drawable.b1,R.drawable.b1,R.drawable.b1,R.drawable.b1,
            R.drawable.b1,R.drawable.b1,R.drawable.b1,R.drawable.b1,R.drawable.b1
            ,R.drawable.b1,R.drawable.b1,R.drawable.b1,R.drawable.b1,R.drawable.b1};
    int id[] = new int[]{10893,10895,10283,10320,10700,10742,10159,10779,10195,10180,10147,10894,10237,10160,10254,10154,10113,10158};
    int price[] = new int[]{5399,3999,6799,9999,999,5488,1246,3199,2599,1246,65,5399,165,129,8499,85,250,70};
    String text1[]=new String[]{"HUAWEI Mate 30 Pro 双4000万徕卡电影四摄",
            "HUAWEI Mate 30 4000万超感光徕卡影像",
            "Apple iPhone 11 (A2223)",
            "Apple iPhone 11 Pro",
            "荣耀8X 千元屏霸 91%屏占比 2000万AI双摄",
            "华为 HUAWEI P30 Pro",
            "Apple AirPods 配充电盒",
            "华为 HUAWEI Mate 20",
            "索尼 WH-1000XM3 头戴式耳机",
            "Apple AirPods 配充电盒",
            "MUJI 羽毛 靠垫",
            "HUAWEI Mate 30 Pro",
            "MAC 雾面丝绒哑光子弹头口红",
            "小米 Redmi AirDots",
            "Apple 2019款 MacBook Air 13.3",
            "无印良品 MUJI 塑料浴室座椅",
            "无印良品 MUJI 小型超声波香薰机",
            "无印良品 女式粗棉线条纹长袖T恤"};
    List<j_11> list=new ArrayList<>();
    j_11 j;
    private ImageView ivProduct;
    private TextView idProduct;
    private TextView tvName;
    private TextView tvPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_product_1);
        String goodsId = getIntent().getStringExtra("goodsId");
        // 根据goodsId加载商品详情数据
        ivProduct = findViewById(R.id.iv_product);
        tvName = findViewById(R.id.tv_product_name);
        tvPrice = findViewById(R.id.tv_product_price);
        for(int i=0;i<id.length;i++){
            j.setGoodsId(id[i]);
            j.setGoodsName(text1[i]);
            j.setGoodsCoverImg(img[i]);
            j.setSellingPrice(price[i]);
            list.add(j);
        }

        for (int i = 0; i < list.size(); i++) {
            j_11 j1 = list.get(i);
            if (goodsId == Integer.toString(j1.getGoodsId())) {
                ivProduct.setImageResource(j1.getGoodsCoverImg());
                // 设置商品名称（使用j_11中的商品名称）
                tvName.setText(j1.getGoodsName());
                // 使用您提供的价格数组
                tvPrice.setText(j1.getSellingPrice());
                break;
            }
        }
    }

    private void tonew(){
        new Thread(){
            public void run(){
                try{
                    URL uel = new URL("");
                    HttpURLConnection cn = (HttpURLConnection) uel.openConnection();
                    cn.setRequestMethod("GET");
                    cn.setRequestProperty("Content-Type","application/json");
                    cn.setRequestProperty("token", "acdae1a2e931130cefe66d336276ea98");
                    if(cn.getResponseCode() == 200){
                        InputStream inputStream = cn.getInputStream();
                        //流转字符串
                        byte[] bytes=new byte[1024];
                        ByteArrayOutputStream baos=new ByteArrayOutputStream();
                        int len=0;
                        while((len=inputStream.read(bytes))>0){//--
                            baos.write(bytes,0,len);
                        }
                        System.out.println(baos.toString());

                        JSONObject obj = new JSONObject(baos.toString());

                        JSONObject obj1 = obj.getJSONObject("data");
                        JSONArray array = obj1.getJSONArray("hotGoodses");
                        for(int i = 0;i<array.length();i++){
                            JSONObject obj3 = array.getJSONObject(i);
                            j = new j_11();
                            j.setGoodsId(obj3.getInt("goodId"));
                            list.add(j);
                        }
                    }
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        }.start();
    }
}