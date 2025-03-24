package cn.zzuli.citypicker.widget;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import cn.zzuli.citypicker.R;
import cn.zzuli.citypicker.adapter.CityAdapter;
import cn.zzuli.citypicker.adapter.DistrictAdapter;
import cn.zzuli.citypicker.adapter.OnItemClickListener;
import cn.zzuli.citypicker.adapter.ProvinceAdapter;
import cn.zzuli.citypicker.bean.CityInfo;
import cn.zzuli.citypicker.utils.CityListLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 类的描述
 */
public class CityPickerBottomDialog extends BaseBottomDialog {

    private TextView                                  tvTitle;
    private RecyclerView                              rvProvince;
    private RecyclerView                              rvCity;
    private RecyclerView                              rvCounty;
    private ProvinceAdapter                           provinceAdapter;
    private CityAdapter                               cityAdapter;
    private DistrictAdapter                           districtAdapter;
    private List<CityInfo>                            provinces = new ArrayList<>();//省
    private List<CityInfo.CityListBeanX>              citys = new ArrayList<>();//市
    private List<CityInfo.CityListBeanX.CityListBean> districts = new ArrayList<>();//区
    private OnSubmitClickListener submitClickListener;//确定点击事件
    private CharSequence title;//标题
    private int posProvince = 0, posCity = 0, posDistrict = 0;//选中的省市区的position

    public CityPickerBottomDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_city_picker_bottom;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {//取消
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {//确定
            @Override
            public void onClick(View v) {
                if (submitClickListener != null) {
                    submitClickListener.onSubmitClick(provinces.get(posProvince), citys.get(posCity), districts.get(posDistrict));
                }
            }
        });
        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(title);
        rvProvince = findViewById(R.id.rv_province);
        rvCity = findViewById(R.id.rv_city);
        rvCounty = findViewById(R.id.rv_county);

        List<CityInfo> provinceList = CityListLoader.getInstance().getProvinces();
        if (provinceList != null) {
            provinces.clear();
            provinces.addAll(provinceList);
        }

        //设置Adapter
        provinceAdapter = new ProvinceAdapter(provinces, posProvince);
        cityAdapter = new CityAdapter(getCitys(posProvince), posCity);
        districtAdapter = new DistrictAdapter(getDistricts(posCity), posDistrict);

        //填充数据
        rvProvince.setAdapter(provinceAdapter);
        rvCity.setAdapter(cityAdapter);
        rvCounty.setAdapter(districtAdapter);

        //设置点击事件
        provinceAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                if (position != posProvince) {
                    posProvince = position;
                    posCity = posDistrict =  0;
                    citys = getCitys(posProvince);
                    districts = getDistricts(posCity);
                    provinceAdapter.notifySelectedItemChanged(posProvince);
                    cityAdapter.notifySelectedItemChanged(posCity);
                    districtAdapter.notifySelectedItemChanged(posDistrict);
                }
            }
        });
        cityAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                if (position != posCity) {
                    posCity = position;
                    posDistrict =  0;
                    districts = getDistricts(posCity);
                    cityAdapter.notifySelectedItemChanged(posCity);
                    districtAdapter.notifySelectedItemChanged(posDistrict);
                }
            }
        });
        districtAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                if (position != posDistrict) {
                    posDistrict = position;
                    districtAdapter.notifySelectedItemChanged(posDistrict);
                }
            }
        });

        //设置中间停靠
        new LinearSnapHelper().attachToRecyclerView(rvProvince);
        new LinearSnapHelper().attachToRecyclerView(rvCity);
        new LinearSnapHelper().attachToRecyclerView(rvCounty);
    }

    public CityPickerBottomDialog setOnSubmitClickListener(OnSubmitClickListener listener) {
        this.submitClickListener = listener;
        return this;
    }
    public interface OnSubmitClickListener {
        //省市区
        void onSubmitClick(CityInfo province, CityInfo.CityListBeanX city, CityInfo.CityListBeanX.CityListBean district);
    }

    //设置标题
    @Override
    public void setTitle(CharSequence title) {
        this.title = title;
    }

    /**
     * 获取市
     * @param position 第一个RecyclerView的position
     */
    private List<CityInfo.CityListBeanX> getCitys(int position) {
        if (provinces.size() > position) {
            List<CityInfo.CityListBeanX> cityList = provinces.get(position).cityList;
            if (cityList != null) {
                citys.clear();
                citys.addAll(cityList);
            }
        }
        return citys;
    }

    /**
     * 获取区
     * @param position 第二个RecyclerView的position
     */
    private List<CityInfo.CityListBeanX.CityListBean> getDistricts(int position) {
        if (citys.size() > position) {
            List<CityInfo.CityListBeanX.CityListBean> cityList = citys.get(position).cityList;
            if (cityList != null) {
                districts.clear();
                districts.addAll(cityList);
            }
        }
        return districts;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        provinceAdapter = null;
//        cityAdapter = null;
//        districtAdapter = null;
//        provinces.clear();
//        citys.clear();
//        districts.clear();
    }
}
