package cn.zzuli.shopapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import cn.zzuli.shopapp.adapter.GoodAdapter;
import cn.zzuli.shopapp.adapter.Good;

public class SearchActivity extends AppCompatActivity {

    private EditText etKeyword;
    private TextView btnSearch;
    private ImageView ivBack;
    private RecyclerView rvGoodsList;
    private TabLayout tabLayout;

    private List<Good> goodsList = new ArrayList<>();
    private GoodAdapter adapter;

    private int selectedCategoryId = 0; // 默认“全部”

    private Handler handler = new Handler(msg -> {
        if (msg.what == 200) {
            adapter.notifyDataSetChanged();
        } else {
            String message = msg.obj != null ? msg.obj.toString() : "未知错误";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
        return true;
    });

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // 初始化控件
        etKeyword = findViewById(R.id.search);
        btnSearch = findViewById(R.id.button);
        ivBack = findViewById(R.id.imageView2);
        rvGoodsList = findViewById(R.id.RecycleView);
        tabLayout = findViewById(R.id.tl_main);

        // 初始化 RecyclerView
        rvGoodsList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GoodAdapter(goodsList);
        rvGoodsList.setAdapter(adapter);

        setupTabLayout();

        // 返回点击事件
        ivBack.setOnClickListener(v -> finish());

        //输入框把图标覆盖掉
        ImageView imageView = findViewById(R.id.imageView12); // 提前获取引用，避免重复查找
        etKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 文本改变前的回调
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 文本正在改变时的回调
            }
            @Override
            public void afterTextChanged(Editable s) {
                // 文本改变后的回调
                imageView.setVisibility(View.GONE); // 隐藏 ImageView
            }
        });

        // 搜索点击事件
        btnSearch.setOnClickListener(v -> {
            performSearch();
        });

    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedCategoryId = tab.getPosition(); // 0: 推荐, 1: 新品, 2: 价格
                performSearch(); // 切换 Tab 时自动搜索
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void performSearch() {
        String keyword = etKeyword.getText().toString().trim();
        String token = getAuthToken();

        if (token == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> fetchSearchResults(token, keyword, selectedCategoryId)).start();
    }

    private String getAuthToken() {
        SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);
        return sp.getString("token", null);
    }

    private void fetchSearchResults(String token, String keyword, int categoryId) {
        try {
            String orderBy = "";
            switch (categoryId) {
                case 0:
                    orderBy = ""; // 推荐
                    break;
                case 1:
                    orderBy = "new"; // 新品
                    break;
                case 2:
                    orderBy = "price"; // 价格
                    break;
            }

            String urlString = "http://115.158.64.84:28019/api/v1/search?" +
                    "keyword=" + keyword +
                    "&pageNumber=1" +
                    "&orderBy=" + orderBy;

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("token", token);

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                JSONObject response = readResponse(conn.getInputStream());
                int resultCode = response.getInt("resultCode");

                if (resultCode == 200) {
                    JSONObject data = response.getJSONObject("data");
                    // 获取 list 字段
                    Object listObj = data.get("list");

                    goodsList.clear();

                    if (listObj instanceof JSONArray) {
                        // 如果 list 是 JSONArray
                        JSONArray listArray = (JSONArray) listObj;
                        for (int i = 0; i < listArray.length(); i++) {
                            JSONObject item = listArray.getJSONObject(i);
                            Good goods = new Good(
                                    item.getInt("goodsId"),
                                    item.getString("goodsName"),
                                    item.getString("goodsIntro"),
                                    item.getString("goodsCoverImg"),
                                    item.getDouble("sellingPrice")
                            );
                            goodsList.add(goods);
                        }
                    } else if (listObj instanceof JSONObject) {
                        // 如果 list 是 JSONObject
                        JSONObject item = (JSONObject) listObj;
                        Good goods = new Good(
                                item.getInt("goodsId"),
                                item.getString("goodsName"),
                                item.getString("goodsIntro"),
                                item.getString("goodsCoverImg"),
                                item.getDouble("sellingPrice")
                        );
                        goodsList.add(goods);
                    }

                    handler.sendEmptyMessage(200);
                } else {
                    Message msg = handler.obtainMessage(500);
                    msg.obj = response.optString("message", "未知错误");
                    handler.sendMessage(msg);
                }
            } else if (responseCode == 401) {
                handler.sendMessage(handler.obtainMessage(401, "未授权，请重新登录"));
            } else {
                Message msg = handler.obtainMessage(500);
                msg.obj = "服务器异常：" + responseCode;
                handler.sendMessage(msg);
            }
        } catch (Exception e) {
            Log.e("SearchActivity", "搜索失败：", e);
            Message msg = handler.obtainMessage(500);
            msg.obj = "网络请求失败：" + e.getMessage();
            handler.sendMessage(msg);
        }
    }

    private JSONObject readResponse(InputStream is) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) > 0) {
            baos.write(buffer, 0, len);
        }
        return new JSONObject(baos.toString());
    }
}
