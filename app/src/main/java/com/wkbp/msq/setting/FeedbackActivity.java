package com.wkbp.msq.setting;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.wkbp.msq.R;
import com.wkbp.msq.base.BaseActivity;
import com.wkbp.msq.util.ToastUtil;

public class FeedbackActivity extends BaseActivity implements View.OnClickListener {
    private EditText et_theme, et_content;
    private TextView tv_pointer, tv_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_feedback);
        setTitleBar("建议反馈");
        initView();
    }

    private void initView() {
        et_theme = (EditText) findViewById(R.id.et_theme);
        et_content = (EditText) findViewById(R.id.et_content);
        tv_pointer = (TextView) findViewById(R.id.tv_pointer);
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        tv_confirm.setOnClickListener(this);

        et_content.addTextChangedListener(new TextWatcher() {
            private CharSequence wordNum;//记录输入的字数
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                wordNum = s;//实时记录输入的字数
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (wordNum.length() >= 250) {
                    ToastUtil.showMessageDefault(mContext, "只能输入250个字");
                }
                int number = s.length();
                //TextView显示剩余字数
                tv_pointer.setText(number + "/250");
                selectionStart = et_content.getSelectionStart();
                selectionEnd = et_content.getSelectionEnd();
                if (wordNum.length() > 250) {
                    //删除多余输入的字（不会显示出来）
                    s.delete(selectionStart - 1, selectionEnd);
                    int tempSelection = selectionEnd;
                    et_content.setText(s);
                    et_content.setSelection(tempSelection);//设置光标在最后
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm:
                if (TextUtils.isEmpty(et_theme.getText().toString().trim())) {
                    ToastUtil.showMessageDefault(mContext, "请输入反馈主题");
                } else if (TextUtils.isEmpty(et_content.getText().toString().trim())) {
                    ToastUtil.showMessageDefault(mContext, "请输入反馈内容");
                }else {
                    startProgressDialog();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            stopProgressDialog();
                            ToastUtil.showMessageDefault(mContext, "反馈成功，感谢您宝贵的意见");
                        }
                    },1000);
                }
                break;
        }
    }
}
