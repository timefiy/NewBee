package cn.zzuli.shopapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.zzuli.shopapp.adapter.GoodsAdapter;
import cn.zzuli.shopapp.adapter.OrderAdapter;
import cn.zzuli.shopapp.adapter.OrderGoodsAdapter;
import cn.zzuli.shopapp.entity.OrderGoods;
import cn.zzuli.shopapp.entity.Orders;

public class OrdersDetailsActivity extends AppCompatActivity {
    private String strOrders = "http://115.158.64.84:28019/api/v1/order/";
    private String strpaySuccess = "http://115.158.64.84:28019/api/v1/paySuccess";
    private String token;
    private String orderNo;
    private Orders order = new Orders();
    private TextView orderStatus;
    private TextView orderNumber;
    private TextView orderCreateTime;
    private TextView goodMoney;
    private Button toPay;
    private Button cancelOrders;
    private List<OrderGoods> orderGoods;
    private int payType = 0;
    private ActivityResultLauncher<Intent> myOrderLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_details);

        orderStatus = findViewById(R.id.tv_orders_status);
        orderNumber = findViewById(R.id.tv_orders_number);
        orderCreateTime = findViewById(R.id.tv_orders_create_time);
        goodMoney = findViewById(R.id.tv_good_money);
        toPay = findViewById(R.id.btn_orders_to_pay);
        cancelOrders = findViewById(R.id.btn_cancel_orders);
        
        toPay.setVisibility(View.GONE);
        cancelOrders.setVisibility(View.GONE);

        ImageView back = findViewById(R.id.iv_back);
        back.setOnClickListener(v->{
            setResult(RESULT_OK);
            finish();
        });

        SharedPreferences info = getSharedPreferences("info", MODE_PRIVATE);
        token = info.getString("token", "");
        Intent intent = getIntent();
        if (intent != null) {
            orderNo = intent.getStringExtra("orderNo");
            System.out.println(orderNo);
        }

        toDetail();

        toPay.setOnClickListener(v->{
            showPaymentDialog();
        });

        cancelOrders.setOnClickListener(v->{
            toCancelOrders();
        });
    }

    private void toDetail(){
        new Thread(){
            @Override
            public void run() {
                try{
                    URL url = new URL(strOrders+orderNo);
                    HttpURLConnection cn = (HttpURLConnection) url.openConnection();
                    cn.setRequestMethod("GET");
                    cn.setRequestProperty("token", token);
                    if(cn.getResponseCode() == 200){
                        InputStream inputStream = cn.getInputStream();
                        byte[] bytes=new byte[1024];
                        ByteArrayOutputStream baos=new ByteArrayOutputStream();
                        int len=0;
                        while((len=inputStream.read(bytes))>0){
                            baos.write(bytes,0,len);
                        }
                        orderGoods = new ArrayList<>();
                        JSONObject object = new JSONObject(baos.toString());
                        JSONObject object2 = object.getJSONObject("data");
                        System.out.println(object);
                        order.setOrderNo(object2.getString("orderNo"));
                        order.setOrderStatusString(object2.getString("orderStatusString"));
                        order.setPayType(object2.getString("payType"));
                        order.setTotalPrice(object2.getInt("totalPrice"));
                        order.setOrderStatus(object2.getString("orderStatus"));
                        order.setCreateTime(object2.getString("createTime"));
                        JSONArray array1 = object2.getJSONArray("newBeeMallOrderItemVOS");
                        for(int i = 0; i<array1.length(); i++){
                            OrderGoods orderGood = new OrderGoods();
                            JSONObject object3 = array1.getJSONObject(i);
                            orderGood.setGoodsId(object3.getInt("goodsId"));
                            orderGood.setGoodsCount(object3.getInt("goodsCount"));
                            orderGood.setGoodsName(object3.getString("goodsName"));
                            orderGood.setGoodsCoverImg(object3.getString("goodsCoverImg"));
                            orderGood.setSellingPrice(object3.getInt("sellingPrice"));
                            orderGoods.add(orderGood);
                        }
                        runOnUiThread(()->{
                            if(order.getOrderStatusString().equals("已支付")){
                                cancelOrders.setVisibility(View.VISIBLE);
                                Log.println(Log.INFO, "OrderDetailActivity", "取消订单按钮以生成");
                            } else if (order.getOrderStatusString().equals("待支付")) {
                                toPay.setVisibility(View.VISIBLE);
                                cancelOrders.setVisibility(View.VISIBLE);
                            }
                            System.out.println(order);
                            orderStatus.setText(order.getOrderStatusString());
                            orderNumber.setText(order.getOrderNo());
                            orderCreateTime.setText(order.getCreateTime());
                            goodMoney.setText(String.valueOf(order.getTotalPrice()));
                            RecyclerView outerRecyclerView = findViewById(R.id.rl_goods_details);
                            outerRecyclerView.setLayoutManager(new LinearLayoutManager(OrdersDetailsActivity.this));
                            OrderGoodsAdapter adapter = new OrderGoodsAdapter(orderGoods, orderNo, myOrderLauncher);
                            outerRecyclerView.setAdapter(adapter);
                        });
                    }
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        }.start();
    }

    private void toPayOrders(){
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL(strpaySuccess+"?orderNo="+order.getOrderNo()+"&payType="+Integer.toString(payType));
                    HttpURLConnection cn = (HttpURLConnection) url.openConnection();
                    cn.setRequestMethod("GET");
                    cn.setRequestProperty("token", token);
                    System.out.println(cn.getResponseCode());
                    System.out.println(orderNo+payType);
                    if(cn.getResponseCode()==200){
                        InputStream inputStream = cn.getInputStream();
                        byte[] bytes=new byte[1024];
                        ByteArrayOutputStream baos=new ByteArrayOutputStream();
                        int len=0;
                        while((len=inputStream.read(bytes))>0){
                            baos.write(bytes,0,len);
                        }
                        JSONObject object = new JSONObject(baos.toString());
                        System.out.println(object.getInt("resultCode"));
                        if(object.getInt("resultCode")==200){
                            runOnUiThread(()->{
                                orderStatus.setText("已支付");
                                toPay.setVisibility(View.GONE);
                            });
                        }
                    }
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        }.start();
    }

    private void toCancelOrders(){
        new Thread(){
            @Override
            public void run(){
                try {
                    URL url = new URL(strOrders+order.getOrderNo()+"/cancel");
                    HttpURLConnection cn = (HttpURLConnection) url.openConnection();
                    cn.setRequestMethod("PUT");
                    cn.setRequestProperty("token", token);
                    if(cn.getResponseCode()==200){
                        runOnUiThread(()->{
                            cancelOrders.setVisibility(View.GONE);
                            orderStatus.setText("手动关闭");
                        });
                    }
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        }.start();
    }

    private void showPaymentDialog() {
        // 创建自定义View
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_payment, null);

        // 初始化支付按钮
        Button btnAlipay = dialogView.findViewById(R.id.btn_alipay);
        Button btnWechat = dialogView.findViewById(R.id.btn_wechat);

        // 创建对话框
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.PaymentDialogTheme)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        // 设置窗口参数（显示后90%宽度）
        dialog.setOnShowListener(dialogInterface -> {
            Window window = dialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            if (window != null) {
                DisplayMetrics metrics = new DisplayMetrics();
                window.getWindowManager().getDefaultDisplay().getMetrics(metrics);

                WindowManager.LayoutParams params = window.getAttributes();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(params);
            }
        });

        btnAlipay.setOnClickListener(v -> {
            payType = 1;
            toPayOrders();
            dialog.dismiss();
        });

        btnWechat.setOnClickListener(v -> {
            payType = 2;
            toPayOrders();
            dialog.dismiss();

        });
        dialog.show();
    }

}
