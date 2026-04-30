package com.kmaebashi.sporder.controller;

import com.kmaebashi.nctfw.ControllerInvoker;
import com.kmaebashi.nctfw.JsonResult;
import com.kmaebashi.nctfw.NotFoundException;
import com.kmaebashi.nctfw.RoutingResult;
import com.kmaebashi.sporder.common.CookieKey;
import com.kmaebashi.sporder.common.Locale;
import com.kmaebashi.sporder.service.MenuService;
import jakarta.servlet.http.HttpServletRequest;

public class MenuController {
    private MenuController() {}

    public static RoutingResult showMenu(ControllerInvoker invoker) {
        return invoker.invoke((context) -> {
            HttpServletRequest request = context.getServletRequest();
            String rtId = request.getParameter("rt_id");
            if (rtId == null) {
                throw new NotFoundException("URLが不正です。");
            }
            String categoryIdStr = request.getParameter("category_id");
            if (categoryIdStr == null) {
                throw new NotFoundException("URLが不正です。");
            }
            int categoryId = Integer.parseInt(categoryIdStr);
            Locale locale = Util.getLocaleFromCookie(request);
            String sessionToken = Util.searchCookie(request, CookieKey.AUTH_COOKIE).getValue();

            return MenuService.showMenu(context.getServiceInvoker(), rtId, categoryId, locale, sessionToken);
        });
    }

    public static RoutingResult getMenuItemInfo(ControllerInvoker invoker) {
        return invoker.invoke((context) -> {
            HttpServletRequest request = context.getServletRequest();
            String rtId = request.getParameter("rt_id");
            if (rtId == null) {
                throw new NotFoundException("URLが不正です。");
            }
            String menuItemIdStr = request.getParameter("menu_item_id");
            if (menuItemIdStr == null) {
                throw new NotFoundException("URLが不正です。");
            }
            int menuItemId = Integer.parseInt(menuItemIdStr);
            Locale locale = Util.getLocaleFromCookie(request);

            return MenuService.getMenuItemInfo(context.getServiceInvoker(), rtId, menuItemId, locale);
        });

    }
}
