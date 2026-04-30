package com.kmaebashi.sporder.dto;

import com.kmaebashi.dbutil.TableColumn;

public class OrderHistoryItemDto {
    @TableColumn("NAME")
    public String name;

    @TableColumn("COUNT")
    public int count;

    @TableColumn("TOTAL_PRICE")
    public int totalPrice;
}
