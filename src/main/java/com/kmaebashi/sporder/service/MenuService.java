package com.kmaebashi.sporder.service;

import com.kmaebashi.nctfw.DocumentResult;
import com.kmaebashi.nctfw.ServiceContext;
import com.kmaebashi.nctfw.ServiceInvoker;
import com.kmaebashi.sporder.dbaccess.CategoryDbAccess;
import com.kmaebashi.sporder.dbaccess.MenuDbAccess;
import com.kmaebashi.sporder.dto.CategoryDto;
import com.kmaebashi.sporder.dto.MenuItemDto;
import com.kmaebashi.sporder.dto.SubcategoryDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.nio.file.Path;
import java.util.List;

public class MenuService {
    private MenuService() {}

    public static DocumentResult showMenu(ServiceInvoker invoker, String rtId, int categoryId) {
        return invoker.invoke((context) -> {
            Path htmlPath = context.getHtmlTemplateDirectory().resolve("menu.html");
            Document doc = Jsoup.parse(htmlPath.toFile(), "UTF-8");
            DocumentResult ret = new DocumentResult(doc);

            renderCategoryBar(context, doc, rtId, categoryId);
            renderMenuList(context, doc, rtId, categoryId);

            return ret;
        });
    }

    private static void renderCategoryBar(ServiceContext context, Document doc, String rtId, int categoryId) {
        List<CategoryDto> categoryList = CategoryDbAccess.getCategories(context.getDbAccessInvoker(), rtId);

        Element categoryBarElem = doc.getElementById("category-bar");
        Element firstCateElem = categoryBarElem.getElementsByClass("category").first();
        categoryBarElem.empty();

        for (CategoryDto dto : categoryList) {
            Element cloneElem = firstCateElem.clone();
            cloneElem.text(dto.name);
            if (dto.categoryId == categoryId) {
                cloneElem.addClass("active");
            } else {
                cloneElem.removeClass("active");
            }
            categoryBarElem.appendChild(cloneElem);
        }
    }

    private static void renderMenuList(ServiceContext context, Document doc, String rtId, int categoryId) {
        List<SubcategoryDto> subcategoryList
                = MenuDbAccess.getSubcategories(context.getDbAccessInvoker(), rtId, categoryId);

        Element menuListElem = doc.getElementById("menu-list");
        Element subcategoryElem = menuListElem.getElementsByClass("subcategory").first();
        Element menuItemElem = menuListElem.getElementsByClass("menu-item").first();
        menuListElem.empty();

        if (subcategoryList.isEmpty()) {
            renderMenuItems(context, menuListElem, menuItemElem, rtId, categoryId);
            return;
        }

        for (SubcategoryDto subcategoryDto : subcategoryList) {
            Element cloneSubcategoryElem = subcategoryElem.clone();
            cloneSubcategoryElem.text(subcategoryDto.name);
            menuListElem.appendChild(cloneSubcategoryElem);

            renderMenuItems(context, menuListElem, menuItemElem, rtId, subcategoryDto.subcategoryId);
        }
    }

    private static void renderMenuItems(ServiceContext context, Element menuListElem, Element menuItemElem,
                                        String rtId, int categoryId) {
        List<MenuItemDto> menuItemList
                = MenuDbAccess.getMenuItems(context.getDbAccessInvoker(), rtId, categoryId);

        for (MenuItemDto dto : menuItemList) {
            Element cloneElem = menuItemElem.clone();
            cloneElem.getElementsByClass("menu-name").first().text(dto.name);
            cloneElem.getElementsByClass("menu-price").first().text("¥" + dto.price);
            cloneElem.getElementsByClass("menu-thumbnail").first()
                    .attr("src", "menuimages?rt_id=" + rtId + "&menu_item_id=" + dto.menuItemId);
            menuListElem.appendChild(cloneElem);
        }
    }
}
