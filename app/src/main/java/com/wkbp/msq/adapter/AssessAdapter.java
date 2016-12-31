package com.wkbp.msq.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wkbp.msq.R;
import com.wkbp.msq.result.bean.CommodityBean;

import java.util.List;

/**
 * Created by shangshuaibo on 2016/11/18 17:57
 */
public class AssessAdapter extends BaseAdapter {
    private Context context;
    private List<CommodityBean> orderList;

    public AssessAdapter(Context context, List<CommodityBean> orderList) {
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
            convertView = View.inflate(context, R.layout.assess_item, null);
            holder = new ViewHolder();
            holder.assess_img = (ImageView) convertView.findViewById(R.id.assess_img);
            holder.ratingBar_description = (RatingBar) convertView.findViewById(R.id
                    .ratingBar_description);
            holder.assess_content = (EditText) convertView.findViewById(R.id.assess_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final CommodityBean bean = orderList.get(position);
        ImageLoader.getInstance().displayImage(bean.getCommodityIcon(), holder.assess_img);
        return convertView;
    }

    class ViewHolder {
        private ImageView assess_img;
        private RatingBar ratingBar_description;
        private EditText assess_content;
    }

}
