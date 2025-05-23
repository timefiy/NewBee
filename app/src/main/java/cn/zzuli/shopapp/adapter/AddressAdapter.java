package cn.zzuli.shopapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.zzuli.shopapp.R;
import cn.zzuli.shopapp.entity.Address;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.VH>{
    List<Address> list;

    public AddressAdapter(List<Address> list) {
        this.list = list;
    }

    public interface OnReviseClickListener {
        void onReviseClick(int addressId, int userId);
    }

    private OnReviseClickListener listener;

    public void setOnReviseClickListener(OnReviseClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address,parent,false);
        VH vh = new VH(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull AddressAdapter.VH holder, int position){
        Address address = list.get(position);
        holder.namePhone.setText(address.getUserName()+" "+address.getUserPhone());
        holder.addressComment.setText(address.getProvinceName()+" "+address.getCityName()+" "+address.getRegionName());
        if(Integer.parseInt(address.getDefaultFlag())== 1){
            holder.defaultFlag.setVisibility(View.VISIBLE);
        } else {
            holder.defaultFlag.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount(){
        return list.size();
    }

    public class VH extends RecyclerView.ViewHolder{
        TextView namePhone;
        TextView addressComment;
        TextView defaultFlag;
        ImageView revise;
        public VH(@NonNull View itemView){
            super(itemView);
            namePhone = itemView.findViewById(R.id.tv_name_phone);
            addressComment = itemView.findViewById(R.id.tv_address_comment);
            defaultFlag = itemView.findViewById(R.id.defaultFlag);
            revise = itemView.findViewById(R.id.iv_revise);
            revise.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    Address address = list.get(position);
                    listener.onReviseClick(address.getAddressId(), address.getUserId());
                }
            });
        }
    }
}
