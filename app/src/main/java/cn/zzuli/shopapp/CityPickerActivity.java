package cn.zzuli.shopapp;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import cn.zzuli.citypicker.bean.CityInfo;
import cn.zzuli.citypicker.utils.CityListLoader;
import cn.zzuli.citypicker.widget.CityPickerBottomDialog;

public class CityPickerActivity extends AppCompatActivity {
    CityPickerBottomDialog dialog;
    private EditText et_city;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_city_picker);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initCityPicker();
        et_city=findViewById(R.id.editTextText);
        et_city.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dialog.show();
                return true;
            }
        });
    }

    private void initCityPicker() {
        //1.初始化数据
        CityListLoader.getInstance().init(this);

        //2.初始化对话框
        dialog = new CityPickerBottomDialog(this);
        dialog.setTitle("选择地址");
        dialog.setDimAmount(0.5F);//default = 0.5
        dialog.setOnSubmitClickListener(new CityPickerBottomDialog.OnSubmitClickListener() {
            @Override
            public void onSubmitClick(CityInfo province, CityInfo.CityListBeanX city, CityInfo.CityListBeanX.CityListBean district) {
                String address = String.format("%s%s%s", province.name, city.name, district.name);
                //Toast.makeText(CityPickerActivity.this, address, Toast.LENGTH_SHORT).show();
                et_city.setText(address);
                dialog.dismiss();
            }
        });
    }
}