package com.wkbp.msq.result.bean;

import java.io.Serializable;

public class CommodityBean implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 4846032903364493503L;
    /**
     * 是否选中
     */
    private boolean isCheck;
    /**
     * 商品ID
     */
    private String commodityID;
    /**
     * 商品名称
     */
    private String commodityName;
    /**
     * 店铺ID
     */
    private String shopID;
    /**
     * 店铺到当前位置的距离
     */
    private String commodityDitance;

    /**
     * 商品图片地址
     */
    private String commodityIcon;
    /**
     * 商品评分数
     */
    private String commodityScore;
    /**
     * 商品规格介绍
     */
    private String commodityIntro;
    /**
     * 商品现在价格
     */
    private String commodityPrice;
    /**
     * 市场价格
     */
    private String commodityMarketPrice;
    /**
     * 商品产地
     */
    private String commodityAdress;
    /**
     * 月销量
     */
    private String commoditySaleNum;

    /**
     * 商品购买数量
     */
    private String commodityNum;

    /**
     * 我的订单中商品购买数量
     */
    private String commodityBuyNum;

    private String leaveMessage;

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getCommodityDitance() {
        return commodityDitance;
    }

    public void setCommodityDitance(String commodityDitance) {
        this.commodityDitance = commodityDitance;
    }

    public String getCommodityBuyNum() {
        return commodityBuyNum;
    }

    public void setCommodityBuyNum(String commodityBuyNum) {
        this.commodityBuyNum = commodityBuyNum;
    }

    public String getCommodityID() {
        return commodityID;
    }

    public void setCommodityID(String commodityID) {
        this.commodityID = commodityID;
    }

    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    public String getCommodityIcon() {
        return commodityIcon;
    }

    public void setCommodityIcon(String commodityIcon) {
        this.commodityIcon = commodityIcon;
    }

    public String getCommodityScore() {
        return commodityScore;
    }

    public void setCommodityScore(String commodityScore) {
        this.commodityScore = commodityScore;
    }

    public String getCommodityIntro() {
        return commodityIntro;
    }

    public void setCommodityIntro(String commodityIntro) {
        this.commodityIntro = commodityIntro;
    }

    public String getCommodityPrice() {
        return commodityPrice;
    }

    public void setCommodityPrice(String commodityPrice) {
        this.commodityPrice = commodityPrice;
    }

    public String getCommodityMarketPrice() {
        return commodityMarketPrice;
    }

    public void setCommodityMarketPrice(String commodityMarketPrice) {
        this.commodityMarketPrice = commodityMarketPrice;
    }

    public String getCommodityAdress() {
        return commodityAdress;
    }

    public void setCommodityAdress(String commodityAdress) {
        this.commodityAdress = commodityAdress;
    }

    public String getCommoditySaleNum() {
        return commoditySaleNum;
    }

    public void setCommoditySaleNum(String commoditySaleNum) {
        this.commoditySaleNum = commoditySaleNum;
    }

    public String getCommodityNum() {
        return commodityNum;
    }

    public void setCommodityNum(String commodityNum) {
        this.commodityNum = commodityNum;
    }

    public String getLeaveMessage() {
        return leaveMessage;
    }

    public void setLeaveMessage(String leaveMessage) {
        this.leaveMessage = leaveMessage;
    }

    @Override
    public String toString() {
        return "CommodityBean{" +
                "isCheck=" + isCheck +
                ", commodityID='" + commodityID + '\'' +
                ", commodityName='" + commodityName + '\'' +
                ", shopID='" + shopID + '\'' +
                ", commodityDitance='" + commodityDitance + '\'' +
                ", commodityIcon='" + commodityIcon + '\'' +
                ", commodityScore='" + commodityScore + '\'' +
                ", commodityIntro='" + commodityIntro + '\'' +
                ", commodityPrice='" + commodityPrice + '\'' +
                ", commodityMarketPrice='" + commodityMarketPrice + '\'' +
                ", commodityAdress='" + commodityAdress + '\'' +
                ", commoditySaleNum='" + commoditySaleNum + '\'' +
                ", commodityNum='" + commodityNum + '\'' +
                ", commodityBuyNum='" + commodityBuyNum + '\'' +
                ", leaveMessage='" + leaveMessage + '\'' +
                '}';
    }
}
