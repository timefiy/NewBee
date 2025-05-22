package cn.zzuli.shopapp.entity;

// created by LWH
// 三级实体类，用于HomeData实体
import com.google.gson.annotations.SerializedName;

public class Carousel {
    @SerializedName("carouselUrl")
    private String carouselUrl;

    @SerializedName("redirectUrl")
    private String redirectUrl;

    public String getCarouselUrl() { return carouselUrl; }
    public String getRedirectUrl() { return redirectUrl; }
}
