package com.kmaebashi.sporder.dbaccess;

import com.kmaebashi.dbutil.NamedParameterPreparedStatement;
import com.kmaebashi.dbutil.ResultSetMapper;
import com.kmaebashi.nctfw.DbAccessInvoker;
import com.kmaebashi.sporder.dto.DeviceSessionDto;
import com.kmaebashi.sporder.dto.JoinTokenDto;
import com.kmaebashi.sporder.dto.TableDto;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.HashMap;

public class LoginDbAccess {
    private LoginDbAccess() {}

    public static TableDto getTableForUpdate(DbAccessInvoker invoker, String rtId, String tableCode) {
        return invoker.invoke((context) -> {
            String sql = """
SELECT
  TABLE_ID,
  CURRENT_ORDER_GROUP
FROM M_TABLES
WHERE
  RT_ID = :RT_ID
  AND TABLE_CODE = :TABLE_CODE
FOR UPDATE
""";

            NamedParameterPreparedStatement npps
                    = NamedParameterPreparedStatement.newInstance(context.getConnection(), sql);
            var params = new HashMap<String, Object>();
            params.put("RT_ID", rtId);
            params.put("TABLE_CODE", tableCode);
            npps.setParameters(params);
            ResultSet rs = npps.getPreparedStatement().executeQuery();

            return ResultSetMapper.toDto(rs, TableDto.class);
        });
    }

    public static int insertOrderGroup(DbAccessInvoker invoker, String rtId, String orderGroupId, String tableId,
                                       LocalDateTime createdAt) {
        return invoker.invoke((context) -> {
            String sql = """
INSERT INTO T_ORDER_GROUPS (
  RT_ID,
  ORDER_GROUP_ID,
  TABLE_ID,
  GUEST_COUNT,
  CREATED_AT
) VALUES (
  :RT_ID,
  :ORDER_GROUP_ID,
  :TABLE_ID,
  NULL,
  :CREATED_AT
)
""";

            NamedParameterPreparedStatement npps
                    = NamedParameterPreparedStatement.newInstance(context.getConnection(), sql);
            var params = new HashMap<String, Object>();
            params.put("RT_ID", rtId);
            params.put("ORDER_GROUP_ID", orderGroupId);
            params.put("TABLE_ID", tableId);
            params.put("CREATED_AT", createdAt);
            npps.setParameters(params);

            return npps.getPreparedStatement().executeUpdate();
        });
    }

    public static int updateTableCurrentOrderGroup(DbAccessInvoker invoker, String rtId, String tableId,
                                                   String orderGroupId, LocalDateTime updatedAt) {
        return invoker.invoke((context) -> {
            String sql = """
UPDATE M_TABLES
SET
  CURRENT_ORDER_GROUP = :ORDER_GROUP_ID,
  UPDATED_AT = :UPDATED_AT
WHERE
  RT_ID = :RT_ID
  AND TABLE_ID = :TABLE_ID
""";

            NamedParameterPreparedStatement npps
                    = NamedParameterPreparedStatement.newInstance(context.getConnection(), sql);
            var params = new HashMap<String, Object>();
            params.put("RT_ID", rtId);
            params.put("TABLE_ID", tableId);
            params.put("ORDER_GROUP_ID", orderGroupId);
            params.put("UPDATED_AT", updatedAt);
            npps.setParameters(params);

            return npps.getPreparedStatement().executeUpdate();
        });
    }

    public static int insertJoinToken(DbAccessInvoker invoker, String rtId, String joinTokenId, String orderGroupId,
                                      LocalDateTime createdAt) {
        return invoker.invoke((context) -> {
            String sql = """
INSERT INTO T_JOIN_TOKENS (
  RT_ID,
  JOIN_TOKEN_ID,
  ORDER_GROUP_ID,
  CREATED_AT,
  USED
) VALUES (
  :RT_ID,
  :JOIN_TOKEN_ID,
  :ORDER_GROUP_ID,
  :CREATED_AT,
  FALSE
)
""";

            NamedParameterPreparedStatement npps
                    = NamedParameterPreparedStatement.newInstance(context.getConnection(), sql);
            var params = new HashMap<String, Object>();
            params.put("RT_ID", rtId);
            params.put("JOIN_TOKEN_ID", joinTokenId);
            params.put("ORDER_GROUP_ID", orderGroupId);
            params.put("CREATED_AT", createdAt);
            npps.setParameters(params);

            return npps.getPreparedStatement().executeUpdate();
        });
    }

    public static JoinTokenDto getJoinTokenForUpdate(DbAccessInvoker invoker, String rtId, String orderGroupId,
                                                     String joinTokenId) {
        return invoker.invoke((context) -> {
            String sql = """
SELECT
  JOIN_TOKEN_ID,
  ORDER_GROUP_ID,
  USED,
  CREATED_AT
FROM T_JOIN_TOKENS
WHERE
  RT_ID = :RT_ID
  AND ORDER_GROUP_ID = :ORDER_GROUP_ID
  AND JOIN_TOKEN_ID = :JOIN_TOKEN_ID
FOR UPDATE
""";

            NamedParameterPreparedStatement npps
                    = NamedParameterPreparedStatement.newInstance(context.getConnection(), sql);
            var params = new HashMap<String, Object>();
            params.put("RT_ID", rtId);
            params.put("ORDER_GROUP_ID", orderGroupId);
            params.put("JOIN_TOKEN_ID", joinTokenId);
            npps.setParameters(params);
            ResultSet rs = npps.getPreparedStatement().executeQuery();

            return ResultSetMapper.toDto(rs, JoinTokenDto.class);
        });
    }

    public static int updateJoinTokenUsed(DbAccessInvoker invoker, String rtId, String joinTokenId) {
        return invoker.invoke((context) -> {
            String sql = """
UPDATE T_JOIN_TOKENS
SET
  USED = TRUE
WHERE
  RT_ID = :RT_ID
  AND JOIN_TOKEN_ID = :JOIN_TOKEN_ID
""";

            NamedParameterPreparedStatement npps
                    = NamedParameterPreparedStatement.newInstance(context.getConnection(), sql);
            var params = new HashMap<String, Object>();
            params.put("RT_ID", rtId);
            params.put("JOIN_TOKEN_ID", joinTokenId);
            npps.setParameters(params);

            return npps.getPreparedStatement().executeUpdate();
        });
    }

    public static int insertDeviceSession(DbAccessInvoker invoker, String rtId, String deviceSessionId,
                                          String orderGroupId, String sessionToken, LocalDateTime expiresAt,
                                          LocalDateTime createdAt) {
        return invoker.invoke((context) -> {
            String sql = """
INSERT INTO T_DEVICE_SESSIONS (
  RT_ID,
  DEVICE_SESSION_ID,
  ORDER_GROUP_ID,
  SESSION_TOKEN,
  EXPIRES_AT,
  CREATED_AT
) VALUES (
  :RT_ID,
  :DEVICE_SESSION_ID,
  :ORDER_GROUP_ID,
  :SESSION_TOKEN,
  :EXPIRES_AT,
  :CREATED_AT
)
""";

            NamedParameterPreparedStatement npps
                    = NamedParameterPreparedStatement.newInstance(context.getConnection(), sql);
            var params = new HashMap<String, Object>();
            params.put("RT_ID", rtId);
            params.put("DEVICE_SESSION_ID", deviceSessionId);
            params.put("ORDER_GROUP_ID", orderGroupId);
            params.put("SESSION_TOKEN", sessionToken);
            params.put("EXPIRES_AT", expiresAt);
            params.put("CREATED_AT", createdAt);
            npps.setParameters(params);

            return npps.getPreparedStatement().executeUpdate();
        });
    }

    public static DeviceSessionDto getDeviceSessionBySessionTokenForUpdate(DbAccessInvoker invoker,
                                                                           String sessionToken) {
        return invoker.invoke((context) -> {
            String sql = """
SELECT
  RT_ID,
  DEVICE_SESSION_ID,
  ORDER_GROUP_ID,
  EXPIRES_AT
FROM T_DEVICE_SESSIONS
WHERE
  SESSION_TOKEN = :SESSION_TOKEN
FOR UPDATE
""";

            NamedParameterPreparedStatement npps
                    = NamedParameterPreparedStatement.newInstance(context.getConnection(), sql);
            var params = new HashMap<String, Object>();
            params.put("SESSION_TOKEN", sessionToken);
            npps.setParameters(params);
            ResultSet rs = npps.getPreparedStatement().executeQuery();

            return ResultSetMapper.toDto(rs, DeviceSessionDto.class);
        });
    }

    public static int updateDeviceSessionExpiresAt(DbAccessInvoker invoker, String rtId, String deviceSessionId,
                                                   LocalDateTime expiresAt) {
        return invoker.invoke((context) -> {
            String sql = """
UPDATE T_DEVICE_SESSIONS
SET
  EXPIRES_AT = :EXPIRES_AT
WHERE
  RT_ID = :RT_ID
  AND DEVICE_SESSION_ID = :DEVICE_SESSION_ID
""";

            NamedParameterPreparedStatement npps
                    = NamedParameterPreparedStatement.newInstance(context.getConnection(), sql);
            var params = new HashMap<String, Object>();
            params.put("RT_ID", rtId);
            params.put("DEVICE_SESSION_ID", deviceSessionId);
            params.put("EXPIRES_AT", expiresAt);
            npps.setParameters(params);

            return npps.getPreparedStatement().executeUpdate();
        });
    }
}
