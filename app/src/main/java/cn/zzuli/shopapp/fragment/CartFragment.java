package cn.zzuli.shopapp.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.zzuli.shopapp.R;

public class CartFragment extends Fragment {

    //token:f5b4addfca9166db551fee4d8cec8d6d
    private final String CART_URL = "http://115.158.64.84:28019/api/v1/shop-cart";
    private ViewGroup rootLayout;
    private String token;

    private Handler handler = new Handler(Looper.getMainLooper());

    public CartFragment() {
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
        // 异步判断购物车是否为空
        View nogoodsView = inflater.inflate(R.layout.nogoods,rootLayout,false);
        View havegoodsView = inflater.inflate(R.layout.havegoods,rootLayout,false);
        //给nogoods界面添加按钮点击事件
        Button nogood_button = nogoodsView.findViewById(R.id.nogoods_button);
        nogood_button.setOnClickListener( v -> {
            Toast.makeText(getContext(),"Click",Toast.LENGTH_SHORT).show();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainerView,new HomeFragment());
            transaction.commit();
            TabLayout tabLayout = requireActivity().findViewById(R.id.tabLayout);
            if (tabLayout != null) {
                tabLayout.getTabAt(0).select(); // 假设 0 是“首页”tab
            }
        });

        checkCartData(nogoodsView,havegoodsView);
        return root;
    }

    private void checkCartData(View view1,View view2) {
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

}
