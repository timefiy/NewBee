package cn.zzuli.shopapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.zzuli.shopapp.Adapter.AddressAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recyclerView = findViewById(R.id.rv_address);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new AddressAdapter(list);
        recyclerView.setAdapter(adapter);
        toAddress();

        adapter.setOnReviseClickListener((addressId, userId) -> {
            Intent intent = new Intent(this, ReviseAddressActivity.class);
            intent.putExtra("address_id", addressId);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        addAddress = findViewById(R.id.btn_new_address);
        back = findViewById(R.id.iv_back);

        addAddress.setOnClickListener(this);
        back.setOnClickListener(this);
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
        startActivity(intent);
    }

    private void handleBackClick() {
        Intent intent = new Intent(this, MyFragment.class);
        startActivity(intent);
    }

    private void toAddress(){
        new Thread(){
            @Override
            public void run(){
                try{
                    SharedPreferences info = getSharedPreferences("info", MODE_PRIVATE);
                    info.getString("token", token);
                    URL url = new URL(strAddress);
                    HttpURLConnection cn = (HttpURLConnection) url.openConnection();
                    cn.setRequestMethod("GET");
                    cn.setRequestProperty("Content-Type","application/json");
                    cn.setRequestProperty("token", "2d5cdc70d4eeba206a4358529452eddf");
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
