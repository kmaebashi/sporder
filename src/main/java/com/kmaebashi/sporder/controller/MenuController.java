package com.kmaebashi.sporder.controller;

import com.kmaebashi.nctfw.ControllerInvoker;
import com.kmaebashi.nctfw.DocumentResult;
import com.kmaebashi.nctfw.NotFoundException;
import com.kmaebashi.nctfw.RoutingResult;
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

            return MenuService.showMenu(context.getServiceInvoker(), rtId, categoryId);
        });
    }
}
