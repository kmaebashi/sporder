package com.kmaebashi.sporder.dto;

import com.kmaebashi.dbutil.TableColumn;

public class CategoryDto {
    @TableColumn("CATEGORY_ID")
    public int categoryId;

    @TableColumn("NAME")
    public String name;
}
