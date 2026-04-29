package com.kmaebashi.sporder.controller.data;

import com.kmaebashi.dbutil.TableColumn;

import java.util.List;

public class MenuItemInfo {
    public static class OptionInfo {
        @TableColumn("ID")
        public int id;

        @TableColumn("NAME")
        public String name;
    }
    public int menuItemId;
    public String name;
    public int price;
    public String optionName;
    public List<OptionInfo> optionList;
}
