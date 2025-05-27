package cn.zzuli.shopapp.entity;

// created by LWH
// 一级实体类
// json 解析 /api/v1/index-infos
import com.google.gson.annotations.SerializedName;

public class ApiResponseHome {
    private int resultCode;
    private String message;
    private HomeData data;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HomeData getData() {
        return data;
    }

    public void setData(HomeData data) {
        this.data = data;
    }
}
