package com.wkbp.msq.result.bean;

import com.wkbp.msq.bean.UserInfoBean;

import java.io.Serializable;

public class GsonLoginBack implements Serializable {
    private static final long serialVersionUID = -5651197790561028387L;
    private String result;
    private String resultNote;
    private UserInfoBean userinfo;

    public GsonLoginBack(String result, String resultNote,
            UserInfoBean userinfo) {
        super();
        this.result = result;
        this.resultNote = resultNote;
        this.userinfo = userinfo;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResultNote() {
        return resultNote;
    }

    public void setResultNote(String resultNote) {
        this.resultNote = resultNote;
    }

    public UserInfoBean getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(UserInfoBean userinfo) {
        this.userinfo = userinfo;
    }

    @Override
    public String toString() {
        return "GsonLoginBack [result=" + result + ", resultNote=" + resultNote
                + ", userinfo=" + userinfo + "]";
    }

}
