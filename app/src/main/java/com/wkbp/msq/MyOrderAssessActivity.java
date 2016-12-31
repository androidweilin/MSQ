package com.wkbp.msq;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.wkbp.msq.adapter.AssessAdapter;
import com.wkbp.msq.base.BaseActivity;
import com.wkbp.msq.result.bean.MyOrderBean;
import com.wkbp.msq.util.Constant;
import com.wkbp.msq.util.HttpUtils;
import com.wkbp.msq.util.SharedPreferenceUtils;
import com.wkbp.msq.util.ToastUtil;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyOrderAssessActivity extends BaseActivity implements View.OnClickListener {
    private MyOrderBean myOrderBean;
    private String userName;
    private String orderId;
    private ListView listView;
    private TextView assess_submit;
    private AssessAdapter assessAdapter;
    private RatingBar ratingBar_description;
    private List<String> RatingScoreList = new ArrayList<>();
    private List<String> talkContent = new ArrayList<>();
    private String mRatingScore = "0";
    private int flag = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.REFRESH_REQUEST_SUCCESS:
                    stopProgressDialog();
                    if (flag == myOrderBean.getOrderList().size()) {
                        ToastUtil.showMessageDefault(mContext, "评价提交成功");
                    }
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
        setContentView(R.layout.activity_my_order_assess);
        setTitleBar("评价商品");
        userName = SharedPreferenceUtils.getCurrentUserInfo(mContext).getUserName();
        myOrderBean = (MyOrderBean) getIntent().getSerializableExtra("myOrderBean");
        orderId = myOrderBean.getOrderID();
        initView();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.assess_list);
        assessAdapter = new AssessAdapter(mContext, myOrderBean.getOrderList());
        listView.setAdapter(assessAdapter);
        assess_submit = (TextView) findViewById(R.id.assess_submit);
        assess_submit.setOnClickListener(this);
    }

    private void getChildInfo() {
        RatingScoreList.clear();
        talkContent.clear();
        for (int i = 0; i < myOrderBean.getOrderList().size(); i++) {
            View view = listView.getChildAt(i);
            ratingBar_description = (RatingBar) view.findViewById(R.id.ratingBar_description);
            ratingBar_description.setOnRatingBarChangeListener(descriptionBarChangeListener);
            RatingScoreList.add(mRatingScore);
            EditText assess_content = (EditText) view.findViewById(R.id.assess_content);
            if (TextUtils.isEmpty(assess_content.getText().toString().trim())) {
                ToastUtil.showMessageDefault(mContext, "请对每一个商品进行评价");
                return;
            }
            talkContent.add(assess_content.getText().toString().trim());
        }
        sendAssess();
    }

    private void sendAssess() {
        flag = 0;
        for (int i = 0; i < myOrderBean.getOrderList().size(); i++) {
            startProgressDialog();
            JSONObject obj = new JSONObject();
            try {
                obj.put("cmd", "talkCommodity");
                obj.put("userName", userName);
                obj.put("commodityID", myOrderBean.getOrderList().get(i).getCommodityID());
                obj.put("talkContent", talkContent.get(i));
                obj.put("orderID", orderId);
                obj.put("rateScore", RatingScoreList.get(i));
                obj.put("talkImage", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HttpUtils.post(mContext, obj, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    try {
                        JSONObject obj = new JSONObject(new String(bytes));
                        String result = obj.getString("result");
                        String resultNote = obj.getString("resultNote");
                        if (result.equals("0")) {
                            flag++;
                            handler.sendEmptyMessage(Constant.REFRESH_REQUEST_SUCCESS);
                        } else {
                            Message msg = handler.obtainMessage();
                            Bundle bundle = new Bundle();
                            bundle.putString("resultNote", resultNote);
                            msg.what = Constant.REFRESH_RESULT_FAIL;
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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
    }

    @Override
    public void onClick(View v) {
        getChildInfo();
    }

    private RatingBar.OnRatingBarChangeListener descriptionBarChangeListener = new RatingBar
            .OnRatingBarChangeListener() {
        @Override
        public void onRatingChanged(RatingBar arg0, float rating, boolean arg2) {
            ratingBar_description.setRating(rating);
            mRatingScore = String.valueOf(rating);
            int index = mRatingScore.indexOf(".");
            mRatingScore = mRatingScore.substring(0, index);
        }
    };
}
