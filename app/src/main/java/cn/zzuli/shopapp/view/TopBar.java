package cn.zzuli.shopapp.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import cn.zzuli.shopapp.R;

public class TopBar extends LinearLayout {
    private ImageView ivLeft;
    private TextView tvTitle;
    private ImageView ivRight;
    private Context context;

    public TopBar(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public TopBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.top, this, true);
        ivLeft = findViewById(R.id.iv_left);
        tvTitle = findViewById(R.id.tv_title);
        ivRight = findViewById(R.id.iv_right);

        // 设置左侧按钮点击事件
        ivLeft.setOnClickListener(v -> {
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }
        });
    }

    // 设置标题
    public void setTitle(String title, int size) {
        tvTitle.setText(title);
        tvTitle.setTextSize(size);
    }

    // 设置右侧按钮点击事件
    public void setRightClickListener(OnClickListener listener) {
        ivRight.setOnClickListener(listener);
    }

    // 设置右侧按钮是否可见
    public void setRightVisible(boolean visible) {
        ivRight.setVisibility(visible ? VISIBLE : GONE);
    }
} 