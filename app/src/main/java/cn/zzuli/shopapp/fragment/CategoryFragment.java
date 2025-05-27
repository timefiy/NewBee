package cn.zzuli.shopapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import cn.zzuli.shopapp.R;
import cn.zzuli.shopapp.SearchActivity;
import cn.zzuli.shopapp.WebViewActivity;


public class CategoryFragment extends Fragment {

    // 分类数据
    private final String[] categories = {
            "家电 数码 手机",
            "女装 男装 穿搭",
            "家具 家饰 家纺",
            "运动 户外 乐器",
            "游戏 动漫 影视",
            "美妆 清洁 宠物",
            "工具 装修 建材",
            "鞋靴 箱包 配件"
    };

    // 商品数据
    private final String[][] products = {
            {"卷发器", "空气净化器", "扫地机器人", "吸尘器", "豆浆机", "暖风机", "加湿器", "蓝牙音箱", "烤箱", "wer",
                    "沙发",
                    "游戏主机",
                    "数码精选",
                    "平板电脑", "苹果Apple", "电脑主机", "数码相机", "电玩动漫", "单反相机", "无人机",
                    "二手电脑",
                    "二手手机",
                    "键盘鼠标",
                    "荣耀手机","华为手机","华为P30","iPhone11","苹果iPhone","华为Mate20","小米手机","OPPO","一加","小米MIX","Reno","vivo","手机以旧换新"},
            {"沙发"},
            {"跑鞋"},
            {"LOL"},
            {"口红","气垫","美白","隔离霜","粉底","腮红","睫毛膏"},
            {"转换器"},
            {"休闲鞋"}

    };


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        ListView categoryList = view.findViewById(R.id.category_list);
        GridView productGrid = view.findViewById(R.id.product_grid);

        // 设置分类适配器（自定义样式）
        CategoryAdapter categoryAdapter = new CategoryAdapter(requireContext(), categories);
        categoryList.setAdapter(categoryAdapter);
        categoryList.setChoiceMode(ListView.CHOICE_MODE_SINGLE); // 单选模式

        // 默认选中第一项
        categoryList.setItemChecked(0, true);
        updateProductGrid(productGrid, 0);

        // 分类点击事件
        categoryList.setOnItemClickListener((parent, view1, position, id) -> {
            // 更新选中状态
            for (int i = 0; i < parent.getChildCount(); i++) {
                View item = parent.getChildAt(i);
                if (item != null) {
                    TextView textView = item.findViewById(android.R.id.text1);
                    if (i == position) {
                        textView.setTextColor(Color.parseColor("#00BCD4"));
                        item.setBackgroundColor(Color.parseColor("#808080")); // 灰色背景
                    } else {
                        textView.setTextColor(Color.BLACK);
                        item.setBackgroundColor(Color.parseColor("#f5f5f5")); // 默认浅灰背景
                    }
                }
            }
            updateProductGrid(productGrid, position);
        });

        return view;
    }

    /**
     * 更新商品网格数据
     */
    private void updateProductGrid(GridView gridView, int categoryIndex) {
        ProductAdapter adapter = new ProductAdapter(requireContext(), products[categoryIndex], this);
        gridView.setAdapter(adapter);
    }

    // ==================== 适配器类 ====================

    /**
     * 左侧分类适配器（自定义文字和背景颜色）
     */
    private static class CategoryAdapter extends BaseAdapter {
        private final Context context;
        private final String[] categories;
        private final LayoutInflater inflater;

        public CategoryAdapter(Context context, String[] categories) {
            this.context = context;
            this.categories = categories;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return categories.length;
        }

        @Override
        public Object getItem(int position) {
            return categories[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(android.R.layout.simple_list_item_activated_1, parent, false);
            }
            TextView textView = convertView.findViewById(android.R.id.text1);
            textView.setText(categories[position]);
            textView.setTextColor(Color.BLACK); // 默认黑色
            convertView.setBackgroundColor(Color.parseColor("#f5f5f5")); // 默认浅灰背景
            return convertView;
        }
    }

    /**
     * 右侧商品适配器（带点击事件）
     */
    private static class ProductAdapter extends BaseAdapter {
        private final Context context;
        private final String[] products;
        private final Fragment fragment;
        private final LayoutInflater inflater;

        public ProductAdapter(Context context, String[] products, Fragment fragment) {
            this.context = context;
            this.products = products;
            this.fragment = fragment;
            this.inflater = LayoutInflater.from(context);
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
                convertView = inflater.inflate(R.layout.item_product, parent, false);
                holder = new ViewHolder();
                holder.name = convertView.findViewById(R.id.product_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.name.setText(products[position]);

            // 商品项点击事件
            convertView.setOnClickListener(v -> {
                String productName = products[position];
                Toast.makeText(context, "已选择: " + productName, Toast.LENGTH_SHORT).show();

                // 如果是"口红"商品，则用WebView打开指定URL
                if ("口红".equals(productName)) {
                    // 创建Intent跳转到WebViewActivity
                    Intent intent = new Intent(context, WebViewActivity.class);
                    intent.putExtra("url", "http://115.158.64.84/#/product-list?categoryId=86");
                    context.startActivity(intent);
                } else {
                    // 其他商品保持原有逻辑
                    fragment.startActivity(new Intent(context, SearchActivity.class));
                }
            });

            return convertView;
        }

        static class ViewHolder {
            TextView name;
        }
    }
}