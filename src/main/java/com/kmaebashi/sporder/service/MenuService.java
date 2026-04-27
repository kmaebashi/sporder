package com.kmaebashi.sporder.service;

import com.kmaebashi.nctfw.DocumentResult;
import com.kmaebashi.nctfw.ServiceContext;
import com.kmaebashi.nctfw.ServiceInvoker;
import com.kmaebashi.sporder.dbaccess.CategoryDbAccess;
import com.kmaebashi.sporder.dto.CategoryDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
}
