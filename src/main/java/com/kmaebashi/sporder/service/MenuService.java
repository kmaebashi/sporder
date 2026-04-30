package com.kmaebashi.sporder.service;

import com.kmaebashi.jsonparser.ClassMapper;
import com.kmaebashi.nctfw.DocumentResult;
import com.kmaebashi.nctfw.JsonResult;
import com.kmaebashi.nctfw.NotFoundException;
import com.kmaebashi.nctfw.ServiceContext;
import com.kmaebashi.nctfw.ServiceInvoker;
import com.kmaebashi.sporder.common.Locale;
import com.kmaebashi.sporder.controller.data.MenuItemInfo;
import com.kmaebashi.sporder.dbaccess.CategoryDbAccess;
import com.kmaebashi.sporder.dbaccess.MenuDbAccess;
import com.kmaebashi.sporder.dto.CategoryDto;
import com.kmaebashi.sporder.dto.MenuItemDto;
import com.kmaebashi.sporder.dto.SubcategoryDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

public class MenuService {
    private MenuService() {}

    public static DocumentResult showMenu(ServiceInvoker invoker, String rtId, int categoryId, Locale locale) {
        return invoker.invoke((context) -> {
            Path htmlPath = context.getHtmlTemplateDirectory().resolve("menu.html");
            Document doc = Jsoup.parse(htmlPath.toFile(), "UTF-8");
            DocumentResult ret = new DocumentResult(doc);

            renderCategoryBar(context, doc, rtId, categoryId, locale);
            renderMenuList(context, doc, rtId, categoryId, locale);

            return ret;
        });
    }

    public static JsonResult getMenuItemInfo(ServiceInvoker invoker, String rtId, int menuItemId, Locale locale) {
        return invoker.invoke((context) -> {
           MenuItemDto dto = MenuDbAccess.getMenuItem(context.getDbAccessInvoker(), rtId, menuItemId,
                   locale.getCode());
           if (dto == null) {
               throw new NotFoundException("メニューアイテムが見つかりません。");
           }

           MenuItemInfo info = new MenuItemInfo();
           info.menuItemId = dto.menuItemId;
           info.name = dto.name;
           info.price = dto.price;
           if (dto.optionId == null) {
               info.optionName = null;
               info.optionList = null;
           } else {
               info.optionName = dto.optionName;
               info.optionList = MenuDbAccess.getOptionList(context.getDbAccessInvoker(), rtId, dto.optionId,
                       locale.getCode());
           }

           String json = ClassMapper.toJson(info);

           return new JsonResult(json);
        });
    }

    private static void renderCategoryBar(ServiceContext context, Document doc, String rtId, int categoryId,
                                          Locale locale) {
        List<CategoryDto> categoryList = CategoryDbAccess.getCategories(context.getDbAccessInvoker(), rtId,
                locale.getCode());

        Element categoryBarElem = doc.getElementById("category-bar");
        Element firstCateElem = categoryBarElem.getElementsByClass("category").first();
        categoryBarElem.empty();

        for (CategoryDto dto : categoryList) {
            Element cloneElem = firstCateElem.clone();
            cloneElem.tagName("a");
            cloneElem.text(dto.name);
            cloneElem.attr("href", createMenuUrl(rtId, dto.categoryId));
            if (dto.categoryId == categoryId) {
                cloneElem.addClass("active");
            } else {
                cloneElem.removeClass("active");
            }
            categoryBarElem.appendChild(cloneElem);
        }
    }

    private static String createMenuUrl(String rtId, int categoryId) {
        String encodedRtId = URLEncoder.encode(rtId, StandardCharsets.UTF_8);
        return "menu?rt_id=" + encodedRtId + "&category_id=" + categoryId;
    }

    private static void renderMenuList(ServiceContext context, Document doc, String rtId, int categoryId,
                                       Locale locale) {
        List<SubcategoryDto> subcategoryList
                = MenuDbAccess.getSubcategories(context.getDbAccessInvoker(), rtId, categoryId, locale.getCode());

        Element menuListElem = doc.getElementById("menu-list");
        Element subcategoryElem = menuListElem.getElementsByClass("subcategory").first();
        Element menuItemElem = menuListElem.getElementsByClass("menu-item").first();
        menuListElem.empty();

        if (subcategoryList.isEmpty()) {
            renderMenuItems(context, menuListElem, menuItemElem, rtId, categoryId, locale);
            return;
        }

        for (SubcategoryDto subcategoryDto : subcategoryList) {
            Element cloneSubcategoryElem = subcategoryElem.clone();
            cloneSubcategoryElem.text(subcategoryDto.name);
            menuListElem.appendChild(cloneSubcategoryElem);

            renderMenuItems(context, menuListElem, menuItemElem, rtId, subcategoryDto.subcategoryId, locale);
        }
    }

    private static void renderMenuItems(ServiceContext context, Element menuListElem, Element menuItemElem,
                                        String rtId, int categoryId, Locale locale) {
        List<MenuItemDto> menuItemList
                = MenuDbAccess.getMenuItems(context.getDbAccessInvoker(), rtId, categoryId, locale.getCode());

        for (MenuItemDto dto : menuItemList) {
            Element cloneElem = menuItemElem.clone();
            cloneElem.attr("data-menu-item-id", String.valueOf(dto.menuItemId));
            cloneElem.getElementsByClass("menu-name").first().text(dto.name);
            cloneElem.getElementsByClass("menu-price").first().text("¥" + dto.price);
            cloneElem.getElementsByClass("menu-thumbnail").first()
                    .attr("src", "menuimages?rt_id=" + rtId + "&menu_item_id=" + dto.menuItemId);
            menuListElem.appendChild(cloneElem);
        }
    }
}
