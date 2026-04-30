package com.kmaebashi.sporder.dto;

import com.kmaebashi.dbutil.TableColumn;

public class OrderListItemDto {
    @TableColumn("ORDER_ID")
    public int orderId;

    @TableColumn("NAME")
    public String name;

    @TableColumn("COUNT")
    public int count;

    @TableColumn("PRICE")
    public int price;
}
