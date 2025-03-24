package cn.zzuli.citypicker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.zzuli.citypicker.R;
import cn.zzuli.citypicker.bean.CityInfo;

import java.util.List;

/**
 * Description: 区的Adapter
 */
public class DistrictAdapter extends RecyclerView.Adapter<DistrictAdapter.MyViewHolder> {

    private List<CityInfo.CityListBeanX.CityListBean> districts;
    private OnItemClickListener          listener;
    private int selectedPosition = 0;

    public DistrictAdapter(List<CityInfo.CityListBeanX.CityListBean> districts, int selectedPosition) {
        this.districts = districts;
        this.selectedPosition = selectedPosition;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_4_city_pick, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        myViewHolder.itemView.setTag(position);
        myViewHolder.itemView.setSelected(position == selectedPosition);
        myViewHolder.tvAddress.setText(districts.get(position).name);
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onItemClick(v, (int) v.getTag());
            }
        });
    }

    @Override
    public int getItemCount() {
        return districts == null ? 0 : districts.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void notifySelectedItemChanged(int selectedItem) {
        this.selectedPosition = selectedItem;
        notifyDataSetChanged();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvAddress;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAddress = itemView.findViewById(R.id.tv_address_for_city_pick);
        }
    }
}
