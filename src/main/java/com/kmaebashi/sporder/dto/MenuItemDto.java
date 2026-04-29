package com.kmaebashi.sporder.dto;

import com.kmaebashi.dbutil.TableColumn;

public class MenuItemDto {
    @TableColumn("MENU_ITEM_ID")
    public int menuItemId;

    @TableColumn("NAME")
    public String name;

    @TableColumn("PRICE")
    public int price;

    @TableColumn("OPTION_ID")
    public Integer optionId;

    @TableColumn("OPTION_NAME")
    public String optionName;

    @TableColumn("PHOTO_S")
    public String photoS;

    @TableColumn("PHOTO_L")
    public String photoL;
}
