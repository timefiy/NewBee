package cn.zzuli.shopapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import cn.zzuli.shopapp.R;
import cn.zzuli.shopapp.adapter.Good;

public class GoodAdapter extends RecyclerView.Adapter<GoodAdapter.GoodsViewHolder> {

    private List<Good> goodsList;
    private Context context;

    public GoodAdapter(List<Good> goodsList) {
        this.goodsList = goodsList;
    }

    @NonNull
    @Override
    public GoodsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_good, parent, false);
        return new GoodsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoodsViewHolder holder, int position) {
        Good goods = goodsList.get(position);

        // 设置文本信息
        holder.tvName.setText(goods.getGoodsName());
        holder.tvIntro.setText(goods.getGoodsIntro());
        holder.tvPrice.setText("¥" + goods.getSellingPrice());

        // 加载图片
        new ImageDownloader(holder.ivCover).execute(goods.getGoodsCoverImg());

    }

    @Override
    public int getItemCount() {
        return goodsList.size();
    }

    static class GoodsViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvName;
        TextView tvIntro;
        TextView tvPrice;

        public GoodsViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvName = itemView.findViewById(R.id.tv_name);
            tvIntro = itemView.findViewById(R.id.tv_intro);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }

    /**
     * 异步下载图片任务
     */
    private class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public ImageDownloader(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urlStr = urls[0];
            try {
                // 如果是相对路径，补全域名
                if (urlStr.startsWith("/")) {
                    urlStr = "http://115.158.64.84:28019/" + urlStr;
                }

                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();

                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                // 设置默认占位图（可选）
                imageView.setImageResource(R.drawable.ic_launcher_foreground);
            }
        }
    }
}
