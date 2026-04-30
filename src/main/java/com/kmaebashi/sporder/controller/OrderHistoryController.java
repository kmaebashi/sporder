package com.kmaebashi.sporder.controller;

import com.kmaebashi.nctfw.ControllerInvoker;
import com.kmaebashi.nctfw.NotFoundException;
import com.kmaebashi.nctfw.RoutingResult;
import com.kmaebashi.sporder.common.CookieKey;
import com.kmaebashi.sporder.common.Locale;
import com.kmaebashi.sporder.service.OrderHistoryService;
import jakarta.servlet.http.HttpServletRequest;

public class OrderHistoryController {
    private OrderHistoryController() {}

    public static RoutingResult showPage(ControllerInvoker invoker) {
        return invoker.invoke((context) -> {
            HttpServletRequest request = context.getServletRequest();
            String rtId = request.getParameter("rt_id");
            if (rtId == null) {
                throw new NotFoundException("URLが不正です。");
            }
            String sessionToken = Util.searchCookie(request, CookieKey.AUTH_COOKIE).getValue();
            Locale locale = Util.getLocaleFromCookie(request);

            return OrderHistoryService.showPage(context.getServiceInvoker(), rtId, sessionToken, locale);
        });
    }
}
