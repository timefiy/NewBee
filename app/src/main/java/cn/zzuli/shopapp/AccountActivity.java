package cn.zzuli.shopapp;

// created by LWH
// used for account_management.xml

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import cn.zzuli.shopapp.view.TopBar;

public class AccountActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_management);

        TopBar topBar = findViewById(R.id.top_bar_account_management);
        topBar.setTitle("账号管理", 20);
    }
}
