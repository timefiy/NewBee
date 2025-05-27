package cn.zzuli.shopapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class CategoryActivity extends AppCompatActivity {

    // 分类数据
    private final String[] categories = {
            "家电 数码 手机",
            "女装 男装 穿搭",
            "家居 家饰 家纺",
            "运动 户外 乐器",
            "游戏 动漫 影视",
            "美妆 清洁 宠物",
            "工具 装修 建材"
    };

    // 商品数据 (二维数组)
    private final String[][] products = {
            {"卷发器", "空气净化器", "厨房电器", "扫地机器人", "取暖器", "豆浆机", "加湿器", "蓝牙音箱", "生活电器", "吸尘器", "暖风机", "烤箱"},
            {"女装1", "女装2", "男装1", "男装2", "穿搭1", "穿搭2"},
            {"家居1", "家居2", "家饰1", "家饰2", "家纺1", "家纺2"},
            {"运动1", "运动2", "户外1", "户外2", "乐器1", "乐器2"},
            {"游戏1", "游戏2", "动漫1", "动漫2", "影视1", "影视2"},
            {"美妆1", "美妆2", "清洁1", "清洁2", "宠物1", "宠物2"},
            {"工具1", "工具2", "装修1", "装修2", "建材1", "建材2"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_category);

        // 初始化视图
        ListView categoryList = findViewById(R.id.category_list);
        if (categoryList == null) {
            Toast.makeText(this, "ListView未找到！", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("DATA_CHECK", "分类数据: " + Arrays.toString(categories));
        GridView productGrid = findViewById(R.id.product_grid);

        // 设置分类列表适配器
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, categories);// categories数组
        System.out.println("11111111111111111111111111");
        categoryList.setAdapter(categoryAdapter);//Android系统内置的列表项布局，特点如下：单行文本显示（对应图片中左侧分类的黑色文字）。支持选中状态高亮（图片中选中项可能有背景色变化，但图中未明确显示）。布局源码实际是一个TextView，因此只能显示简单文本。
                //将分类名称数组绑定到左侧列表视图，使用系统默认的简单文本布局
        Log.d("ADAPTER_CHECK", "适配器Item数: " + categoryAdapter.getCount());
        // 默认选中第一项
        categoryList.setItemChecked(0, true);
        updateProductGrid(0);
        categoryList.post(() -> {
            Toast.makeText(this, "ListView高度: " + categoryList.getHeight(), Toast.LENGTH_LONG).show();
        });


        // 分类点击事件
        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                categoryList.setItemChecked(position, true);
                updateProductGrid(position);
            }
        });
    }

    private void updateProductGrid(int categoryIndex) {
        GridView productGrid = findViewById(R.id.product_grid);
        ProductAdapter adapter = new ProductAdapter(this, products[categoryIndex]);
        productGrid.setAdapter(adapter);

        productGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String product = products[categoryIndex][position];
                Toast.makeText(CategoryActivity.this, "已选择: " + product, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 自定义商品适配器
    class ProductAdapter extends BaseAdapter {
        private Context context;
        private String[] products;

        public ProductAdapter(Context context, String[] products) {
            this.context = context;
            this.products = products;
        }

        @Override
        public int getCount() {
            return products.length;
        }

        @Override
        public Object getItem(int position) {
            return products[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context)
                        .inflate(R.layout.item_product, parent, false);
                holder = new ViewHolder();
                holder.icon = convertView.findViewById(R.id.product_icon);
                holder.name = convertView.findViewById(R.id.product_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.name.setText(products[position]);
            // 这里可以设置图标颜色为蓝绿色
            // holder.icon.setColorFilter(Color.parseColor("#00CED1"));

            return convertView;
        }

        class ViewHolder {
            ImageView icon;
            TextView name;
        }
    }
}