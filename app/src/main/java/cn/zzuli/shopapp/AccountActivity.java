package cn.zzuli.shopapp;

// created by LWH
// used for account_management.xml

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import cn.zzuli.shopapp.utils.SecurityUtil;
import cn.zzuli.shopapp.view.TopBar;

public class AccountActivity extends AppCompatActivity {
    private EditText etNickname;
    private EditText etSignature;
    private EditText etPassword;
    private Button btnSave;
    private Button btnLogout;
    private TopBar topBar;
    private String token;
    private String baseUrl = "http://115.158.64.84:28019/api/v1/user/info";

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 200) {
                Toast.makeText(AccountActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                String errorMsg = (String) msg.obj;
                Toast.makeText(AccountActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_management);

        // 初始化视图
        initViews();
        // 获取保存的token
        SharedPreferences info = getSharedPreferences("info", MODE_PRIVATE);
        token = info.getString("token", "");
        // 设置点击事件
        setClickListeners();
    }

    private void initViews() {
        topBar = findViewById(R.id.top_bar_account_management);
        etNickname = findViewById(R.id.Edit_name);
        etSignature = findViewById(R.id.Edit_person_sign);
        etPassword = findViewById(R.id.Edit_password);
        btnSave = findViewById(R.id.btn_save);
        btnLogout = findViewById(R.id.btn_logout);

        topBar.setTitle("账号管理", 20);
    }

    private void setClickListeners() {
        btnSave.setOnClickListener(v -> {
            String nickname = etNickname.getText().toString().trim();
            String signature = etSignature.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            
            if (TextUtils.isEmpty(nickname)) {
                Toast.makeText(this, "昵称不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                return;
            }
            
            updateUserInfo(nickname, signature, password);
        });

        btnLogout.setOnClickListener(v -> {
            // 清除登录信息
            SharedPreferences info = getSharedPreferences("info", MODE_PRIVATE);
            SharedPreferences.Editor edit = info.edit();
            edit.clear();
            edit.apply();
            
            // 返回登录状态
            setResult(RESULT_OK);
            finish();
        });
    }

    private void updateUserInfo(String nickname, String signature, String password) {
        new Thread(() -> {
            try {
                URL url = new URL(baseUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("token", token);
                conn.setDoOutput(true);

                // 构建请求体
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("nickName", nickname);
                jsonBody.put("introduceSign", signature);
                jsonBody.put("passwordMd5", SecurityUtil.md5(password));

                // 发送请求
                OutputStream os = conn.getOutputStream();
                os.write(jsonBody.toString().getBytes(StandardCharsets.UTF_8));
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    handler.sendEmptyMessage(200);
                } else {
                    // 读取错误信息
                    java.io.BufferedReader reader = new java.io.BufferedReader(
                            new java.io.InputStreamReader(conn.getErrorStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject errorJson = new JSONObject(response.toString());
                    String errorMsg = errorJson.optString("message", "更新失败");
                    
                    Message msg = new Message();
                    msg.what = responseCode;
                    msg.obj = errorMsg;
                    handler.sendMessage(msg);
                }
            } catch (Exception e) {
                Message msg = new Message();
                msg.what = 500;
                msg.obj = "网络请求失败：" + e.getMessage();
                handler.sendMessage(msg);
            }
        }).start();
    }
}
