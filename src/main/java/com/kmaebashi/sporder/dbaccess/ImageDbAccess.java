package com.kmaebashi.sporder.dbaccess;

import com.kmaebashi.dbutil.NamedParameterPreparedStatement;
import com.kmaebashi.dbutil.ResultSetMapper;
import com.kmaebashi.nctfw.DbAccessInvoker;
import com.kmaebashi.sporder.dto.MenuItemDto;

import java.sql.ResultSet;
import java.util.HashMap;

public class ImageDbAccess {
    private ImageDbAccess() {}

    public static MenuItemDto getMenuItem(DbAccessInvoker invoker, String rtId, int menuItemId) {
        return invoker.invoke((context) -> {
            String sql = """
SELECT
  MENU_ITEM_ID,
  PHOTO_S
FROM M_MENU_ITEMS
WHERE
  RT_ID = :RT_ID
  AND MENU_ITEM_ID = :MENU_ITEM_ID
""";

            NamedParameterPreparedStatement npps
                    = NamedParameterPreparedStatement.newInstance(context.getConnection(), sql);
            var params = new HashMap<String, Object>();
            params.put("RT_ID", rtId);
            params.put("MENU_ITEM_ID", menuItemId);
            npps.setParameters(params);
            ResultSet rs = npps.getPreparedStatement().executeQuery();

            return ResultSetMapper.toDto(rs, MenuItemDto.class);
        });
    }
}
