package com.wkbp.msq.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wkbp.msq.R;
import com.wkbp.msq.result.bean.AddressBean;

import java.util.List;

/**
 * Created by shangshuaibo on 2016/11/21.
 */
public class AddressAdapter extends BaseAdapter {
    private Context context;
    private List<AddressBean> addressList;
    private OnClickAddressItemListener onClickAddressItemListener;

    public interface OnClickAddressItemListener {
        void onClickAddressItem(View view, int position);
    }

    public void setOnClickAddressItemListener(OnClickAddressItemListener
                                                      onClickAddressItemListener) {
        this.onClickAddressItemListener = onClickAddressItemListener;
    }

    public AddressAdapter(Context context, List<AddressBean> addressList) {
        this.context = context;
        this.addressList = addressList;
    }

    @Override
    public int getCount() {
        return (addressList == null) ? 0 : addressList.size();
    }

    @Override
    public Object getItem(int position) {
        return addressList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.address_list_item, null);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
            holder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
            holder.address_editor = (ImageView) convertView.findViewById(R.id
                    .address_editor);
            holder.address_checked = (ImageView) convertView.findViewById(R.id.address_checked);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final AddressBean addressBean = addressList.get(position);
        holder.tv_name.setText(addressBean.getName());
        holder.tv_phone.setText(addressBean.getPhone());
        holder.tv_address.setText(addressBean.getAdress());
        if (addressBean.getIsDefault() == 0) {
            holder.address_checked.setImageResource(R.drawable.address_unselecte);
            //holder.address_checked.setEnabled(true);
        } else {
            holder.address_checked.setImageResource(R.drawable.address_selecte);
            //holder.address_checked.setEnabled(false);
        }
        holder.address_checked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAddressItemListener.onClickAddressItem(holder.address_checked, position);
            }
        });
        holder.address_editor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAddressItemListener.onClickAddressItem(holder.address_editor, position);
            }
        });
        return convertView;
    }


    class ViewHolder {
        TextView tv_name;
        TextView tv_phone;
        TextView tv_address;
        ImageView address_editor;
        ImageView address_checked;
    }
}
