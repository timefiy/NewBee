package cn.zzuli.shopapp.adapter;

public class Good {
    private int goodsId;
    private String goodsName;
    private String goodsIntro;
    private String goodsCoverImg;
    private double sellingPrice;

    public Good(int goodsId, String goodsName, String goodsIntro, String goodsCoverImg, double sellingPrice) {
        this.goodsId = goodsId;
        this.goodsName = goodsName;
        this.goodsIntro = goodsIntro;
        this.goodsCoverImg = goodsCoverImg;
        this.sellingPrice = sellingPrice;
    }

    public int getGoodsId() {
        return goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public String getGoodsIntro() {
        return goodsIntro;
    }

    public String getGoodsCoverImg() {
        return goodsCoverImg;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }
}
