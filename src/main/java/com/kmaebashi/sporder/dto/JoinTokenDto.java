package com.kmaebashi.sporder.dto;

import com.kmaebashi.dbutil.TableColumn;

import java.time.LocalDateTime;

public class JoinTokenDto {
    @TableColumn(value = "JOIN_TOKEN_ID", trim = true)
    public String joinTokenId;

    @TableColumn(value = "ORDER_GROUP_ID", trim = true)
    public String orderGroupId;

    @TableColumn("USED")
    public boolean used;

    @TableColumn("CREATED_AT")
    public LocalDateTime createdAt;
}
