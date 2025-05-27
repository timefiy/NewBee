package cn.zzuli.shopapp.entity;

import java.util.List;

/**
 * 购物车接口的响应实体类，用于接收并解析服务器返回的 JSON 数据。
 * Gson 会将 JSON 自动映射到该类中，以便在程序中直接访问数据。
 */
public class CarResponse {

    private int resultCode;

    private List<DataBean> data;

    private String message;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

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

    /**
     * 购物车中单个商品项的数据结构。
     * 对应服务器返回的 data 数组中的每一项。
     */
    public static class DataBean {
        private boolean checked;
        private int cartItemId;
        private int goodsCount;
        private String goodsCoverImg;
        private int goodsId;
        private String goodsName;
        private int sellingPrice;

        public int getCartItemId() {
            return cartItemId;
        }

        public void setCartItemId(int cartItemId) {
            this.cartItemId = cartItemId;
        }

        public int getGoodsCount() {
            return goodsCount;
        }

        public void setGoodsCount(int goodsCount) {
            this.goodsCount = goodsCount;
        }

        public String getGoodsCoverImg() {
            return goodsCoverImg;
        }

        public void setGoodsCoverImg(String goodsCoverImg) {
            this.goodsCoverImg = goodsCoverImg;
        }

        public int getGoodsId() {
            return goodsId;
        }

        public void setGoodsId(int goodsId) {
            this.goodsId = goodsId;
        }

        public String getGoodsName() {
            return goodsName;
        }

        public void setGoodsName(String goodsName) {
            this.goodsName = goodsName;
        }

        public int getSellingPrice() {
            return sellingPrice;
        }

        public void setSellingPrice(int sellingPrice) {
            this.sellingPrice = sellingPrice;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }
    }
}
