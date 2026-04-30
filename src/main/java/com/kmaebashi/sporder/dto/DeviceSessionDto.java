package com.kmaebashi.sporder.dto;

import com.kmaebashi.dbutil.TableColumn;

import java.time.LocalDateTime;

public class DeviceSessionDto {
    @TableColumn("RT_ID")
    public String rtId;

    @TableColumn(value = "DEVICE_SESSION_ID", trim = true)
    public String deviceSessionId;

    @TableColumn(value = "ORDER_GROUP_ID", trim = true)
    public String orderGroupId;

    @TableColumn("EXPIRES_AT")
    public LocalDateTime expiresAt;
}
