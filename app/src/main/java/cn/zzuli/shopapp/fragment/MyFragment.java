// MyFragment.java（完整版）
package cn.zzuli.shopapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import cn.zzuli.shopapp.AboutUsActivity;
import cn.zzuli.shopapp.AccountActivity;
import cn.zzuli.shopapp.R;

public class MyFragment extends Fragment {

    // 保持原有参数处理方法
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

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

        // 关于我们的跳转
        RelativeLayout AboutAs=view.findViewById(R.id.AboutAs);
        AboutAs.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(),AboutUsActivity.class);
            startActivity(intent);
        });

        // appended by LWH
        // 账号管理跳转
        RelativeLayout rela_account_management=view.findViewById(R.id.account_management);
        rela_account_management.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AccountActivity.class);
            startActivity(intent);
        });

        return view;

    }
}