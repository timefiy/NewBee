package cn.zzuli.shopapp;

import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;

import cn.zzuli.shopapp.fragment.CartFragment;
import cn.zzuli.shopapp.fragment.CategoryFragment;
import cn.zzuli.shopapp.fragment.HomeFragment;
import cn.zzuli.shopapp.fragment.MyFragment;

public class MainActivity extends AppCompatActivity {
    private HomeFragment homeFragment;
    private CategoryFragment categoryFragment;
    private CartFragment cartFragment;
    private MyFragment myFragment;
    private TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        homeFragment=new HomeFragment();
        categoryFragment=new CategoryFragment();
        cartFragment=new CartFragment();
        myFragment=new MyFragment();
        tabLayout=findViewById(R.id.tabLayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int id = tab.getPosition();
                toFragment(id);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * 设置消息红点
     */
    public void setRedPoints(int num){
        TabLayout.Tab item = tabLayout.getTabAt(2);
        BadgeDrawable drawable = item.getOrCreateBadge();
        if (drawable == null) drawable = BadgeDrawable.create(this);
        if(num>0) {
            drawable.setBackgroundColor(Color.RED);//背景颜色
            drawable.setMaxCharacterCount(3);//消息最大长度
            drawable.setNumber(num);//消息数量
            drawable.setBadgeTextColor(Color.WHITE);//消息文字颜色
        }else{
            drawable.setVisible(false);
        }
    }

    public void toFragment(int id) {
        Fragment fragment=null;
        if (id == 0) {
            fragment=homeFragment;
        }else if(id ==1){
            fragment=categoryFragment;
        }else if(id ==2){
            fragment=cartFragment;
        }else if(id ==3){
            fragment=myFragment;
        }
        if(fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment)
                    .commit();
        }
    }
}