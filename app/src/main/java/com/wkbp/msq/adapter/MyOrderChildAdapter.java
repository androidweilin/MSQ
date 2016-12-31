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
 * Created by shangshuaibo on 2016/11/28 10:45
 */
public class MyOrderChildAdapter extends BaseAdapter {
    private Context context;
    private List<CommodityBean> commodityList;

    public MyOrderChildAdapter(Context context, List<CommodityBean> commodityList) {
        this.context = context;
        this.commodityList = commodityList;
    }

    @Override
    public int getCount() {
        return (commodityList == null) ? 0 : commodityList.size();
    }

    @Override
    public Object getItem(int position) {
        return commodityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.order_item, null);
            holder = new ViewHolder();
            holder.tv_orderName = (TextView) convertView.findViewById(R.id.tv_orderName);
            holder.tv_orderPrice = (TextView) convertView.findViewById(R.id.tv_orderPrice);
            holder.tv_orderNum = (TextView) convertView.findViewById(R.id.tv_orderTotal);
            holder.iv_orderImg = (ImageView) convertView.findViewById(R.id.iv_order);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CommodityBean commodityBean = commodityList.get(position);
        holder.tv_orderName.setText(commodityBean.getCommodityName());
        holder.tv_orderPrice.setText(String.format("￥%1$s", commodityBean.getCommodityPrice()));
        holder.tv_orderNum.setText(String.format("x%1$s", commodityBean.getCommodityBuyNum()));
        ImageLoader.getInstance().displayImage(commodityBean.getCommodityIcon(), holder
                .iv_orderImg);
        return convertView;
    }

    class ViewHolder {
        TextView tv_orderName, tv_orderPrice, tv_orderNum;
        ImageView iv_orderImg;
    }
}
