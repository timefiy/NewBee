package cn.zzuli.shopapp.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.zzuli.shopapp.OrdersDetailsActivity;
import cn.zzuli.shopapp.R;
import cn.zzuli.shopapp.adapter.CartGoodsAdapter;
import cn.zzuli.shopapp.adapter.GoodsAdapter;
import cn.zzuli.shopapp.entity.CarResponse;
import cn.zzuli.shopapp.entity.Orders;

public class CartFragment extends Fragment {

    private final String CART_URL = "http://115.158.64.84:28019/api/v1/shop-cart";
    private ViewGroup rootLayout;
    private String token;
    private CartGoodsAdapter adapter;
    private Orders order =new Orders();
    //初始化两个购物车页面的view
    private View nogoodsView;
    private View havegoodsView;
    private CheckBox checkAll;
    private TextView tvTotal;
    private RecyclerView recyclerView;
    private List<CarResponse.DataBean> cartGoodsData;
    private int addressId = 0;
    private int payType;
    private  String orderNo;
    private Handler handler = new Handler(Looper.getMainLooper());

    public CartFragment() {
    }

    //定义接口回调
    public interface OncartGoodsDataLoadedListener {

        //Gson解析成功后回调的方法
        void onDataLoaded(List<CarResponse.DataBean> cartGoodsData);

        //失败后回调的方法
        void onError(Exception e);
    }

    @NonNull
    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // 创建一个临时空的容器布局
        View root = inflater.inflate(R.layout.fragment_cart, container, false);

        rootLayout = root.findViewById(R.id.frameLayout3);
        View MainActivity = inflater.inflate(R.layout.activity_main,container,false);
        SharedPreferences info = requireActivity().getSharedPreferences("info", MODE_PRIVATE);
        token = info.getString("token", "");
        System.out.println(token);
        //初始化两个购物车页面的view
        nogoodsView = inflater.inflate(R.layout.nogoods,rootLayout,false);
        havegoodsView = inflater.inflate(R.layout.havegoods,rootLayout,false);

        //给no goods界面添加按钮点击事件,使其跳转到首页
        Button nogood_button = nogoodsView.findViewById(R.id.nogoods_button);
        nogood_button.setOnClickListener( v -> {
            Toast.makeText(getContext(),"Click",Toast.LENGTH_SHORT).show();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainerView,new HomeFragment());
            transaction.commit();
            TabLayout tabLayout = requireActivity().findViewById(R.id.tabLayout);
            if (tabLayout != null) {
                tabLayout.getTabAt(0).select(); //0 是“首页”tab
            }
        });

        //加载页面
        checkcartGoodsData(nogoodsView,havegoodsView);

        Button btToBuy = havegoodsView.findViewById(R.id.cartToBuyButton);
        btToBuy.setOnClickListener(v -> {
            List<CarResponse.DataBean> checkedItems = new ArrayList<>();
            double total = 0;
            for(CarResponse.DataBean item : cartGoodsData) {
                if(item.isChecked()) {
                    checkedItems.add(item);
                    total += item.getSellingPrice() * item.getGoodsCount();
                }
            }
            if(checkedItems.isEmpty()) {
                Toast.makeText(getContext(), "请选择要购买的商品",Toast.LENGTH_SHORT).show();
                return;
            }
            showOrderConfirmationLayout(checkedItems);

        });
        ImageButton btOption = havegoodsView.findViewById(R.id.optionButton);
        Button deleteButton = havegoodsView.findViewById(R.id.deleteButton);
        deleteButton.setVisibility(View.GONE);    // 隐藏
        btOption.setOnClickListener(v -> {
            if (deleteButton.getVisibility() == View.VISIBLE) {
                deleteButton.setVisibility(View.GONE);
            } else {
                deleteButton.setVisibility(View.VISIBLE);
            }
        });
        deleteButton.setOnClickListener(v -> {
            new Thread(() -> {
                for (CarResponse.DataBean item : new ArrayList<>(cartGoodsData)) {
                    if (item.isChecked()) {
                        deleteCartItem(item.getCartItemId());
                    }
                }

                // 删除完后重新拉取购物车列表
                new Handler(Looper.getMainLooper()).post(() -> {
                    checkcartGoodsData(nogoodsView, havegoodsView); // 主线程中刷新视图
                });
            }).start();
        });
        return root;
    }

    //判断购物车是否为空，以此来决定显示不同界面
    private void checkcartGoodsData(View view1,View view2) {
        new Thread(() -> {
            try {
                URL url = new URL(CART_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("token", token);
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                conn.disconnect();

                JSONObject jsonObject = new JSONObject(result.toString());
                System.out.println("完整响应内容：" + result.toString());
                int resultCode = jsonObject.getInt("resultCode");
                if (resultCode != 200)
                    System.out.println(resultCode + " token " + token);
                JSONArray jsonArray = jsonObject.getJSONArray("data");

                if (jsonArray.length() == 0) {
                    handler.post(() -> {
                        rootLayout.removeAllViews();
                        rootLayout.addView(view1);
                    });//无商品
                } else {
                    handler.post(() -> {
                        rootLayout.removeAllViews();
                        rootLayout.addView(view2);
                        recyclerView = view2.findViewById(R.id.Cart_havegoods);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        adapter = new CartGoodsAdapter(new ArrayList<>(),true);
                        recyclerView.setAdapter(adapter);

                        //将服务端的购物车内的数据获取到have goods页面中
                        if (view2.getParent() != null) {
                            loadcartGoodsData(new OncartGoodsDataLoadedListener() {
                                @Override
                                public void onDataLoaded(List<CarResponse.DataBean> cartGoodsData) {
                                    adapter.setcartGoodsData(cartGoodsData);
                                    System.out.println("这里执行了");
                                }

                                @Override
                                public void onError(Exception e) {
                                    Log.e("loadcartGoodsData","出错了" + e.getClass().getName() + " - " + e.getMessage());
                                    e.printStackTrace();
                                }
                            });
                        }
                        checkAll = view2.findViewById(R.id.cart_checkBox);
                        tvTotal = view2.findViewById(R.id.totalmoney);
                        checkAll.setOnClickListener(v -> {
                            boolean isChecked = checkAll.isChecked();//全选框是否被选中
                            for(CarResponse.DataBean item : cartGoodsData) {
                                item.setChecked(isChecked);//设置每一个条目选框的选中状态
                            }
                            double total;
                            total = 0;
                            if(isChecked == true){
                                for (CarResponse.DataBean item : cartGoodsData) {
                                    total += item.getGoodsCount()*item.getSellingPrice();
                                }
                            }else {
                                total = 0;
                            }
                            tvTotal.setText("总计：￥"+total);
                            adapter.notifyDataSetChanged();
                        });
                        adapter.setOnCartChangeListener(updatedCart -> {
                            boolean allChecked = true;
                            double total = 0;
                            for (CarResponse.DataBean item : updatedCart) {
                                if (item.isChecked()) {
                                    total += item.getSellingPrice() * item.getGoodsCount();
                                } else {
                                    allChecked = false;
                                }
                            }
                            tvTotal.setText("总计：￥" + total);
                            checkAll.setChecked(allChecked);//自动更新全选checkbox状态
                        });



                    }); // 有商品
                }

            } catch (Exception e) {
                Log.e("CartCheck", "发生异常: " + e.getClass().getName() + " - " + e.getMessage());
                e.printStackTrace();
                handler.post(() -> {
                    rootLayout.removeAllViews();
                    rootLayout.addView(view1);
                    Toast.makeText(view1.getContext(),"您还未登录哦",Toast.LENGTH_LONG).show();
                });
            }

        }).start();
    }
    //获取json数据，转为Java对象并回调主线程
    public void loadcartGoodsData(OncartGoodsDataLoadedListener listener) {
        new Thread(() -> {
            try {
                URL url = new URL(CART_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("token", token);
                if(conn.getResponseCode() == 200){
                    InputStream inputStream = conn.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder jsonBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        jsonBuilder.append(line);
                    }
                    bufferedReader.close();
                    String jsonStr = jsonBuilder.toString();
                    System.out.println("完整响应内容：jsonStr:" + jsonStr);
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    JSONArray dataArray = jsonObject.getJSONArray("data");
                    cartGoodsData = new ArrayList<>();
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject item = dataArray.getJSONObject(i);
                        CarResponse.DataBean bean = new CarResponse.DataBean();
                        bean.setGoodsId(item.getInt("goodsId"));
                        bean.setCartItemId(item.getInt("cartItemId"));
                        bean.setGoodsCount(item.getInt("goodsCount"));
                        bean.setGoodsName(item.getString("goodsName"));
                        bean.setGoodsCoverImg(item.getString("goodsCoverImg"));
                        bean.setSellingPrice(item.getInt("sellingPrice"));

                        cartGoodsData.add(bean);
                    }

                    System.out.println(cartGoodsData);
                    Log.d("cartFragment","购物车数量"+cartGoodsData.size());
                }

                // 回到主线程回调结果
                handler.post(()->{
                    listener.onDataLoaded(cartGoodsData);
                });

            } catch (Exception e) {
                handler.post(() -> {
                    listener.onError(e);
                });
            }
        }).start();
    }

    private void loadDefaultAddress(TextView nameText,TextView addressText) {
        new Thread(() -> {
            try {
                URL url = new URL("http://115.158.64.84:28019/api/v1/address/default" ); // 换成你的实际接口
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("token", token); // 如果需要登录 token
                System.out.println(token);
                System.out.println(conn.getResponseCode());
                if (conn.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    reader.close();

                    JSONObject json = new JSONObject(result.toString());
                    JSONObject data = json.getJSONObject("data");
                    String name = data.getString("userName");
                    String phone = data.getString("userPhone");
                    String address = data.getString("detailAddress");
                    addressId = data.getInt("addressId");
                    System.out.println(name+phone+address);
                    requireActivity().runOnUiThread(() -> {
                        nameText.setText(name + "  " + phone);
                        addressText.setText(address);
                    });
                }

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    //加载生成订单页面
    private void showOrderConfirmationLayout(List<CarResponse.DataBean> checkedItems) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View orderView = inflater.inflate(R.layout.creat_order, null);

        // 替换 root 布局（比如原本 havegoodsView 所在的容器）
        ViewGroup rootContainer = (ViewGroup) rootLayout.findViewById(R.id.frameLayout3); //原来布局的容器 id
        rootContainer.removeAllViews();
        rootContainer.addView(orderView);

        TextView nameText = orderView.findViewById(R.id.recipientName);
        ImageView backtoCart = orderView.findViewById((R.id.order_back_btn));
        TextView addressText = orderView.findViewById(R.id.recipientAddress);
        TextView totalText = orderView.findViewById(R.id.totalText);
        RecyclerView orderRecyclerView = orderView.findViewById(R.id.orderRecyclerView);
        ConstraintLayout itemsContainer = orderView.findViewById(R.id.orderLayout);
        Button submitBtn = orderView.findViewById(R.id.btn_confirm_order);
        // 设置地址（可以从本地或网络获取）
        loadDefaultAddress(nameText,addressText);


        // 添加每个商品信息
        CartGoodsAdapter orderAdapter = new CartGoodsAdapter(checkedItems, false);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderRecyclerView.setAdapter(orderAdapter);

        double total = 0;
        for (CarResponse.DataBean item : checkedItems) {
            total += item.getSellingPrice() * item.getGoodsCount();
        }

        totalText.setText("总计：￥" + total);

        backtoCart.setOnClickListener((v -> {
            rootLayout.removeAllViews();
            checkcartGoodsData(nogoodsView,havegoodsView);
        }));

        submitBtn.setOnClickListener(v -> {

            Toast toast = Toast.makeText(getContext(), "请选择支付方式", Toast.LENGTH_SHORT);
            toast.show();
            //1000 毫秒（1 秒）后取消
            new Handler(Looper.getMainLooper()).postDelayed(() -> toast.cancel(), 1000);

            // TODO: 发送到服务端的提交逻辑
            showPaymentDialog();//弹出微信支付宝支付界面
//            getOrderNo();//得到订单号

            //POST收货地址和商品列表
            new Thread(() -> {
                try {
                    URL url = new URL("http://115.158.64.84:28019/api/v1/saveOrder");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.setRequestProperty("token", token);
                    conn.setDoOutput(true);

                    // 构建请求体
                    JSONObject requestBody = new JSONObject();
                    requestBody.put("addressId", addressId);

                    JSONArray array = new JSONArray();
                    for (CarResponse.DataBean item : cartGoodsData) {
                        if (item.isChecked()) {
                            array.put(item.getCartItemId());
                        }
                    }
                    requestBody.put("cartItemIds", array);


                    // 写入请求体
                    OutputStream os = conn.getOutputStream();
                    os.write(requestBody.toString().getBytes("UTF-8"));
                    os.flush();
                    os.close();

                    if(conn.getResponseCode() == 200){
                        System.out.println(requestBody);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            for (CarResponse.DataBean item : cartGoodsData) {
                if (item.isChecked()) {
                    int cartItemId = item.getCartItemId(); // 确保这是购物车项 ID，不是商品 ID
                    deleteCartItem(cartItemId);
                }
            }

        });
    }


    private void showPaymentDialog() {
        // 创建自定义View
        View dialogView = LayoutInflater.from(rootLayout.getContext())
                .inflate(R.layout.dialog_payment, null);

        // 初始化支付按钮
        Button btnAlipay = dialogView.findViewById(R.id.btn_alipay);
        Button btnWechat = dialogView.findViewById(R.id.btn_wechat);

        // 创建对话框
        AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.PaymentDialogTheme)
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
            Toast.makeText(getContext(),"请前往订单页面确认支付",Toast.LENGTH_LONG).show();
            rootLayout.removeAllViews();
            checkcartGoodsData(nogoodsView,havegoodsView);
            dialog.dismiss();
        });

        btnWechat.setOnClickListener(v -> {
            payType = 2;
            Toast.makeText(getContext(),"请前往订单页面确认支付",Toast.LENGTH_LONG).show();
            rootLayout.removeAllViews();
            checkcartGoodsData(nogoodsView,havegoodsView);
            dialog.dismiss();

        });
        dialog.show();
    }
    private void deleteCartItem(int cartItemId) {
        new Thread(() -> {
            try {
                URL url = new URL("http://115.158.64.84:28019/api/v1/shop-cart/" + cartItemId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("token", token); // 添加 token 认证
                conn.connect();

                int code = conn.getResponseCode();
                if (code == 200) {
                    Log.d("DeleteCartItem", "删除成功: ID=" + cartItemId);
                } else {
                    Log.e("DeleteCartItem", "删除失败: ID=" + cartItemId + ", Code=" + code);
                }

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


//    获取订单号
    private void getOrderNo(){
        new Thread(()->{
            try {
                URL url = new URL("http://115.158.64.84:28019/api/v1/order");
                HttpURLConnection cn = (HttpURLConnection) url.openConnection();
                cn.setRequestMethod("GET");
                cn.setRequestProperty("token", token);
                System.out.println("getOrderNo"+token);
                if(cn.getResponseCode() == 200){
                    BufferedReader reader = new BufferedReader(new InputStreamReader(cn.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    reader.close();

                    JSONObject object = new JSONObject(result.toString());
                    JSONObject object2 = object.getJSONObject("data");
                    System.out.println(object);
                    System.out.println(object2);
                    JSONArray list = object2.getJSONArray("list");
                    if (list.length() > 0) {
                        JSONObject orderJson = list.getJSONObject(0);

                        order.setOrderNo(orderJson.getString("orderNo"));
                        order.setOrderStatusString(orderJson.getString("orderStatusString"));
                        order.setPayType(orderJson.getString("payType"));
                        order.setTotalPrice(orderJson.getInt("totalPrice"));
                        order.setOrderStatus(orderJson.getString("orderStatus"));
                        order.setCreateTime(orderJson.getString("createTime"));

                        orderNo = order.getOrderNo(); // 获取订单号

                        System.out.println("订单号为：" + orderNo);
                    } else {
                        System.out.println("订单列表为空，未能获取订单号");
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();

    }
    private void toPayOrders(){
        new Thread(()-> {
                try {
                    URL url = new URL("http://115.158.64.84:28019/api/v1/paySuccess"+"?orderNo="+order.getOrderNo()+"&payType="+payType);
                    HttpURLConnection cn = (HttpURLConnection) url.openConnection();
                    cn.setRequestMethod("GET");
                    cn.setRequestProperty("token", token);
                    System.out.println(cn.getResponseCode());
                    System.out.println(orderNo);
                    if(cn.getResponseCode()==200){
                        BufferedReader reader = new BufferedReader(new InputStreamReader(cn.getInputStream()));
                        StringBuilder result = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }
                        reader.close();
                        JSONObject object = new JSONObject(result.toString());
                        System.out.println(object.getInt("resultCode"));
                        if(object.getInt("resultCode")==200){

                        }
                    }
                }catch (Exception e){
                    throw new RuntimeException(e);
                }

        }).start();
    }
}
