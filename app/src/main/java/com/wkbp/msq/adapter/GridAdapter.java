package com.wkbp.msq.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wkbp.msq.R;
import com.wkbp.msq.result.bean.CommodityBean;

import java.util.List;

/**
 * Created by shangshuaibo on 2016/11/18 13:12
 */
public class GridAdapter extends BaseAdapter {
    private Context context;
    private List<CommodityBean> homeData;

    public GridAdapter(Context context, List<CommodityBean> homeData) {
        super();
        this.context = context;
        this.homeData = homeData;
    }

    @Override
    public int getCount() {
        return (homeData == null) ? 0 : homeData.size();
    }

    @Override
    public Object getItem(int position) {
        return homeData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.gridview_item, null);
            holder = new ViewHolder();
            holder.ll_grid_item = (LinearLayout) convertView.findViewById(R.id.ll_grid_item);
            holder.iv_grid_item = (ImageView) convertView.findViewById(R.id.iv_grid_item);
            holder.tv_grid_title = (TextView) convertView.findViewById(R.id.tv_grid_title);
            holder.tv_grid_price = (TextView) convertView.findViewById(R.id.tv_grid_price);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CommodityBean bean = homeData.get(position);
        holder.tv_grid_title.setText(bean.getCommodityName());
        holder.tv_grid_price.setText(context.getResources().getString(R.string.￥, bean
                .getCommodityMarketPrice()));
        ImageLoader.getInstance().displayImage(bean.getCommodityIcon(), holder.iv_grid_item);
        return convertView;
    }

    class ViewHolder {
        LinearLayout ll_grid_item;
        ImageView iv_grid_item;
        TextView tv_grid_title;
        TextView tv_grid_price;
    }


}
