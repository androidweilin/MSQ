package com.wkbp.msq.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wkbp.msq.R;
import com.wkbp.msq.result.bean.GsonShopDetailInfoBack;

import java.util.List;

public class DetailTalkAdapter extends BaseAdapter {
    private Context mContext;
    private List<GsonShopDetailInfoBack.Talk> mDatas;
    private LayoutInflater mInflater;

    public DetailTalkAdapter(Context context, List<GsonShopDetailInfoBack.Talk> data) {
        mContext = context;
        mDatas = data;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mDatas != null ? mDatas.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.shop_talk_item,
                    null);
            holder.icon = (ImageView) convertView.findViewById(R.id.talk_head);
            holder.nickName = (TextView) convertView.findViewById(R.id.talk_nickName);
            holder.content = (TextView) convertView.findViewById(R.id.talk_content);
            holder.date = (TextView) convertView.findViewById(R.id.talk_date);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String icon = mDatas.get(position).getTalkIcon();
        ImageLoader.getInstance().displayImage(icon, holder.icon);

        holder.nickName.setText(mDatas.get(position).getTalkNickName());
        holder.date.setText(mDatas.get(position).getTalkDate());
        holder.content.setText(mDatas.get(position).getTalkContent());

        return convertView;
    }

    class ViewHolder {
        ImageView icon;
        TextView content;
        TextView date;
        TextView nickName;
    }
}
