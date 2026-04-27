package com.kmaebashi.sporder.dto;

import com.kmaebashi.dbutil.TableColumn;

public class SubcategoryDto {
    @TableColumn("SUBCATEGORY_ID")
    public int subcategoryId;

    @TableColumn("NAME")
    public String name;
}
