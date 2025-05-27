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
import cn.zzuli.shopapp.MainActivity;
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
        // 1. 加载 Fragment 布局
        View view = inflater.inflate(R.layout.fragment_my, container, false);

        // 2. 绑定用户信息展示控件
        tvNickname = view.findViewById(R.id.tv_nickname);
        tvAccount = view.findViewById(R.id.tv_account);
        etSignature = view.findViewById(R.id.et_signature);

        // 3. 从 SharedPreferences 获取 Token
        SharedPreferences sp = requireContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        token = sp.getString("token", "");

        if (token == null) {
            // Token 为空：提示登录（需补充登录跳转逻辑）
            Toast.makeText(requireContext(), "请先登录", Toast.LENGTH_SHORT).show();
        } else {
            // Token 有效：加载用户信息
            loadProfile();
        }

        // 4. 「关于我们」点击事件（保留原有逻辑）
        RelativeLayout aboutAs = view.findViewById(R.id.AboutAs);
        aboutAs.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AboutUsActivity.class);
            startActivity(intent);
        });

        // 5. 返回首页逻辑（保留原有逻辑）
        TabLayout tabLayout = requireActivity().findViewById(R.id.tabLayout);
        ImageView backBtn = view.findViewById(R.id.imageView6);
        backBtn.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).toFragment(0);
            if (tabLayout != null) {
                tabLayout.getTabAt(0).select();
            }
        });

        return view;
    }

    // 新增：加载用户信息的网络请求方法
    private void loadProfile() {
        new Thread(() -> {
            try {
                // 注意：URL 需与后端接口一致！原 URL 中的 & 可能是输入错误，需修正
                URL url = new URL("http://115.158.64.84:28019/api/v1/user/info");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("token", token); // 携带 Token 验证

                if (conn.getResponseCode() == 200) {

                    InputStream inputStream = conn.getInputStream();
                    //流转字符串
                    byte[] bytes=new byte[1024];
                    ByteArrayOutputStream baos=new ByteArrayOutputStream();
                    int len=0;
                    while((len=inputStream.read(bytes))>0){
                        baos.write(bytes,0,len);
                    }
                    System.out.println(baos.toString());
                    JSONObject obj=new JSONObject(baos.toString());
                    JSONObject object = obj.getJSONObject("data");

                    String introduceSign = object.getString("introduceSign");
                    String loginName = object.getString("loginName");
                    String nickName = object.getString("nickName");

                    // 响应成功：解析 JSON 并更新 UI（示例用固定值，实际需解析真实数据）
                    requireActivity().runOnUiThread(() -> {
                        tvNickname.setText(nickName);
                        tvAccount.setText(loginName);
                        etSignature.setText(introduceSign);
                    });
                }
            } catch (Exception e) {
                // 网络异常：捕获并提示
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(),
                            "网络请求错误：" + e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                });
            }
        }).start();
    }
}