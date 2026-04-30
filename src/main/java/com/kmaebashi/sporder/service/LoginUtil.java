package com.kmaebashi.sporder.service;

import com.kmaebashi.nctfw.DbAccessInvoker;
import com.kmaebashi.nctfw.ServiceInvoker;
import com.kmaebashi.sporder.dbaccess.LoginDbAccess;
import com.kmaebashi.sporder.dto.DeviceSessionDto;

import java.time.LocalDateTime;

public class LoginUtil {
    private LoginUtil() {}

    public static String checkAndRefreshSession(ServiceInvoker invoker, String sessionToken) throws Exception {
        return invoker.invoke((context) -> {
            DeviceSessionDto dto = LoginDbAccess.getDeviceSessionBySessionTokenForUpdate( context.getDbAccessInvoker(), sessionToken);
            if (dto == null || dto.expiresAt.isBefore(LocalDateTime.now())) {
                return null;
            }

            LoginDbAccess.updateDeviceSessionExpiresAt( context.getDbAccessInvoker(), dto.rtId, dto.deviceSessionId,
                    LocalDateTime.now().plusHours(6));
            return dto.deviceSessionId;
        });
    }
}
