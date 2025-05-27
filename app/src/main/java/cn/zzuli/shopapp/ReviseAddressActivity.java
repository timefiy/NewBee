package cn.zzuli.shopapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import cn.zzuli.citypicker.bean.CityInfo;
import cn.zzuli.citypicker.utils.CityListLoader;
import cn.zzuli.citypicker.widget.CityPickerBottomDialog;
import cn.zzuli.shopapp.entity.Address;

public class ReviseAddressActivity extends AppCompatActivity {
    private EditText userName;
    private CheckBox defaultFlag;
    private EditText userPhone;
    private EditText detailAddress;
    private Button revise;
    private Button delete;
    private ImageView back;
    private EditText et_city;
    private Address address = new Address();
    private CityPickerBottomDialog dialog;
    private String strAddress = "http://115.158.64.84:28019/api/v1/address/";
    private int addressId;
    private int userId;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences info = getSharedPreferences("info", MODE_PRIVATE);
        token = info.getString("token", "");
        System.out.println(token);
        if(token==null){
            Intent intent = new Intent(ReviseAddressActivity.this, RegisterActivity.class);
            startActivity(intent);
        }

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_revise_address);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        if(intent!=null){
            addressId = intent.getIntExtra("address_id", -1);
            userId = intent.getIntExtra("userId", -1);
        }
        initDisplay();

        et_city=findViewById(R.id.tv_address_choose);
        revise=findViewById(R.id.btn_revise_address);
        delete=findViewById(R.id.btn_delete_address);
        back = findViewById(R.id.iv_back);
        userName = findViewById(R.id.et_name_address);
        defaultFlag = findViewById(R.id.ck_set);
        userPhone = findViewById(R.id.et_phone);
        detailAddress = findViewById(R.id.et_detail_address);
        
        initTextChanged();
        initCityPicker();

        et_city.setKeyListener(null);
        et_city.setOnClickListener(v-> {
            dialog.show();
        });
        revise.setOnClickListener(v->{
            initReviseAddress();
            toRevise();
        });
        delete.setOnClickListener(v->{
            toDelete();
            finish();
        });
        back.setOnClickListener(v->{
            finish();
        });
    }

    private void initTextChanged() {
        bindValidation(userName, "^[\\u4e00-\\u9fa5a-zA-Z0-9]{1,}$", "请输入姓名");
        bindValidation(userPhone, "^1[3-9]\\d{9}$", "手机号格式错误");
        bindValidation(detailAddress, "^[\\u4e00-\\u9fa5a-zA-Z0-9]{1,}$", "请输入详细地址");
        et_city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override public void afterTextChanged(Editable s) {
                String input = s.toString();
                et_city.setError(input.isEmpty() ? "请选择地区" : null);
            }
        });
    }

    private void bindValidation(EditText editText, String regex, String errorMsg) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override public void afterTextChanged(Editable s) {
                boolean isValid = s.toString().matches(regex);
                editText.setError(isValid ? null : errorMsg);
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    private void initDisplay(){
        new Thread(){
            @Override
            public void run(){
                try {
                    URL url = new URL(strAddress+addressId);
                    HttpURLConnection cn = (HttpURLConnection) url.openConnection();
                    cn.setRequestMethod("GET");
                    cn.setRequestProperty("token", token);
                    cn.setRequestProperty("Content-Type", "application/json");
                    if(cn.getResponseCode()==200){
                        InputStream inputStream = cn.getInputStream();
                        byte[] bytes=new byte[1024];
                        ByteArrayOutputStream baos=new ByteArrayOutputStream();
                        int len=0;
                        while((len=inputStream.read(bytes))>0){
                            baos.write(bytes,0,len);
                        }
                        JSONObject object = new JSONObject(baos.toString());
                        JSONObject obj = object.getJSONObject("data");
                        address.setUserName(obj.getString("userName"));
                        address.setUserPhone(obj.getString("userPhone"));
                        address.setProvinceName(obj.getString("provinceName"));
                        address.setCityName(obj.getString("cityName"));
                        address.setRegionName(obj.getString("regionName"));
                        address.setDetailAddress(obj.getString("detailAddress"));
                        address.setDefaultFlag(obj.getString("defaultFlag"));
                        runOnUiThread(() -> {
                                userName.setText(address.getUserName());
                                userPhone.setText(address.getUserPhone());
                                et_city.setText( address.getProvinceName()+
                                        " " + address.getCityName() +
                                        " " + address.getRegionName());
                                detailAddress.setText(address.getDetailAddress());
                                defaultFlag.setChecked(Integer.parseInt(address.getDefaultFlag()) != 0);
                        });
                    }
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        }.start();
    }
    public void initReviseAddress(){
        address.setUserName(userName.getText().toString());
        address.setUserPhone(userPhone.getText().toString());
        address.setDetailAddress(detailAddress.getText().toString());
        String[] addressNames = et_city.getText().toString().split("\\s+");
        if (addressNames.length >= 3) {
            address.setProvinceName(addressNames[0]);
            address.setCityName(addressNames[1]);
            address.setRegionName(addressNames[2]);
        }
        address.setDefaultFlag(defaultFlag.isChecked()?"1":"0");
    }

    private void initCityPicker() {
        CityListLoader.getInstance().init(this);

        dialog = new CityPickerBottomDialog(this);
        dialog.setTitle("地区");
        dialog.setDimAmount(0.5F);
        dialog.setOnSubmitClickListener(new CityPickerBottomDialog.OnSubmitClickListener() {
            @Override
            public void onSubmitClick(CityInfo province, CityInfo.CityListBeanX city, CityInfo.CityListBeanX.CityListBean district) {
                String address = String.format("%s %s %s", province.name, city.name, district.name);
                et_city.setText(address);
                dialog.dismiss();
            }
        });
    }

    private void toRevise(){
        new Thread(){
            @Override
            public void run() {
                try {
                    JSONObject object = new JSONObject();
                    object.put("cityName", address.getCityName());
                    object.put("defaultFlag", address.getDefaultFlag());
                    object.put("detailAddress", address.getDetailAddress());
                    object.put("provinceName", address.getProvinceName());
                    object.put("regionName", address.getRegionName());
                    object.put("userName", address.getUserName());
                    object.put("userPhone", address.getUserPhone());
                    object.put("userId", userId);
                    object.put("addressId", addressId);
                    String params = object.toString();
                    URL url = new URL(strAddress);
                    System.out.println(params);
                    HttpURLConnection cn = (HttpURLConnection) url.openConnection();
                    cn.setRequestMethod("PUT");
                    cn.setRequestProperty("Content-Type","application/json");
                    cn.setRequestProperty("token", token);
                    cn.setDoOutput(true);
                    OutputStream outputStream = cn.getOutputStream();
                    outputStream.write(params.getBytes(StandardCharsets.UTF_8));
                    outputStream.close();
                    int responseCode = cn.getResponseCode();
                    runOnUiThread(() -> {
                        if (responseCode == 200) {
                            saveAddress();
                        } else {
                            Toast.makeText(ReviseAddressActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();
    }

    private void toDelete(){
        new Thread(){
            @Override
            public void run(){
                try{
                    URL url = new URL(strAddress+addressId);
                    HttpURLConnection cn = (HttpURLConnection) url.openConnection();
                    cn.setRequestMethod("DELETE");
                    cn.setRequestProperty("Content-Type","application/json");
                    cn.setRequestProperty("token", token);
                    if(cn.getResponseCode()==200){
                        saveAddress();
                    }
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        }.start();
    }

    private void saveAddress() {
        setResult(RESULT_OK);
        finish();
    }
}
