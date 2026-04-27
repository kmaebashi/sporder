package com.kmaebashi.sporder.dbaccess;

import com.kmaebashi.dbutil.NamedParameterPreparedStatement;
import com.kmaebashi.dbutil.ResultSetMapper;
import com.kmaebashi.nctfw.DbAccessInvoker;
import com.kmaebashi.sporder.dto.MenuItemDto;
import com.kmaebashi.sporder.dto.SubcategoryDto;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;

public class MenuDbAccess {
    private MenuDbAccess() {}

    public static List<SubcategoryDto> getSubcategories(DbAccessInvoker invoker, String rtId, int categoryId) {
        return invoker.invoke((context) -> {
            String sql = """
SELECT
  SUB.SUBCATEGORY_ID,
  WORD.WORD AS NAME
FROM M_SUBCATEGORIES SUB
LEFT OUTER JOIN M_WORDS WORD
  ON SUB.RT_ID = WORD.RT_ID
  AND SUB.NAME_ID = WORD.WORD_ID
WHERE
  SUB.RT_ID = :RT_ID
  AND SUB.CATEGORY_ID = :CATEGORY_ID
ORDER BY
  SUB.DISPLAY_ORDER
""";

            NamedParameterPreparedStatement npps
                    = NamedParameterPreparedStatement.newInstance(context.getConnection(), sql);
            var params = new HashMap<String, Object>();
            params.put("RT_ID", rtId);
            params.put("CATEGORY_ID", categoryId);
            npps.setParameters(params);
            ResultSet rs = npps.getPreparedStatement().executeQuery();

            return ResultSetMapper.toDtoList(rs, SubcategoryDto.class);
        });
    }

    public static List<MenuItemDto> getMenuItems(DbAccessInvoker invoker, String rtId, int categoryId) {
        return invoker.invoke((context) -> {
            String sql = """
SELECT
  ITEM.MENU_ITEM_ID,
  WORD.WORD AS NAME,
  ITEM.PRICE,
  ITEM.PHOTO_S
FROM M_CATEGORIES_ITEMS CATE_ITEM
INNER JOIN M_MENU_ITEMS ITEM
  ON CATE_ITEM.RT_ID = ITEM.RT_ID
  AND CATE_ITEM.MENU_ITEM_ID = ITEM.MENU_ITEM_ID
LEFT OUTER JOIN M_WORDS WORD
  ON ITEM.RT_ID = WORD.RT_ID
  AND ITEM.NAME_ID = WORD.WORD_ID
WHERE
  CATE_ITEM.RT_ID = :RT_ID
  AND CATE_ITEM.CATEGORY_ID = :CATEGORY_ID
ORDER BY
  CATE_ITEM.DISPLAY_ORDER
""";

            NamedParameterPreparedStatement npps
                    = NamedParameterPreparedStatement.newInstance(context.getConnection(), sql);
            var params = new HashMap<String, Object>();
            params.put("RT_ID", rtId);
            params.put("CATEGORY_ID", categoryId);
            npps.setParameters(params);
            ResultSet rs = npps.getPreparedStatement().executeQuery();

            return ResultSetMapper.toDtoList(rs, MenuItemDto.class);
        });
    }
}
