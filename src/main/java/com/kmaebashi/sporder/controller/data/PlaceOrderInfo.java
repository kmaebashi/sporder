package com.kmaebashi.sporder.controller.data;

public class PlaceOrderInfo {
    public static class OrderItem {
        public int orderId;
        public boolean deleted;
    }
    public String rtId;
    public String orderGroupId;
    public OrderItem[] orderItems;
}
