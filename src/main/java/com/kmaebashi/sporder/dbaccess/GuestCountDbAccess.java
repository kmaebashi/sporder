package com.kmaebashi.sporder.dbaccess;

import com.kmaebashi.dbutil.NamedParameterPreparedStatement;
import com.kmaebashi.nctfw.DbAccessInvoker;

import java.sql.ResultSet;
import java.util.HashMap;

public class GuestCountDbAccess {
    private GuestCountDbAccess() {}

    public static String getRestaurantName(DbAccessInvoker invoker, String rtId, int locale) {
        return invoker.invoke((context) -> {
            String sql = """
SELECT
  WORD.WORD AS NAME
FROM M_RESTAURANTS RT
LEFT OUTER JOIN M_WORDS WORD
  ON RT.RT_ID = WORD.RT_ID
  AND RT.NAME_ID = WORD.WORD_ID
  AND WORD.LOCALE = :LOCALE
WHERE
  RT.RT_ID = :RT_ID
""";

            NamedParameterPreparedStatement npps
                    = NamedParameterPreparedStatement.newInstance(context.getConnection(), sql);
            var params = new HashMap<String, Object>();
            params.put("RT_ID", rtId);
            params.put("LOCALE", locale);
            npps.setParameters(params);
            ResultSet rs = npps.getPreparedStatement().executeQuery();
            if (!rs.next()) {
                return null;
            }

            return rs.getString("NAME");
        });
    }

    public static int updateGuestCount(DbAccessInvoker invoker, String orderGroupId, int guestCount) {
        return invoker.invoke((context) -> {
            String sql = """
UPDATE T_ORDER_GROUPS
SET
  GUEST_COUNT = :GUEST_COUNT
WHERE
  ORDER_GROUP_ID = :ORDER_GROUP_ID
""";

            NamedParameterPreparedStatement npps
                    = NamedParameterPreparedStatement.newInstance(context.getConnection(), sql);
            var params = new HashMap<String, Object>();
            params.put("ORDER_GROUP_ID", orderGroupId);
            params.put("GUEST_COUNT", guestCount);
            npps.setParameters(params);

            return npps.getPreparedStatement().executeUpdate();
        });
    }
}
