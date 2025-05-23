//package cn.zzuli.shopapp.fragment;
//
//import android.os.Bundle;
//
//import androidx.fragment.app.Fragment;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import java.net.URL;
//
//import cn.zzuli.shopapp.R;
/// **
// * A simple {@link Fragment} subclass.
// * Use the {@link CartFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
//public class CartFragment extends Fragment {
//
//    String CARTURL = "http://115.158.64.84:28019/api/v1/shop-cart";
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    public CartFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment CartFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static CartFragment newInstance(String param1, String param2) {
//        CartFragment fragment = new CartFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        //替换xml布局文件为 nogoods.xml
//        return inflater.inflate(R.layout.nogoods, container, false);
//    }
//}

package cn.zzuli.shopapp.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

        // 异步判断购物车是否为空
        checkCartData(inflater);

        return root;
    }

    private void checkCartData(LayoutInflater inflater) {
        new Thread(() -> {
            try {
                URL url = new URL(CART_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                // 从 SharedPreferences 获取 token
                SharedPreferences info = requireActivity().getSharedPreferences("info", MODE_PRIVATE);
                String token = info.getString("token", "b744d31ce6635bd22701af1ad750e8cc");

                conn.setRequestProperty("token",token);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                conn.disconnect();

                JSONObject jsonObject = new JSONObject(result.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("data");


                if (jsonArray.length() == 0) {
                    showLayout(inflater, R.layout.nogoods);  // 空购物车
                } else {
                    showLayout(inflater, R.layout.havegoods); // 有商品
                }

            } catch (Exception e) {
                Log.e("CartCheck", "发生异常: " + e.getClass().getName() + " - " + e.getMessage());
                e.printStackTrace();
                showLayout(inflater, R.layout.nogoods);
            }

        }).start();
    }

    private void showLayout(LayoutInflater inflater, int layoutRes) {
        handler.post(() -> {
            rootLayout.removeAllViews();
            View layout = inflater.inflate(layoutRes, rootLayout, false);
            rootLayout.addView(layout);
        });
    }
}
