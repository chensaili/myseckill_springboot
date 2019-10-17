package com.csl.service.model;

import java.math.BigDecimal;

public class OrderModel {
    private String id;
    private Integer userId;
    private Integer itemId;
    //若非空，则表示是以秒杀商品方式下单
    private Integer promoId;
    //购买件数
    private Integer amount;
    //购买金额,若promoId非空，则表示秒杀商品价格
    private BigDecimal orderPirce;
    //购买商品单价，若promoId非空，则表示秒杀商品价格
    private BigDecimal itemPrice;

    public Integer getPromoId() {
        return promoId;
    }

    public void setPromoId(Integer promoId) {
        this.promoId = promoId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public BigDecimal getOrderPirce() {
        return orderPirce;
    }

    public void setOrderPirce(BigDecimal orderPirce) {
        this.orderPirce = orderPirce;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }
}
