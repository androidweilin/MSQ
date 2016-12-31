package com.wkbp.msq.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wkbp.msq.R;
import com.wkbp.msq.customView.ListViewForScrollView;
import com.wkbp.msq.result.bean.MyOrderBean;

import java.util.List;

/**
 * Created by shangshuaibo on 2016/11/28 10:45
 */
public class MyOrderAdapter extends BaseAdapter {
    private Context context;
    private List<MyOrderBean> myOrderList;
    private OnOrderClickListener onOrderClickListener;

    public interface OnOrderClickListener {
        void onOrderClick(View view, int position);
    }

    public void setOnOrderClickListener(OnOrderClickListener onOrderClickListener) {
        this.onOrderClickListener = onOrderClickListener;
    }

    public MyOrderAdapter(Context context, List<MyOrderBean> myOrderList) {
        this.context = context;
        this.myOrderList = myOrderList;
    }

    @Override
    public int getCount() {
        return (myOrderList == null) ? 0 : myOrderList.size();
    }

    @Override
    public Object getItem(int position) {
        return myOrderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.order_list_item, null);
            holder = new ViewHolder();
            holder.tv_orderNumber = (TextView) convertView.findViewById(R.id.tv_orderNumber);
            holder.tv_orderStatus = (TextView) convertView.findViewById(R.id.tv_orderStatus);
            holder.tv_order_total = (TextView) convertView.findViewById(R.id.tv_order_total);
            holder.btn_delete = (TextView) convertView.findViewById(R.id.btn_delete);
            holder.btn_state = (TextView) convertView.findViewById(R.id.btn_state);
            holder.lv_order = (ListViewForScrollView) convertView.findViewById(R.id.lv_order);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MyOrderBean myOrderBean = myOrderList.get(position);
        holder.tv_orderNumber.setText(myOrderBean.getOrderID());
        holder.tv_order_total.setText(String.format("订单金额:￥%1$s", myOrderBean.getTotalMoney()));
        String orderState = myOrderBean.getOrderState();
        switch (orderState) {
            case "0":

                break;
            case "1":

                break;
            case "2":// 待收货
                holder.btn_delete.setVisibility(View.GONE);
                holder.btn_state.setText("确认收货");
                holder.tv_orderStatus.setText("待收货");
                break;
            case "3":// 已完成
                holder.btn_delete.setVisibility(View.VISIBLE);
                holder.btn_state.setVisibility(View.GONE);
                holder.tv_orderStatus.setText("已完成");
                break;
            case "4":// 待发货
                holder.btn_delete.setVisibility(View.GONE);
                holder.btn_state.setText("确认收货");
                holder.tv_orderStatus.setText("待发货");
                break;
            case "5":// 未付款 可以取消,待付款
                holder.btn_delete.setVisibility(View.VISIBLE);
                holder.btn_state.setText("立即付款");
                holder.tv_orderStatus.setText("待付款");
                break;
            case "6":// 待评价
                holder.btn_delete.setVisibility(View.GONE);
                holder.btn_state.setText("前去评价");
                break;
        }
        holder.lv_order.setAdapter(new MyOrderChildAdapter(context, myOrderBean.getOrderList()));
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderClickListener.onOrderClick(holder.btn_delete, position);
            }
        });
        holder.btn_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderClickListener.onOrderClick(holder.btn_state, position);
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView tv_orderNumber, tv_orderStatus, tv_order_total, btn_delete, btn_state;
        ListViewForScrollView lv_order;
    }
}
