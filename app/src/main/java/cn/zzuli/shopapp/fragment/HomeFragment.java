package cn.zzuli.shopapp.fragment;

// created by LWH

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.zzuli.shopapp.R;
import cn.zzuli.shopapp.RegisterActivity;
import cn.zzuli.shopapp.SearchActivity;
import cn.zzuli.shopapp.entity.ApiResponseHome;
import cn.zzuli.shopapp.entity.Carousel;
import cn.zzuli.shopapp.entity.Goods;
import cn.zzuli.shopapp.entity.HomeData;
import cn.zzuli.shopapp.entity.Icon;
import cn.zzuli.shopapp.adapter.GoodsAdapter;


public class HomeFragment extends Fragment {

    private String Home_apiurl = "http://115.158.64.84:28019/api/v1/index-infos";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // 用于更改相关组件的参数
    private String mParam1;
    private String mParam2;

    private boolean isLogin=false;//是否登录
    private LinearLayout hometop;
    private ImageView iv_home_left;
    private TextView tv_home_login;
    private TextView tv_home_search;
    private ImageView iv_home_login;
    private Banner banner;
    private GridView homeNavGridView;// 网格视图，导航栏

    private GridView hotgoods_gri;
    private GridView newgoods_gri;
    private GridView recommendgoods_gri;

//    private ApiResponseHome apiResponseHome;
//    private HomeData homeData;
    private List<Carousel> carousels;
    private List<Goods> hotGoodses;
    private List<Goods> newGoodses;
    private List<Goods> recommendGoodses;



    private static final String CATEGORY_JSON_DATA = "[" +
    "{\"name\": \"新蜂超市\", \"imgUrl\": \"https://s.yezgea02.com/1604041127880/%E8%B6%85%E5%B8%82%402x.png\", \"categoryId\": 100001}," +
    "{\"name\": \"新蜂服饰\", \"imgUrl\": \"https://s.yezgea02.com/1604041127880/%E6%9C%8D%E9%A5%B0%402x.png\", \"categoryId\": 100003}," +
    "{\"name\": \"全球购\", \"imgUrl\": \"https://s.yezgea02.com/1604041127880/%E5%85%A8%E7%90%83%E8%B4%AD%402x.png\", \"categoryId\": 100002}," +
    "{\"name\": \"新蜂生鲜\", \"imgUrl\": \"https://s.yezgea02.com/1604041127880/%E7%94%9F%E9%B2%9C%402x.png\", \"categoryId\": 100004}," +
    "{\"name\": \"新蜂到家\", \"imgUrl\": \"https://s.yezgea02.com/1604041127880/%E5%88%B0%E5%AE%B6%402x.png\", \"categoryId\": 100005}," +
    "{\"name\": \"充值缴费\", \"imgUrl\": \"https://s.yezgea02.com/1604041127880/%E5%85%85%E5%80%BC%402x.png\", \"categoryId\": 100006}," +
    "{\"name\": \"9.9元拼\", \"imgUrl\": \"https://s.yezgea02.com/1604041127880/9.9%402x.png\", \"categoryId\": 100007}," +
    "{\"name\": \"领劵\", \"imgUrl\": \"https://s.yezgea02.com/1604041127880/%E9%A2%86%E5%88%B8%402x.png\", \"categoryId\": 100008}," +
    "{\"name\": \"省钱\", \"imgUrl\": \"https://s.yezgea02.com/1604041127880/%E7%9C%81%E9%92%B1%402x.png\", \"categoryId\": 100009}," +
    "{\"name\": \"全部\", \"imgUrl\": \"https://s.yezgea02.com/1604041127880/%E5%85%A8%E9%83%A8%402x.png\", \"categoryId\": 100010}" +
    "]";

    public HomeFragment() {
       // 必须的构造函数
    }

   // 重命名并更改参数的类型和数量
    public static HomeFragment newInstance(String param1, String param2) {
        // 创建实例
        HomeFragment fragment = new HomeFragment();
        // 创建Bundle对象并存入参数
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override // 生命周期方法
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 初始化数据
        initData();

        // 加载布局
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ScrollView sv_home=view.findViewById(R.id.sv_home);
        hometop = view.findViewById(R.id.home_top);
        iv_home_left=view.findViewById(R.id.iv_home_left);
        tv_home_login=view.findViewById(R.id.tv_home_login);
        tv_home_search=view.findViewById(R.id.tv_home_search);
        iv_home_login=view.findViewById(R.id.iv_home_login);
        // 轮播图
        banner = view.findViewById(R.id.banner_text);
        // 导航栏
        homeNavGridView = view.findViewById(R.id.home_nav);

        // 初始化热门商品和推荐商品的 GridView
        hotgoods_gri = view.findViewById(R.id.home_hotgoods);
        newgoods_gri = view.findViewById(R.id.home_newgoods); // 确保新品上线的 GridView 也在这里初始化
        recommendgoods_gri = view.findViewById(R.id.home_recommendgoods);

        // 初始化列表
        carousels = new ArrayList<>();
        hotGoodses = new ArrayList<>();
        newGoodses = new ArrayList<>();
        recommendGoodses = new ArrayList<>();

        if(isLogin){
            tv_home_login.setVisibility(View.GONE); // 隐藏"登录"
            iv_home_login.setVisibility(View.VISIBLE); // 显示头像
        }else{
            iv_home_login.setVisibility(View.GONE); // 隐藏头像
            tv_home_login.setVisibility(View.VISIBLE); // 显示"登录"
        }

        // "登录"的跳转
        tv_home_login.setOnClickListener(v -> {
            Intent intent=new Intent(getContext(), RegisterActivity.class);
            startActivityForResult(intent,100);
        });

        // "搜索"跳转
        tv_home_search.setOnClickListener(v -> {
            Intent intent=new Intent(getContext(), SearchActivity.class);
            startActivity(intent);
        });

        // 顶部导航栏颜色的变动
        sv_home.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY>200){
                    hometop.setBackgroundResource(R.color.top);
                    iv_home_left.setImageResource(R.drawable.ic_menu);
                    tv_home_login.setTextColor(Color.WHITE);
                    iv_home_login.setImageResource(R.drawable.ic_person_white);
                }else {
                    hometop.setBackgroundColor(Color.TRANSPARENT);
                    iv_home_left.setImageResource(R.drawable.ic_menu2);
                    tv_home_login.setTextColor(getResources().getColor(R.color.top));
                    iv_home_login.setImageResource(R.drawable.ic_person);
                }
            }
        });

        // 将json字符串转换为list
        List<Icon> iconList = parseJsonData(CATEGORY_JSON_DATA);

        // 自定义导航栏相关的适配器
        HomeNavAdapter adapter = new HomeNavAdapter(getActivity(), iconList);
        homeNavGridView.setAdapter(adapter);

        return view;
    }

    private void initData() {
        // 创建新线程：将网络请求放在子线程总
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(Home_apiurl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000); // 设置连接超时
                    conn.setReadTimeout(5000);    // 设置读取超时
                    conn.connect();

                    int responseCode = conn.getResponseCode();
//                    Log.d("NETWORK", "Response Code: " + responseCode);
                    
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // 读取响应数据
                        //  Java 中用于高效读取字符输入流的类
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        // 高效拼接字符串。
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();
                        
                        // 打印原始响应数据
//                        Log.d("NETWORK", "Raw Response: " + response.toString());

                        // 切换到主线程更新UI
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // 在这里处理响应数据并更新UI
                                    parseJson(response.toString());
                                    updateBanner(); // 添加更新轮播图的方法
                                } catch (Exception e) {
                                    Log.e("UI_ERROR", "Error updating UI: " + e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
//                    else {
//                        Log.e("HTTP_ERROR", "Response Code: " + responseCode);
                        // 读取错误流
//                        BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
//                        StringBuilder errorResponse = new StringBuilder();
//                        String errorLine;
//                        while ((errorLine = errorReader.readLine()) != null) {
//                            errorResponse.append(errorLine);
//                        }
//                        errorReader.close();
//                        Log.e("HTTP_ERROR", "Error Response: " + errorResponse.toString());
//                    }
                } catch (Exception e) {
//                    Log.e("NETWORK_ERROR", "Network request failed: " + e.getMessage());
                    e.printStackTrace();
                    // 切换到主线程显示错误信息
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "网络请求失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    // 添加更新轮播图的方法
    private void updateBanner() {
        if (carousels != null && !carousels.isEmpty()) {
            List<String> imageUrls = new ArrayList<>();
            for (Carousel carousel : carousels) {
                imageUrls.add(carousel.getCarouselUrl());
            }
            
            banner.setAdapter(new BannerImageAdapter<String>(imageUrls) {
                @Override
                public void onBindView(BannerImageHolder holder, String data, int position, int size) {
                    Glide.with(holder.itemView)
                            .load(data)
                            .into(holder.imageView);
                }
            }).addBannerLifecycleObserver(this)
              .setIndicator(new CircleIndicator(requireContext()));
        }
    }

    // 定义相关json解码方法
    private List<Icon> parseJsonData(String jsonData) {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Icon>>() {}.getType();
        return gson.fromJson(jsonData, listType);
    }

    private void parseJson(String jsonStr) {
        Gson gson = new Gson();
        try {
//            Log.d("JSON", "Parsing JSON: " + jsonStr);
            ApiResponseHome response = gson.fromJson(jsonStr, ApiResponseHome.class);
            if (response != null) {
//                Log.d("JSON", "Response code: " + response.getResultCode());
                if (response.getResultCode() == 200) {
                    HomeData data = response.getData();
                    if (data != null) {
                        Log.d("JSON", "Data received successfully");
                        // 使用空值检查
                        List<Carousel> carouselList = data.getCarousels();
                        if (carouselList != null) {
                            carousels = carouselList;
//                            Log.d("JSON", "Carousels count: " + carouselList.size());
                        }
                        
                        List<Goods> hotGoodsList = data.getHotGoodses();
                        if (hotGoodsList != null) {
                            hotGoodses = hotGoodsList;
//                            Log.d("JSON", "Hot goods count: " + hotGoodsList.size());
                            // 更新热门商品 GridView
                            updateHotGoodsGridView();
                        }
                        
                        List<Goods> newGoodsList = data.getNewGoodses();
                        if (newGoodsList != null) {
                            newGoodses = newGoodsList;
//                            Log.d("JSON", "New goods count: " + newGoodsList.size());
//                            Log.d("JSON", "New goods content: " + newGoodses.toString());
                            // 更新新品上线 GridView
                            updateNewGoodsGridView();
                        }
                        
                        List<Goods> recommendGoodsList = data.getRecommendGoodses();
                        if (recommendGoodsList != null) {
                            recommendGoodses = recommendGoodsList;
//                            Log.d("JSON", "Recommend goods count: " + recommendGoodsList.size());
                            // 更新推荐商品 GridView
                            updateRecommendGoodsGridView();
                        }
                    } else {
                        Log.e("API_ERROR", "HomeData is null");
                    }
                } else {
                    Log.e("API_ERROR", "Response code is not 200: " + response.getResultCode());
                }
            } else {
                Log.e("API_ERROR", "Response is null");
            }
        } catch (JsonSyntaxException e) {
            Log.e("JSON_ERROR", "Failed to parse JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 添加更新新品上线 GridView 的方法
    private void updateNewGoodsGridView() {
        if (newGoodses != null && !newGoodses.isEmpty()) {
            GoodsAdapter newGoodsAdapter = new GoodsAdapter(getContext(), newGoodses) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        // 修改这里，使用 home_goods.xml 布局
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.home_goods, parent, false);
                    }

                    // 修改这里的 ID，匹配 home_goods.xml 中的 ID
                    ImageView goodsImage = convertView.findViewById(R.id.iv_goods_image);
                    TextView goodsName = convertView.findViewById(R.id.tv_goods_name);
                    TextView goodsPrice = convertView.findViewById(R.id.tv_goods_price);

                    Goods goods = newGoodses.get(position);

                    Glide.with(getContext())
                         .load("http://115.158.64.84:28019" + goods.getGoodsCoverImg())
                         .placeholder(R.drawable.ic_category)
                         .error(R.drawable.ic_menu)
                         .into(goodsImage);

                    goodsName.setText(goods.getGoodsName());
                    // 确认 Goods 类中的方法名和数据类型是否正确
                    goodsPrice.setText("￥" + goods.getSellingPrice()); // 假设是 getSellingPrice()

                    return convertView;
                }
            };
            newgoods_gri.setAdapter(newGoodsAdapter);
        }
    }

    // 添加更新热门商品 GridView 的方法
    private void updateHotGoodsGridView() {
        if (hotGoodses != null && !hotGoodses.isEmpty()) {
            BaseAdapter hotGoodsAdapter = new BaseAdapter() {
                @Override
                public int getCount() {
                    return hotGoodses.size();
                }

                @Override
                public Object getItem(int position) {
                    return hotGoodses.get(position);
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.home_goods, parent, false);
                    }

                    ImageView goodsImage = convertView.findViewById(R.id.iv_goods_image);
                    TextView goodsName = convertView.findViewById(R.id.tv_goods_name);
                    TextView goodsPrice = convertView.findViewById(R.id.tv_goods_price);

                    Goods goods = hotGoodses.get(position);

                    Glide.with(getContext())
                         .load("http://115.158.64.84:28019" + goods.getGoodsCoverImg())
                         .placeholder(R.drawable.ic_category)
                         .error(R.drawable.ic_menu)
                         .into(goodsImage);

                    goodsName.setText(goods.getGoodsName());
                    goodsPrice.setText("￥" + goods.getSellingPrice());

                    return convertView;
                }
            };
            hotgoods_gri.setAdapter(hotGoodsAdapter);
        }
    }

    // 添加更新推荐商品 GridView 的方法
    private void updateRecommendGoodsGridView() {
        if (recommendGoodses != null && !recommendGoodses.isEmpty()) {
            BaseAdapter recommendGoodsAdapter = new BaseAdapter() {
                @Override
                public int getCount() {
                    return recommendGoodses.size();
                }

                @Override
                public Object getItem(int position) {
                    return recommendGoodses.get(position);
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.home_goods, parent, false);
                    }

                    ImageView goodsImage = convertView.findViewById(R.id.iv_goods_image);
                    TextView goodsName = convertView.findViewById(R.id.tv_goods_name);
                    TextView goodsPrice = convertView.findViewById(R.id.tv_goods_price);

                    Goods goods = recommendGoodses.get(position);

                    Glide.with(getContext())
                         .load("http://115.158.64.84:28019" + goods.getGoodsCoverImg())
                         .placeholder(R.drawable.ic_category)
                         .error(R.drawable.ic_menu)
                         .into(goodsImage);

                    goodsName.setText(goods.getGoodsName());
                    goodsPrice.setText("￥" + goods.getSellingPrice());

                    return convertView;
                }
            };
            recommendgoods_gri.setAdapter(recommendGoodsAdapter);
        }
    }

    // 自定义的 BaseAdapter：HomeNavAdapter
    private class HomeNavAdapter extends BaseAdapter {

        private Context context;
        private List<Icon> iconList;

        public HomeNavAdapter(Context context, List<Icon> iconList) {
            this.context = context;
            this.iconList = iconList;
        }

        @Override
        public int getCount() {
            return iconList.size();
        }

        @Override
        public Object getItem(int position) {
            return iconList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.home_nav, parent, false);
            }

            ImageView iconImageView = convertView.findViewById(R.id.img_icon);
            TextView iconTextView = convertView.findViewById(R.id.txt_icon);

            Icon icon = iconList.get(position);

            // 加载图片
            Glide.with(context)
                 .load(icon.getImgUrl())
                 .placeholder(R.drawable.ic_category) // Optional placeholder
                 .error(R.drawable.ic_menu) // Optional error image
                 .into(iconImageView);

            //加载文字
            iconTextView.setText(icon.getName());

            return convertView;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 && data != null){
            int result = data.getIntExtra("result", 0);
            if(result>0){
                isLogin=true;
                tv_home_login.setVisibility(View.GONE);
                iv_home_login.setVisibility(View.VISIBLE);
            }
        }
    }
}