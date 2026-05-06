package com.kmaebashi.sporder.dto;

import com.kmaebashi.dbutil.TableColumn;

public class CloseTableDto {
    @TableColumn("RT_ID")
    public String rtId;

    @TableColumn("TABLE_ID")
    public String tableId;

    @TableColumn(value = "TABLE_CODE", trim = true)
    public String tableCode;

    @TableColumn(value = "CURRENT_ORDER_GROUP", trim = true)
    public String currentOrderGroup;
}
