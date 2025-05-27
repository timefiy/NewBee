package cn.zzuli.shopapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;

import cn.zzuli.shopapp.adapter.OrderAdapter;
import cn.zzuli.shopapp.entity.OrderGoods;
import cn.zzuli.shopapp.entity.Orders;

public class MyOrderActivity extends AppCompatActivity {
    private int id;
    private String token;
    private int status;
    private List<Orders> orders = new ArrayList<>();
    private URL url;
    private int totalPage;
    private ActivityResultLauncher<Intent> myOrderLauncher;


    private String strOrders = "http://115.158.64.84:28019/api/v1/order";
    private String strAll;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        SharedPreferences info = getSharedPreferences("info", MODE_PRIVATE);
        token = info.getString("token", token);

        if(token==null){
            Intent intent = new Intent(MyOrderActivity.this, RegisterActivity.class);
            startActivity(intent);
        }

        initOrders();
    }
    public void initOrders(){
        ImageView iv_back = findViewById(R.id.iv_back);

        TabLayout tabLayout = findViewById(R.id.tbl_orders);
        id = tabLayout.getSelectedTabPosition();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                id = tab.getPosition();
                toAll();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                id = tab.getPosition();
                toAll();
            }
        });

        toAll();

        iv_back.setOnClickListener(v->{
            finish();
        });

        myOrderLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        orders.clear();
                        toAll();
                        Log.d("OrderRefresh", "订单数据已刷新");
                    }
                }
        );
    }

    private void toAll(){
        orders.clear();
        new Thread(){
            @Override
            public void run(){
                try{
                    int page = 1;
                    do{
                        System.out.println(page);
                        if(id != 0){
                            status = id - 1;
                            strAll = strOrders+"?pageNumber="+Integer.toString(page)+"&status="+status;
                        } else{
                            strAll = strOrders+"?pageNumber="+Integer.toString(page);
                        }
                        url = new URL(strAll);
                        HttpURLConnection cn = (HttpURLConnection) url.openConnection();
                        cn.setRequestMethod("GET");
                        cn.setRequestProperty("Content-Type","application/json");
                        cn.setRequestProperty("token", token);
                        if(cn.getResponseCode()==200){
                            InputStream inputStream = cn.getInputStream();
                            byte[] bytes=new byte[1024];
                            ByteArrayOutputStream baos=new ByteArrayOutputStream();
                            int len=0;
                            while((len=inputStream.read(bytes))>0){
                                baos.write(bytes,0,len);
                            }
                            JSONObject object = new JSONObject(baos.toString());
                            JSONObject object1 = object.getJSONObject("data");
                            totalPage = object1.getInt("totalPage");
                            JSONArray array = object1.getJSONArray("list");
                            for(int i = 0;i < array.length();i++){
                                List<OrderGoods> orderGoods = new ArrayList<>();
                                Orders order = new Orders();
                                JSONObject object2 = array.getJSONObject(i);
                                order.setOrderId(object2.getInt("orderId"));
                                order.setOrderNo(object2.getString("orderNo"));
                                order.setOrderStatusString(object2.getString("orderStatusString"));
                                order.setPayType(object2.getString("payType"));
                                order.setTotalPrice(object2.getInt("totalPrice"));
                                order.setOrderStatus(object2.getString("orderStatus"));
                                order.setCreateTime(object2.getString("createTime"));
                                JSONArray array1 = object2.getJSONArray("newBeeMallOrderItemVOS");
                                for(int j = 0;j < array1.length();j++){
                                    OrderGoods orderGood = new OrderGoods();
                                    JSONObject object3 = array1.getJSONObject(j);
                                    orderGood.setGoodsId(object3.getInt("goodsId"));
                                    orderGood.setGoodsCount(object3.getInt("goodsCount"));
                                    orderGood.setGoodsName(object3.getString("goodsName"));
                                    orderGood.setGoodsCoverImg(object3.getString("goodsCoverImg"));
                                    orderGood.setSellingPrice(object3.getInt("sellingPrice"));
                                    orderGoods.add(orderGood);
                                }
                                order.setOrderGoods(orderGoods);
                                orders.add(order);
                                System.out.println(order);
                            }
                        }
                        page++;
                    }while (page <= totalPage);
                    runOnUiThread(() -> {
                        RecyclerView outerRecyclerView = findViewById(R.id.rl_order_layout);
                        outerRecyclerView.setLayoutManager(new LinearLayoutManager(MyOrderActivity.this));
                        OrderAdapter adapter = new OrderAdapter(orders, MyOrderActivity.this, myOrderLauncher);
                        outerRecyclerView.setAdapter(adapter);
                    });
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        }.start();
    }
}
