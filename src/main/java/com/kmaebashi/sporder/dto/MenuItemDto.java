package com.kmaebashi.sporder.dto;

import com.kmaebashi.dbutil.TableColumn;

public class MenuItemDto {
    @TableColumn("MENU_ITEM_ID")
    public int menuItemId;

    @TableColumn("NAME")
    public String name;

    @TableColumn("PRICE")
    public int price;

    @TableColumn("PHOTO_S")
    public String photoS;
}
