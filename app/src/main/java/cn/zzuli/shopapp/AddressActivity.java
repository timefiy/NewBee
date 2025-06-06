package cn.zzuli.shopapp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.zzuli.shopapp.adapter.AddressAdapter;
import cn.zzuli.shopapp.entity.Address;
import cn.zzuli.shopapp.fragment.MyFragment;

public class AddressActivity extends AppCompatActivity implements View.OnClickListener{
    private Button addAddress;
    private ImageView back;
    private ImageView revise;
    private String strAddress = "http://115.158.64.84:28019/api/v1/address";
    private String token;
    private Address address;
    private List<Address> list = new ArrayList<>();
    private AddressAdapter adapter;
    private ActivityResultLauncher<Intent> addAddressLauncher;
    private ActivityResultLauncher<Intent> editAddressLauncher;
    private Intent it;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences info = getSharedPreferences("info", MODE_PRIVATE);
        token = info.getString("token", "");
        System.out.println(token);
        if(token==null){
            Intent intent = new Intent(AddressActivity.this, RegisterActivity.class);
            startActivity(intent);
        }

        RecyclerView recyclerView = findViewById(R.id.rv_address);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new AddressAdapter(list);
        recyclerView.setAdapter(adapter);
        toAddress();

        adapter.setOnReviseClickListener((addressId, userId) -> {
            it = new Intent(this, ReviseAddressActivity.class);
            it.putExtra("address_id", addressId);
            it.putExtra("userId", userId);
            editAddressLauncher.launch(it);
        });

        addAddress = findViewById(R.id.btn_new_address);
        back = findViewById(R.id.iv_back);

        addAddress.setOnClickListener(this);
        back.setOnClickListener(this);

        addAddressLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        list.clear();
                        toAddress();
                        Log.d(TAG, "更新已完成");
                    }
                }
        );
        editAddressLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        list.clear();
                        toAddress(); // 触发数据重载
                        Log.d(TAG, "地址修改完成，列表已刷新");
                    }
                }
        );
    }

    @Override
    public void onClick(View v){
        if(v.getId() == R.id.btn_new_address){
            handleAddAddressClick();
        } else if (v.getId()==R.id.iv_back) {
            handleBackClick();
        }
    }

    private void handleAddAddressClick() {
        Intent intent = new Intent(this, NewAddressActivity.class);
        addAddressLauncher.launch(intent);
    }

    private void handleBackClick() {
        finish();
    }

    private void toAddress(){
        new Thread(){
            @Override
            public void run(){
                try{
                    URL url = new URL(strAddress);
                    HttpURLConnection cn = (HttpURLConnection) url.openConnection();
                    cn.setRequestMethod("GET");
                    cn.setRequestProperty("Content-Type","application/json");
                    cn.setRequestProperty("token", token);
                    if (cn.getResponseCode() == 200) {
                        InputStream inputStream = cn.getInputStream();
                        byte[] bytes=new byte[1024];
                        ByteArrayOutputStream baos=new ByteArrayOutputStream();
                        int len=0;
                        while((len=inputStream.read(bytes))>0){
                            baos.write(bytes,0,len);
                        }
                        JSONObject object = new JSONObject(baos.toString());
                        JSONArray array = object.getJSONArray("data");
                        for(int i = 0;i < array.length();i++){
                            address = new Address();
                            JSONObject obj = array.getJSONObject(i);
                            address.setAddressId(obj.getInt("addressId"));
                            address.setUserId(obj.getInt("userId"));
                            address.setUserName(obj.getString("userName"));
                            address.setUserPhone(obj.getString("userPhone"));
                            address.setProvinceName(obj.getString("provinceName"));
                            address.setCityName(obj.getString("cityName"));
                            address.setRegionName(obj.getString("regionName"));
                            address.setDetailAddress(obj.getString("detailAddress"));
                            address.setDefaultFlag(obj.getString("defaultFlag"));
                            list.add(address);
                        }
                        runOnUiThread(() -> adapter.notifyDataSetChanged());
                    }
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        }.start();
    }
}
