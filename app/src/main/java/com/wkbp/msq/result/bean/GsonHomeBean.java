package com.wkbp.msq.result.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 首页返回数据
 * Created by shangshuaibo on 2016/11/22 10:35
 */
public class GsonHomeBean implements Serializable {

    private String result;
    private String resultNote;
    private int totalPage;
    /**
     * commodityAdress : 北京中关村
     * commodityBuyNum : 0
     * commodityDitance : 12230.0km
     * commodityID : 7253
     * commodityIcon : http://218.241.30.183:8080//bossgroupimage/shop/5483/product/7253/36255.png
     * commodityIntro : 即将下架，微信群一分免费送，拓展朋友圈
     * commodityMarketPrice : 3.00
     * commodityName : 微信群免费送，拓展朋友圈
     * commodityPrice : 0.01
     * commoditySaleNum : 314
     * commodityScore : 4.6
     * shopID : 5483
     */

    private List<CommodityBean> recommendCommodityList;
    /**
     * commodityAdress : 北京市海淀区
     * commodityBuyNum : 0
     * commodityID : 1505
     * commodityIcon : http://218.241.30.183:8080//bossgroupimage/shop/137/product/1505/7515.png
     * commodityIntro : 1元免费微信群邀请，限量不限人数
     * commodityMarketPrice : 10.00
     * commodityName : 1元5个微信群邀请
     * commodityPrice : 1.00
     * commoditySaleNum : 14
     * commodityScore : 4.8
     * shopID : 137
     */

    private List<CommodityBean> scrollCommodityList;

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

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<CommodityBean> getRecommendCommodityList() {
        return recommendCommodityList;
    }

    public void setRecommendCommodityList(List<CommodityBean> recommendCommodityList) {
        this.recommendCommodityList = recommendCommodityList;
    }

    public List<CommodityBean> getScrollCommodityList() {
        return scrollCommodityList;
    }

    public void setScrollCommodityList(List<CommodityBean> scrollCommodityList) {
        this.scrollCommodityList = scrollCommodityList;
    }

}
