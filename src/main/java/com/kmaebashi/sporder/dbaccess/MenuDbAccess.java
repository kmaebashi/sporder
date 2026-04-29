package com.kmaebashi.sporder.dbaccess;

import com.kmaebashi.dbutil.NamedParameterPreparedStatement;
import com.kmaebashi.dbutil.ResultSetMapper;
import com.kmaebashi.nctfw.DbAccessInvoker;
import com.kmaebashi.sporder.controller.data.MenuItemInfo;
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

    public static MenuItemDto getMenuItem(DbAccessInvoker invoker, String rtId, int menuItemId) {
        return invoker.invoke((context) -> {
            String sql = """
SELECT
  ITEM.MENU_ITEM_ID,
  MENU_WORD.WORD AS NAME,
  ITEM.PRICE,
  ITEM.OPTION_ID,
  OPTION_WORD.WORD AS OPTION_NAME
FROM M_MENU_ITEMS ITEM
LEFT OUTER JOIN M_WORDS MENU_WORD
  ON ITEM.RT_ID = MENU_WORD.RT_ID
  AND ITEM.NAME_ID = MENU_WORD.WORD_ID
  AND MENU_WORD.LOCALE = 1
LEFT OUTER JOIN M_OPTIONS OPT
  ON ITEM.RT_ID = OPT.RT_ID
  AND ITEM.OPTION_ID = OPT.OPTION_ID
LEFT OUTER JOIN M_WORDS OPTION_WORD
  ON OPT.RT_ID = OPTION_WORD.RT_ID
  AND OPT.NAME_ID = OPTION_WORD.WORD_ID
  AND OPTION_WORD.LOCALE = 1
WHERE
  ITEM.RT_ID = :RT_ID
  AND ITEM.MENU_ITEM_ID = :MENU_ITEM_ID
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

    public static List<MenuItemInfo.OptionInfo> getOptionList(DbAccessInvoker invoker, String rtId, int optionId) {
        return invoker.invoke((context) -> {
            String sql = """
SELECT
  VAL.OPTION_VALUE_ID AS ID,
  WORD.WORD AS NAME
FROM M_OPTION_VALUES VAL
LEFT OUTER JOIN M_WORDS WORD
  ON VAL.RT_ID = WORD.RT_ID
  AND VAL.NAME_ID = WORD.WORD_ID
  AND WORD.LOCALE = 1
WHERE
  VAL.RT_ID = :RT_ID
  AND VAL.OPTION_ID = :OPTION_ID
ORDER BY
  VAL.OPTION_VALUE_ID
""";

            NamedParameterPreparedStatement npps
                    = NamedParameterPreparedStatement.newInstance(context.getConnection(), sql);
            var params = new HashMap<String, Object>();
            params.put("RT_ID", rtId);
            params.put("OPTION_ID", optionId);
            npps.setParameters(params);
            ResultSet rs = npps.getPreparedStatement().executeQuery();

            return ResultSetMapper.toDtoList(rs, MenuItemInfo.OptionInfo.class);
        });
    }
}
