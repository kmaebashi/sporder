package com.kmaebashi.sporder.dbaccess;

import com.kmaebashi.dbutil.NamedParameterPreparedStatement;
import com.kmaebashi.dbutil.ResultSetMapper;
import com.kmaebashi.nctfw.DbAccessInvoker;
import com.kmaebashi.sporder.dto.CloseTableDto;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.HashMap;

public class CloseTableDbAccess {
    private CloseTableDbAccess() {}

    public static CloseTableDto getTableForUpdate(DbAccessInvoker invoker, String tableCode) {
        return invoker.invoke((context) -> {
            String sql = """
SELECT
  RT_ID,
  TABLE_ID,
  TABLE_CODE,
  CURRENT_ORDER_GROUP
FROM M_TABLES
WHERE
  TABLE_CODE = :TABLE_CODE
FOR UPDATE
""";

            NamedParameterPreparedStatement npps
                    = NamedParameterPreparedStatement.newInstance(context.getConnection(), sql);
            var params = new HashMap<String, Object>();
            params.put("TABLE_CODE", tableCode);
            npps.setParameters(params);
            ResultSet rs = npps.getPreparedStatement().executeQuery();

            return ResultSetMapper.toDto(rs, CloseTableDto.class);
        });
    }

    public static CloseTableDto getTableByOrderGroup(DbAccessInvoker invoker, String rtId, String orderGroupId) {
        return invoker.invoke((context) -> {
            String sql = """
SELECT
  RT_ID,
  TABLE_ID,
  TABLE_CODE,
  CURRENT_ORDER_GROUP
FROM M_TABLES
WHERE
  RT_ID = :RT_ID
  AND CURRENT_ORDER_GROUP = :ORDER_GROUP_ID
""";

            NamedParameterPreparedStatement npps
                    = NamedParameterPreparedStatement.newInstance(context.getConnection(), sql);
            var params = new HashMap<String, Object>();
            params.put("RT_ID", rtId);
            params.put("ORDER_GROUP_ID", orderGroupId);
            npps.setParameters(params);
            ResultSet rs = npps.getPreparedStatement().executeQuery();

            return ResultSetMapper.toDto(rs, CloseTableDto.class);
        });
    }

    public static int updateOrderGroupClosedAt(DbAccessInvoker invoker, String rtId, String orderGroupId,
                                               LocalDateTime closedAt) {
        return invoker.invoke((context) -> {
            String sql = """
UPDATE T_ORDER_GROUPS
SET
  CLOSED_AT = :CLOSED_AT
WHERE
  RT_ID = :RT_ID
  AND ORDER_GROUP_ID = :ORDER_GROUP_ID
""";

            NamedParameterPreparedStatement npps
                    = NamedParameterPreparedStatement.newInstance(context.getConnection(), sql);
            var params = new HashMap<String, Object>();
            params.put("RT_ID", rtId);
            params.put("ORDER_GROUP_ID", orderGroupId);
            params.put("CLOSED_AT", closedAt);
            npps.setParameters(params);

            return npps.getPreparedStatement().executeUpdate();
        });
    }

    public static int clearTableCurrentOrderGroup(DbAccessInvoker invoker, String rtId, String tableId,
                                                  LocalDateTime updatedAt) {
        return invoker.invoke((context) -> {
            String sql = """
UPDATE M_TABLES
SET
  CURRENT_ORDER_GROUP = NULL,
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
            params.put("UPDATED_AT", updatedAt);
            npps.setParameters(params);

            return npps.getPreparedStatement().executeUpdate();
        });
    }
}
