package com.wkbp.msq.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wkbp.msq.R;

/**
 * Created by shangshuaibo on 2016/11/18 13:31
 */
public class PopWindowAdapter extends BaseAdapter {
    Context context;
    String[] data;

    public PopWindowAdapter(Context context, String[] data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return (data == null) ? 0 : data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = View.inflate(context, R.layout.popwindow_item,null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.tv_pop);
        tv.setText(data[position]);
        return convertView;
    }
}
