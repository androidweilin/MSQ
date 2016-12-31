package com.wkbp.msq.bean;

import java.io.Serializable;

public class UserInfoBean implements Serializable {
    private static final long serialVersionUID = -1468367843186328890L;
    private boolean isLogin;
    private String userName;
    private String userIcon;
    private String nickName;
    private String score;
    private String passWord;
    private int ticketCount;
    public UserInfoBean() {

    }

    public UserInfoBean(String userName) {
        super();
        this.userName = userName;
    }

    public UserInfoBean(String userName, String passWord) {
        super();
        this.userName = userName;
        this.passWord = passWord;
    }

    public UserInfoBean(String userName, String userIcon, String nickName) {
        super();
        this.userName = userName;
        this.userIcon = userIcon;
        this.nickName = nickName;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

    public String getUserName() {
        return userName;
    }
    
    public int getTicketCount(){
    	return ticketCount;
    }
    public void setTicketCount(int ticketcount){
    	this.ticketCount = ticketcount;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    @Override
    public String toString() {
        return "UserInfo [isLogin=" + isLogin + ", userName=" + userName
                + ", userIcon=" + userIcon + ", nickName=" + nickName
                + ", score=" + score + ", passWord=" + passWord + "]";
    }

}
