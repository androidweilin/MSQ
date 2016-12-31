package com.wkbp.msq.popordialog;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.wkbp.msq.R;
import com.wkbp.msq.adapter.PopWindowAdapter;
import com.wkbp.msq.util.Util;

/**
 * Created by shangshuaibo on 2016/11/18 13:42
 */
public class MyPopup {
    public static PopupWindow popupWindow;

    public static PopupWindow instancePop(Context mContext) {
        if (popupWindow == null) {
            popupWindow = new PopupWindow(mContext);
        }
        return popupWindow;
    }

    public static void getPopupWindow(Context mContext, final String[] data, LinearLayout ll,
                                      final Handler handler, final int type, final ImageView
                                              icon_price, final ImageView icon_brand) {
        if (popupWindow == null) {
            popupWindow = new PopupWindow(mContext);
        }
        View view = View.inflate(mContext, R.layout.popupwindow, null);
        popupWindow.setContentView(view);
        int width = ll.getWidth();
        popupWindow.setWidth(width);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //popupWindow.setBackgroundDrawable(null);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(ll, 0, Util.dip2px(mContext, 5));
        popupWindow.update();

        ListView popListView = (ListView) view.findViewById(R.id.pop_list);
        popListView.setAdapter(new PopWindowAdapter(mContext, data));

        popListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Message msg = handler.obtainMessage();
                msg.arg1 = position;
                msg.what = type;
                handler.sendMessage(msg);
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                icon_price.setImageResource(R.drawable.icon_down);
                icon_brand.setImageResource(R.drawable.icon_down);
            }
        });
    }
}
