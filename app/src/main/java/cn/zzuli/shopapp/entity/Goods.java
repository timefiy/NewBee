package cn.zzuli.shopapp.entity;

import com.google.gson.annotations.SerializedName;

// created by LWH
// 三级实体类，用于HomeData实体
public class Goods {
    @SerializedName("goodsCoverImg")
    private String goodsCoverImg;

    @SerializedName("goodsId")
    private int goodsId;

    @SerializedName("goodsIntro")
    private String goodsIntro;

    @SerializedName("goodsName")
    private String goodsName;

    @SerializedName("sellingPrice")
    private int sellingPrice;

    @SerializedName("tag")
    private String tag;

    public String getGoodsCoverImg() { return goodsCoverImg; }
    public int getGoodsId() { return goodsId; }
    public String getGoodsIntro() { return goodsIntro; }
    public String getGoodsName() { return goodsName; }
    public int getSellingPrice() { return sellingPrice; }
    public String getTag() { return tag; }

    @Override
    public String toString() {
        return "Goods{" +
               "goodsId=" + goodsId +
               ", goodsName='" + goodsName + '\'' +
               ", goodsIntro='" + goodsIntro + '\'' +
               ", goodsCoverImg='" + goodsCoverImg + '\'' +
               ", sellingPrice=" + sellingPrice +
               '}';
    }
}
