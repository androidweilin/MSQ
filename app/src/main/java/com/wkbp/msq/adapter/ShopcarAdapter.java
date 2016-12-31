package com.wkbp.msq.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wkbp.msq.R;
import com.wkbp.msq.result.bean.CommodityBean;

import java.util.List;

/**
 * Created by shangshuaibo on 2016/11/18 17:57
 */
public class ShopcarAdapter extends BaseAdapter {
    private Context context;
    private List<CommodityBean> shopcarList;
    private OnShopCarItemClickListner onShopCarItemClickListner;

    public ShopcarAdapter(Context context, List<CommodityBean> shopcarList) {
        this.context = context;
        this.shopcarList = shopcarList;
    }

    @Override
    public int getCount() {
        return (shopcarList == null) ? 0 : shopcarList.size();
    }

    @Override
    public Object getItem(int position) {
        return shopcarList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.shopcar_item, null);
            holder = new ViewHolder();
            holder.tv_orderName = (TextView) convertView.findViewById(R.id.tv_orderName);
            holder.tv_orderPrice = (TextView) convertView.findViewById(R.id.tv_orderPrice);
            holder.tv_orderTotal = (TextView) convertView.findViewById(R.id.tv_orderTotal);
            holder.item_checkBox = (CheckBox) convertView.findViewById(R.id.item_checkBox);
            holder.iv_order = (ImageView) convertView.findViewById(R.id.iv_order);
            holder.iv_orderSub = (ImageView) convertView.findViewById(R.id.iv_orderSub);
            holder.iv_orderAdd = (ImageView) convertView.findViewById(R.id.iv_orderAdd);
            holder.iv_orderDelete = (ImageView) convertView.findViewById(R.id.iv_orderDelete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final CommodityBean bean = shopcarList.get(position);

        holder.tv_orderName.setText(bean.getCommodityName());
        holder.tv_orderPrice.setText(String.format("￥%1$s", bean.getCommodityPrice()));
        holder.tv_orderTotal.setText(bean.getCommodityNum());
        ImageLoader.getInstance().displayImage(bean.getCommodityIcon(), holder.iv_order);

        holder.item_checkBox.setChecked(bean.isCheck());
        holder.item_checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.item_checkBox.isChecked()) {
                    bean.setCheck(true);
                } else {
                    bean.setCheck(false);
                }
                onShopCarItemClickListner.onShopCarItemClick(holder.item_checkBox,position);
            }
        });

        holder.iv_orderSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShopCarItemClickListner.onShopCarItemClick(holder.iv_orderSub,position);
            }
        });
        holder.iv_orderAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShopCarItemClickListner.onShopCarItemClick(holder.iv_orderAdd,position);
            }
        });
        holder.iv_orderDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShopCarItemClickListner.onShopCarItemClick(holder.iv_orderDelete,position);
            }
        });
        return convertView;
    }

    class ViewHolder {
        CheckBox item_checkBox;
        ImageView iv_order;//商品图片
        ImageView iv_orderSub;//减
        ImageView iv_orderAdd;//加
        ImageView iv_orderDelete;//删除
        TextView tv_orderName;//商品名称
        TextView tv_orderPrice;//商品单价
        TextView tv_orderTotal;//商品总数
    }

    public interface OnShopCarItemClickListner {
        void onShopCarItemClick(View view, int position);
    }

    public void setOnShopCarItemClickListner(OnShopCarItemClickListner onShopCarItemClickListner) {
        this.onShopCarItemClickListner = onShopCarItemClickListner;
    }
}
