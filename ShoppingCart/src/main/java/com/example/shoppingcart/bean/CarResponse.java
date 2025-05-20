package com.example.shoppingcart.bean;

import java.util.List;

public class CarResponse {

    private int resultCode;

    private List<DataBean> data;

    private  String message;

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

    public static class DataBean{
        private int cartItemId;
        private int goodsCount;
        private  String goodsCoverImg;
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
    }
}
