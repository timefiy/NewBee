package cn.zzuli.shopapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.zzuli.shopapp.AboutUsActivity;
import cn.zzuli.shopapp.AccountActivity;
import cn.zzuli.shopapp.AddressActivity;
import cn.zzuli.shopapp.MainActivity;
import cn.zzuli.shopapp.MyOrderActivity;
import cn.zzuli.shopapp.R;

public class MyFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    // 用户信息展示控件
    private TextView tvNickname, tvAccount;
    private TextView etSignature;
    private String token; // 存储用户 Token

    public static MyFragment newInstance(String param1, String param2) {
        MyFragment fragment = new MyFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, container, false);

        // 绑定用户信息控件
        tvNickname = view.findViewById(R.id.tv_nickname);
        tvAccount = view.findViewById(R.id.tv_account);
        etSignature = view.findViewById(R.id.et_signature);

        // 获取 Token
        SharedPreferences sp = requireContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        token = sp.getString("token", "");

        if (token.isEmpty()) {
            Toast.makeText(requireContext(), "请先登录", Toast.LENGTH_SHORT).show();
            // 实际开发中应跳转登录界面，例如：
            // startActivity(new Intent(getActivity(), LoginActivity.class));
        } else {
            loadProfile();
        }

        // 绑定点击事件控件
        RelativeLayout aboutAs = view.findViewById(R.id.AboutAs);
        RelativeLayout toAddress = view.findViewById(R.id.rl_to_address);
        RelativeLayout toOrders = view.findViewById(R.id.rl_to_orders);
        RelativeLayout relaAccountManagement = view.findViewById(R.id.account_management);
        ImageView backBtn = view.findViewById(R.id.imageView6);

        // 关于我们
        aboutAs.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AboutUsActivity.class));
        });

        // 地址管理
        toAddress.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AddressActivity.class));
        });

        // 订单管理
        toOrders.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), MyOrderActivity.class));
        });

        // 账号管理
        relaAccountManagement.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AccountActivity.class));
        });

        // 返回首页
        backBtn.setOnClickListener(v -> {
            TabLayout tabLayout = requireActivity().findViewById(R.id.tabLayout);
            ((MainActivity) requireActivity()).toFragment(0);
            if (tabLayout != null) {
                tabLayout.getTabAt(0).select();
            }
        });

        return view; // 确保此处返回视图
    }

    private void loadProfile() {
        new Thread(() -> {
            try {
                URL url = new URL("http://115.158.64.84:28019/api/v1/user/info");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("token", token);

                if (conn.getResponseCode() == 200) {
                    InputStream is = conn.getInputStream();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = is.read(buffer)) > -1) {
                        baos.write(buffer, 0, len);
                    }
                    String response = baos.toString();
                    JSONObject json = new JSONObject(response);
                    JSONObject data = json.getJSONObject("data");

                    final String nickname = data.getString("nickName");
                    final String account = data.getString("loginName");
                    final String signature = data.getString("introduceSign");

                    requireActivity().runOnUiThread(() -> {
                        tvNickname.setText(nickname);
                        tvAccount.setText(account);
                        etSignature.setText(signature);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "加载失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}