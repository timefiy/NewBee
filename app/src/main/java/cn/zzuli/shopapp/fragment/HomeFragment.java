package cn.zzuli.shopapp.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import cn.zzuli.shopapp.R;
import cn.zzuli.shopapp.RegisterActivity;
import cn.zzuli.shopapp.SearchActivity;

public class HomeFragment extends Fragment {

//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    public HomeFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment HomeFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static HomeFragment newInstance(String param1, String param2) {
//        HomeFragment fragment = new HomeFragment();
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
    private boolean isLogin=false;//是否登录
    private LinearLayout hometop;
    private ImageView iv_home_left;
    private TextView tv_home_login;
    private TextView tv_home_search;
    private ImageView iv_home_login;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ScrollView sv_home=view.findViewById(R.id.sv_home);
        hometop = view.findViewById(R.id.home_top);
        iv_home_left=view.findViewById(R.id.iv_home_left);
        tv_home_login=view.findViewById(R.id.tv_home_login);
        tv_home_search=view.findViewById(R.id.tv_home_search);
        iv_home_login=view.findViewById(R.id.iv_home_login);
        if(isLogin){
            tv_home_login.setVisibility(View.GONE);
            iv_home_login.setVisibility(View.VISIBLE);
        }else{
            iv_home_login.setVisibility(View.GONE);
            tv_home_login.setVisibility(View.VISIBLE);
        }
        tv_home_login.setOnClickListener(v -> {
            Intent intent=new Intent(getContext(), RegisterActivity.class);
            startActivityForResult(intent,100);
        });
        tv_home_search.setOnClickListener(v -> {
            Intent intent=new Intent(getContext(), SearchActivity.class);
            startActivity(intent);
        });
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
                    tv_home_login.setTextColor(getResources().getColor(R. color. top));
                    iv_home_login.setImageResource(R.drawable.ic_person);
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100){
            int result = data.getIntExtra("result", 0);
            if(result>0){
                isLogin=true;
                tv_home_login.setVisibility(View.GONE);
                iv_home_login.setVisibility(View.VISIBLE);
            }
        }
    }
}