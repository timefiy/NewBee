package cn.zzuli.shopapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import cn.zzuli.shopapp.utils.SecurityUtil;

public class RegisterActivity extends AppCompatActivity {
    private TextView tv_title,tv_switch;
    private EditText et_name,et_pwd;
    private Button btn_register;
    private boolean isLogin=true;
    private String str="http://115.158.64.84:28019/api/v1/user/register";
    private String strLogin="http://115.158.64.84:28019/api/v1/user/login";
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if(msg.what==200){
                Toast.makeText(RegisterActivity.this, "注册成功！请登录！", Toast.LENGTH_SHORT).show();
                isLogin=true;
                toSwith(isLogin);
            }else if(msg.what==201){
                String errorMsg= (String) msg.obj;
                Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
    }

    private void initViews() {
        tv_title=findViewById(R.id.tv_title);
        tv_switch=findViewById(R.id.tv_switch);
        et_name=findViewById(R.id.et_name);
        et_pwd=findViewById(R.id.et_pwd);
        btn_register=findViewById(R.id.btn_register);
        toSwith(isLogin);
        SharedPreferences info = getSharedPreferences("info", MODE_PRIVATE);
        String name1 = info.getString("name", "");
        String pwd1 = info.getString("pwd", "");
        et_name.setText(name1);
        et_pwd.setText(pwd1);
        tv_switch.setOnClickListener(v -> {
            isLogin=!isLogin;
            toSwith(isLogin);
        });
        btn_register.setOnClickListener(v -> {
            String name=et_name.getText().toString();
            String pwd=et_pwd.getText().toString();
            if(TextUtils.isEmpty(name)||TextUtils.isEmpty(pwd)){
                Toast.makeText(this, "用户名或者密码为空！", Toast.LENGTH_SHORT).show();
                return;
            }
            if(isLogin){
                toLogin(name,pwd);
            }else{
                toRegister(name,pwd);
            }
        });
    }

    private void toRegister(String name, String pwd) {
        new Thread(){
            @Override
            public void run() {
                try {
                    //1.准备数据
                    JSONObject object=new JSONObject();
                    object.put("loginName",name);
                    object.put("password",pwd);
                    String params=object.toString();
                    //2.网络请求
                    URL url=new URL(str);
                    HttpURLConnection cn= (HttpURLConnection) url.openConnection();
                    cn.setRequestMethod("POST");
                    cn.setRequestProperty("Content-Type","application/json");
                    cn.setDoOutput(true);
                    OutputStream outputStream = cn.getOutputStream();
                    outputStream.write(params.getBytes(StandardCharsets.UTF_8));
                    outputStream.close();
                    if(cn.getResponseCode()==200){
                        InputStream inputStream = cn.getInputStream();
                        //流转字符串
                        byte[] bytes=new byte[1024];
                        ByteArrayOutputStream baos=new ByteArrayOutputStream();
                        int len=0;
                        while((len=inputStream.read(bytes))>0){
                            baos.write(bytes,0,len);
                        }
                        System.out.println(baos.toString());
                        JSONObject obj=new JSONObject(baos.toString());
                        if(obj.optInt("resultCode",0)==200){
                            //注册成功
                            handler.sendEmptyMessage(200);
                        }else {
                            //注册失败，获取错误信息
                            String errorMsg=obj.optString("message");
                            Message msg=new Message();
                            msg.obj=errorMsg;
                            msg.what=201;
                            handler.sendMessage(msg);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();
    }

    private void toLogin(String name, String pwd) {
        new Thread(){
            @Override
            public void run() {
                try {
                    //1.准备数据
                    JSONObject object=new JSONObject();
                    object.put("loginName",name);
                    object.put("passwordMd5", SecurityUtil.md5(pwd));
                    String params=object.toString();
                    //2.网络请求
                    URL url=new URL(strLogin);
                    HttpURLConnection cn= (HttpURLConnection) url.openConnection();
                    cn.setRequestMethod("POST");
                    cn.setRequestProperty("Content-Type","application/json");
                    cn.setDoOutput(true);
                    OutputStream outputStream = cn.getOutputStream();
                    outputStream.write(params.getBytes(StandardCharsets.UTF_8));
                    outputStream.close();
                    if(cn.getResponseCode()==200){
                        InputStream inputStream = cn.getInputStream();
                        //流转字符串
                        byte[] bytes=new byte[1024];
                        ByteArrayOutputStream baos=new ByteArrayOutputStream();
                        int len=0;
                        while((len=inputStream.read(bytes))>0){
                            baos.write(bytes,0,len);
                        }
                        System.out.println(baos.toString());
                        JSONObject obj=new JSONObject(baos.toString());
                        if(obj.optInt("resultCode")==200){
                            String token=obj.optString("data");
                            SharedPreferences info = getSharedPreferences("info", MODE_PRIVATE);
                            SharedPreferences.Editor edit = info.edit();
                            edit.putString("token",token);
                            edit.putString("name",name);
                            edit.putString("pwd",pwd);
                            edit.apply();
                            //切换回主线程的另一种方法，登录成功则关闭LoginActivity,返回
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent=new Intent();
                                    intent.putExtra("result",1);
                                    setResult(RESULT_OK,intent);
                                    finish();
                                }
                            });
                        }else {
                            //登录失败，显示错误消息
                            String message = obj.optString("message");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();
    }

    //参数isLog,false 注册，true 登陆
    private void toSwith(boolean isLog) {
        if(isLog){
            tv_title.setText("登陆");
            btn_register.setText("登陆");
            tv_switch.setText("去注册");
        }else{
            tv_title.setText("注册");
            btn_register.setText("注册");
            tv_switch.setText("去登陆");
        }
    }
}