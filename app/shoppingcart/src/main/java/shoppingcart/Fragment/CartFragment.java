package com.example.shoppingcart.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shoppingcart.R;

public class CartFragment extends Fragment {

    //两个窗口,一个是有商品时显示的界面,一个是没有商品时显示的界面
    private RecyclerView recyclerView;
    private View emptyView;
    private View nonEmptyView;

    //两个参数,用于购买商品时传递商品id和商品数量,用bundle存储.
    private static String PARAM1 = "param1";
    private static String PARAM2 = "param2";
    private String param1;
    private String param2;

    //构造函数
    public CartFragment() {

    }
    public static CartFragment newInstance(String param1,String param2){
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        args.putString(PARAM1,param1);
        args.putString(PARAM2,param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            param1 = getArguments().getString(PARAM1);
            param2 = getArguments().getString(PARAM2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.nogoods,container,false);
    }

    @Nullable


    @Override
    public View onCreateView()

    private void loadCartData() {
        // 发起 API 请求获取购物车数据，下面是伪代码示例
        ApiClient.getCartItems(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                List<CartItem> items = response.body();
                if (items == null || items.isEmpty()) {
                    showEmptyView();
                } else {
                    showNonEmptyView(items);
                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                showEmptyView(); // 或者显示错误提示
            }
        });
    }

    private void showEmptyView() {
        emptyView.setVisibility(View.VISIBLE);
        nonEmptyView.setVisibility(View.GONE);
    }

    private void showNonEmptyView(List<CartItem> items) {
        emptyView.setVisibility(View.GONE);
        nonEmptyView.setVisibility(View.VISIBLE);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new CartAdapter(items));
    }
}
