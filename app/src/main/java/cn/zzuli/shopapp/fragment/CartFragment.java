package cn.zzuli.shopapp.fragment;

import android.content.Context;
import android.graphics.Color;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.zzuli.shopapp.R;
import cn.zzuli.shopapp.adapter.CartGoodsAdapter;
import cn.zzuli.shopapp.adapter.GoodsAdapter;
import cn.zzuli.shopapp.entity.CarResponse;

public class CartFragment extends Fragment {

    private final String CART_URL = "http://115.158.64.84:28019/api/v1/shop-cart";
    private ViewGroup rootLayout;
    private String token;
    private CartGoodsAdapter adapter;
    private CheckBox checkAll;
    private TextView tvTotal;
    private RecyclerView recyclerView;
    private List<CarResponse.DataBean> cartGoodsData;
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
        View nogoodsView = inflater.inflate(R.layout.nogoods,rootLayout,false);
        View havegoodsView = inflater.inflate(R.layout.havegoods,rootLayout,false);

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
                        adapter = new CartGoodsAdapter(new ArrayList<>());
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

    //加载生成订单页面
    private void showOrderConfirmationLayout(List<CarResponse.DataBean> checkedItems) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View orderView = inflater.inflate(R.layout.creat_order, null);

        // 替换 root 布局（比如原本 havegoodsView 所在的容器）
        ViewGroup rootContainer = (ViewGroup) rootLayout.findViewById(R.id.frameLayout3); // 你原来布局的容器 id
        rootContainer.removeAllViews();
        rootContainer.addView(orderView);

        TextView nameText = orderView.findViewById(R.id.recipientName);
        TextView addressText = orderView.findViewById(R.id.recipientAddress);
        TextView totalText = orderView.findViewById(R.id.totalText);
        LinearLayout itemsContainer = orderView.findViewById(R.id.orderLayout);
        Button submitBtn = orderView.findViewById(R.id.btn_confirm_order);

        // 设置地址（可以从本地或网络获取）
        addressText.setText("收货地址：河南省新乡市学院路...");

        // 添加每个商品信息
        double total = 0;
        for (CarResponse.DataBean item : checkedItems) {
            TextView itemText = new TextView(getContext());
            itemText.setText(item.getGoodsName() + " x" + item.getGoodsCount() + " ￥" + item.getSellingPrice());
            itemsContainer.addView(itemText);
            total += item.getSellingPrice() * item.getGoodsCount();
        }

        totalText.setText("总计：￥" + total);

        submitBtn.setOnClickListener(v -> {
            Toast.makeText(getContext(), "还未实现", Toast.LENGTH_SHORT).show();
            // TODO: 发送到服务端的提交逻辑
        });
    }
    private void loadDefaultAddress() {
        new Thread(() -> {
            try {
                URL url = new URL("http://115.158.64.84:28019/api/v1/address{addressId}"); // 换成你的实际接口
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("token", token); // 如果需要登录 token

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
                    String address = data.getString("fullAddress");

                    requireActivity().runOnUiThread(() -> {
                        TextView nameView = getView().findViewById(R.id.recipientName);
                        TextView addressView = getView().findViewById(R.id.recipientAddress);
                        nameView.setText(name + "  " + phone);
                        addressView.setText(address);
                    });
                }

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
