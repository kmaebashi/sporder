package com.kmaebashi.sporder.dbaccess;

import com.kmaebashi.dbutil.NamedParameterPreparedStatement;
import com.kmaebashi.dbutil.ResultSetMapper;
import com.kmaebashi.nctfw.DbAccessInvoker;
import com.kmaebashi.sporder.dto.CategoryDto;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;

public class CategoryDbAccess {
    private CategoryDbAccess() {}

    public static List<CategoryDto> getCategories(DbAccessInvoker invoker, String rtId, int locale){
        return invoker.invoke((context) -> {
            String sql = """
SELECT
  WORD.WORD AS NAME,
  CATE.CATEGORY_ID
FROM M_CATEGORIES CATE
LEFT OUTER JOIN M_WORDS WORD
  ON CATE.RT_ID = WORD.RT_ID
  AND CATE.NAME_ID = WORD.WORD_ID
  AND WORD.LOCALE = :LOCALE
WHERE
  CATE.RT_ID = :RT_ID
ORDER BY
  CATE.DISPLAY_ORDER
""";

            NamedParameterPreparedStatement npps
                    = NamedParameterPreparedStatement.newInstance(context.getConnection(), sql);
            var params = new HashMap<String, Object>();
            params.put("RT_ID", rtId);
            params.put("LOCALE", locale);
            npps.setParameters(params);
            ResultSet rs = npps.getPreparedStatement().executeQuery();
            List<CategoryDto> dtoList = ResultSetMapper.toDtoList(rs, CategoryDto.class);

            return dtoList;
        });
    }
}
