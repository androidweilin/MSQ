package com.wkbp.msq.setting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.wkbp.msq.OrderDetailInfoActivity;
import com.wkbp.msq.R;
import com.wkbp.msq.adapter.GridAdapter;
import com.wkbp.msq.base.BaseActivity;
import com.wkbp.msq.result.bean.CommodityBean;
import com.wkbp.msq.result.bean.GsonCommodityRightBack;
import com.wkbp.msq.util.HttpUtils;
import com.wkbp.msq.util.Constant;
import com.wkbp.msq.util.SharedPreferenceUtils;
import com.wkbp.msq.util.ToastUtil;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CollectOrderActivity extends BaseActivity implements AdapterView.OnItemClickListener{
    private GridView gridView;
    private String userName;
    private List<CommodityBean> collectList = new ArrayList<>();
    private GridAdapter gridAdapter;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.REFRESH_REQUEST_SUCCESS:
                    stopProgressDialog();
                    gridAdapter.notifyDataSetChanged();
                    break;
                case Constant.REFRESH_RESULT_FAIL:
                    stopProgressDialog();
                    Bundle bundle = msg.getData();
                    ToastUtil.showMessageDefault(mContext, (String) bundle.get("resultNote"));
                    break;
                case Constant.REFRESH_REQUEST_FAIL:
                    stopProgressDialog();
                    ToastUtil.showMessageDefault(mContext, "网络连接异常");
                    break;
                case Constant.REFRESH_NITEWORK_FAIL:
                    stopProgressDialog();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_collect_order);
        setTitleBar("商品收藏");
        userName = SharedPreferenceUtils.getCurrentUserInfo(mContext).getUserName();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    private void initView() {
        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setOnItemClickListener(this);
        gridAdapter = new GridAdapter(mContext,collectList);
        gridView.setAdapter(gridAdapter);
    }

    @Override
    protected void load() {
        super.load();
        startProgressDialog();
        JSONObject obj = new JSONObject();
        try {
            obj.put("cmd", "getCommodityFavouriteList");
            obj.put("userName", userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.post(mContext, obj, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Gson gson = new Gson();
                GsonCommodityRightBack rightBack = gson.fromJson(new String(bytes), new
                        TypeToken<GsonCommodityRightBack>() {
                        }.getType());
                String result = rightBack.getResult();
                String resultNote = rightBack.getResultNote();
                if (result.equals("0")) {
                    collectList.clear();
                    collectList.addAll(rightBack.getCommodityList());
                    handler.sendEmptyMessage(Constant.REFRESH_REQUEST_SUCCESS);
                } else {
                    Message msg = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("resultNote", resultNote);
                    msg.what = Constant.REFRESH_RESULT_FAIL;
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                handler.sendEmptyMessage(Constant.REFRESH_REQUEST_FAIL);
            }
        }, new HttpUtils.RequestNetworkError() {
            @Override
            public void networkError() {
                handler.sendEmptyMessage(Constant.REFRESH_NITEWORK_FAIL);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent detailIntent = new Intent(mContext,OrderDetailInfoActivity.class);
        detailIntent.putExtra("commodity",collectList.get(position));
        startActivity(detailIntent);
    }
}
