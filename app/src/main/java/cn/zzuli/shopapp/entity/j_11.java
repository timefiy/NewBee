package cn.zzuli.shopapp.entity;

public class j_11 {//商品类
    private    int goodsCoverImg;
    private    int goodsId;
    private    String goodsName;
    private    int sellingPrice;


    public int getGoodsId()
    {
        return goodsId;
    }

    public String getGoodsName()
    {
        return goodsName;
    }

    public void setGoodsName(String goodsName)
    {
        this.goodsName = goodsName;
    }

    public void setGoodsId(int goodsId)
    {
        this.goodsId = goodsId;
    }

    public int getGoodsCoverImg() {
        return goodsCoverImg;
    }

    public void setGoodsCoverImg(int goodsCoverImg) {
        this.goodsCoverImg = goodsCoverImg;
    }

    public int getSellingPrice() {

        return sellingPrice;
    }

    public void setSellingPrice(int sellingPrice) {

        this.sellingPrice = sellingPrice;
    }


}