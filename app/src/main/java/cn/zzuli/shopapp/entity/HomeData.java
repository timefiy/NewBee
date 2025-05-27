package cn.zzuli.shopapp.entity;
// created by LWH

import com.google.gson.annotations.SerializedName;
import java.util.List;

// 二级实体类，用于ApiResponseHome实体
public class HomeData {
    @SerializedName("carousels")
    private List<Carousel> carousels;

    @SerializedName("hotGoodses")
    private List<Goods> hotGoodses;

    @SerializedName("newGoodses")
    private List<Goods> newGoodses;

    @SerializedName("recommendGoodses")
    private List<Goods> recommendGoodses;

    public List<Carousel> getCarousels() { return carousels; }
    public List<Goods> getHotGoodses() { return hotGoodses; }
    public List<Goods> getNewGoodses() { return newGoodses; }
    public List<Goods> getRecommendGoodses() { return recommendGoodses; }
}
