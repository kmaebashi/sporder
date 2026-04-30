package com.kmaebashi.sporder.dbaccess;

import com.kmaebashi.dbutil.NamedParameterPreparedStatement;
import com.kmaebashi.dbutil.ResultSetMapper;
import com.kmaebashi.nctfw.DbAccessInvoker;
import com.kmaebashi.sporder.dto.OrderHistoryItemDto;
import com.kmaebashi.sporder.dto.OrderListItemDto;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public class OrderDbAccess {
    private OrderDbAccess() {}

    public static Integer getNextOrderIdForUpdate(DbAccessInvoker invoker, String rtId) {
        return invoker.invoke((context) -> {
            String sql = """
SELECT
  NEXT_ORDER_ID
FROM S_ORDER_ID
WHERE
  RT_ID = :RT_ID
FOR UPDATE
""";

            NamedParameterPreparedStatement npps
                    = NamedParameterPreparedStatement.newInstance(context.getConnection(), sql);
            var params = new HashMap<String, Object>();
            params.put("RT_ID", rtId);
            npps.setParameters(params);
            ResultSet rs = npps.getPreparedStatement().executeQuery();

            if (!rs.next()) {
                return null;
            }
            return rs.getInt("NEXT_ORDER_ID");
        });
    }

    public static int updateNextOrderId(DbAccessInvoker invoker, String rtId, int nextOrderId,
                                        LocalDateTime updatedAt) {
        return invoker.invoke((context) -> {
            String sql = """
UPDATE S_ORDER_ID
SET
  NEXT_ORDER_ID = :NEXT_ORDER_ID,
  UPDATED_AT = :UPDATED_AT
WHERE
  RT_ID = :RT_ID
""";

            NamedParameterPreparedStatement npps
                    = NamedParameterPreparedStatement.newInstance(context.getConnection(), sql);
            var params = new HashMap<String, Object>();
            params.put("RT_ID", rtId);
            params.put("NEXT_ORDER_ID", nextOrderId);
            params.put("UPDATED_AT", updatedAt);
            npps.setParameters(params);

            return npps.getPreparedStatement().executeUpdate();
        });
    }

    public static int insertOrder(DbAccessInvoker invoker, String rtId, String orderGroupId, int orderId,
                                  int menuItemId, int count, LocalDateTime createdAt) {
        return invoker.invoke((context) -> {
            String sql = """
INSERT INTO T_ORDERS (
  RT_ID,
  ORDER_GROUP_ID,
  ORDER_ID,
  MENU_ITEM_ID,
  COUNT,
  CREATED_AT,
  ORDERED_AT
) VALUES (
  :RT_ID,
  :ORDER_GROUP_ID,
  :ORDER_ID,
  :MENU_ITEM_ID,
  :COUNT,
  :CREATED_AT,
  NULL
)
""";

            NamedParameterPreparedStatement npps
                    = NamedParameterPreparedStatement.newInstance(context.getConnection(), sql);
            var params = new HashMap<String, Object>();
            params.put("RT_ID", rtId);
            params.put("ORDER_GROUP_ID", orderGroupId);
            params.put("ORDER_ID", orderId);
            params.put("MENU_ITEM_ID", menuItemId);
            params.put("COUNT", count);
            params.put("CREATED_AT", createdAt);
            npps.setParameters(params);

            return npps.getPreparedStatement().executeUpdate();
        });
    }

    public static List<OrderListItemDto> getUnplacedOrderList(DbAccessInvoker invoker, String rtId,
                                                              String orderGroupId, int locale) {
        return invoker.invoke((context) -> {
            String sql = """
SELECT
  ORD.ORDER_ID,
  WORD.WORD AS NAME,
  ORD.COUNT,
  ITEM.PRICE
FROM T_ORDERS ORD
INNER JOIN M_MENU_ITEMS ITEM
  ON ORD.RT_ID = ITEM.RT_ID
  AND ORD.MENU_ITEM_ID = ITEM.MENU_ITEM_ID
LEFT OUTER JOIN M_WORDS WORD
  ON ITEM.RT_ID = WORD.RT_ID
  AND ITEM.NAME_ID = WORD.WORD_ID
  AND WORD.LOCALE = :LOCALE
WHERE
  ORD.RT_ID = :RT_ID
  AND ORD.ORDER_GROUP_ID = :ORDER_GROUP_ID
  AND ORD.ORDERED_AT IS NULL
  AND ORD.DELETED_AT IS NULL
ORDER BY
  ORD.CREATED_AT
""";

            NamedParameterPreparedStatement npps
                    = NamedParameterPreparedStatement.newInstance(context.getConnection(), sql);
            var params = new HashMap<String, Object>();
            params.put("RT_ID", rtId);
            params.put("ORDER_GROUP_ID", orderGroupId);
            params.put("LOCALE", locale);
            npps.setParameters(params);
            ResultSet rs = npps.getPreparedStatement().executeQuery();

            return ResultSetMapper.toDtoList(rs, OrderListItemDto.class);
        });
    }

    public static int updateOrderDeletedAt(DbAccessInvoker invoker, String rtId, String orderGroupId,
                                           int orderId, LocalDateTime deletedAt) {
        return invoker.invoke((context) -> {
            String sql = """
UPDATE T_ORDERS
SET
  DELETED_AT = :DELETED_AT
WHERE
  RT_ID = :RT_ID
  AND ORDER_GROUP_ID = :ORDER_GROUP_ID
  AND ORDER_ID = :ORDER_ID
  AND ORDERED_AT IS NULL
  AND DELETED_AT IS NULL
""";

            NamedParameterPreparedStatement npps
                    = NamedParameterPreparedStatement.newInstance(context.getConnection(), sql);
            var params = new HashMap<String, Object>();
            params.put("RT_ID", rtId);
            params.put("ORDER_GROUP_ID", orderGroupId);
            params.put("ORDER_ID", orderId);
            params.put("DELETED_AT", deletedAt);
            npps.setParameters(params);

            return npps.getPreparedStatement().executeUpdate();
        });
    }

    public static int updateOrderOrderedAt(DbAccessInvoker invoker, String rtId, String orderGroupId,
                                           int orderId, LocalDateTime orderedAt) {
        return invoker.invoke((context) -> {
            String sql = """
UPDATE T_ORDERS
SET
  ORDERED_AT = :ORDERED_AT
WHERE
  RT_ID = :RT_ID
  AND ORDER_GROUP_ID = :ORDER_GROUP_ID
  AND ORDER_ID = :ORDER_ID
  AND ORDERED_AT IS NULL
  AND DELETED_AT IS NULL
""";

            NamedParameterPreparedStatement npps
                    = NamedParameterPreparedStatement.newInstance(context.getConnection(), sql);
            var params = new HashMap<String, Object>();
            params.put("RT_ID", rtId);
            params.put("ORDER_GROUP_ID", orderGroupId);
            params.put("ORDER_ID", orderId);
            params.put("ORDERED_AT", orderedAt);
            npps.setParameters(params);

            return npps.getPreparedStatement().executeUpdate();
        });
    }

    public static List<OrderHistoryItemDto> getOrderHistoryList(DbAccessInvoker invoker, String rtId,
                                                                String orderGroupId, int locale) {
        return invoker.invoke((context) -> {
            String sql = """
SELECT
  WORD.WORD AS NAME,
  CAST(SUM(ORD.COUNT) AS INTEGER) AS COUNT,
  CAST(SUM(ITEM.PRICE * ORD.COUNT) AS INTEGER) AS TOTAL_PRICE
FROM T_ORDERS ORD
INNER JOIN M_MENU_ITEMS ITEM
  ON ORD.RT_ID = ITEM.RT_ID
  AND ORD.MENU_ITEM_ID = ITEM.MENU_ITEM_ID
LEFT OUTER JOIN M_WORDS WORD
  ON ITEM.RT_ID = WORD.RT_ID
  AND ITEM.NAME_ID = WORD.WORD_ID
  AND WORD.LOCALE = :LOCALE
WHERE
  ORD.RT_ID = :RT_ID
  AND ORD.ORDER_GROUP_ID = :ORDER_GROUP_ID
  AND ORD.ORDERED_AT IS NOT NULL
  AND ORD.DELETED_AT IS NULL
GROUP BY
  ORD.MENU_ITEM_ID,
  WORD.WORD
ORDER BY
  MAX(ORD.CREATED_AT)
""";

            NamedParameterPreparedStatement npps
                    = NamedParameterPreparedStatement.newInstance(context.getConnection(), sql);
            var params = new HashMap<String, Object>();
            params.put("RT_ID", rtId);
            params.put("ORDER_GROUP_ID", orderGroupId);
            params.put("LOCALE", locale);
            npps.setParameters(params);
            ResultSet rs = npps.getPreparedStatement().executeQuery();

            return ResultSetMapper.toDtoList(rs, OrderHistoryItemDto.class);
        });
    }
}
