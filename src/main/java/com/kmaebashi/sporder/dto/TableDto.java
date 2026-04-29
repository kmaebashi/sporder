package com.kmaebashi.sporder.dto;

import com.kmaebashi.dbutil.TableColumn;

public class TableDto {
    @TableColumn("TABLE_ID")
    public String tableId;

    @TableColumn(value = "CURRENT_ORDER_GROUP", trim = true)
    public String currentOrderGroup;
}
