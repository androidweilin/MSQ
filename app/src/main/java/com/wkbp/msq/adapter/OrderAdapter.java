package com.wkbp.msq.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wkbp.msq.R;
import com.wkbp.msq.result.bean.CommodityBean;

import java.util.List;

/**
 * Created by shangshuaibo on 2016/11/18 17:57
 */
public class OrderAdapter extends BaseAdapter {
    private Context context;
    private List<CommodityBean> orderList;

    public OrderAdapter(Context context, List<CommodityBean> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @Override
    public int getCount() {
        return (orderList == null) ? 0 : orderList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.order_item, null);
            holder = new ViewHolder();
            holder.tv_orderPrice = (TextView) convertView.findViewById(R.id.tv_orderPrice);
            holder.tv_orderTotal = (TextView) convertView.findViewById(R.id.tv_orderTotal);
            holder.tv_orderName = (TextView) convertView.findViewById(R.id.tv_orderName);
            holder.iv_order = (ImageView) convertView.findViewById(R.id.iv_order);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final CommodityBean bean = orderList.get(position);

        holder.tv_orderName.setText(bean.getCommodityName());
        holder.tv_orderPrice.setText(String.format("￥%1$s", bean.getCommodityPrice()));
        holder.tv_orderTotal.setText(String.format("x%1$s",bean.getCommodityNum()));
        ImageLoader.getInstance().displayImage(bean.getCommodityIcon(), holder.iv_order);

        return convertView;
    }

    class ViewHolder {
        ImageView iv_order;//图片
        TextView tv_orderPrice;//商品单价
        TextView tv_orderTotal;//商品总数
        TextView tv_orderName;//商品总数
    }

}
