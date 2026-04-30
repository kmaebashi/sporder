package com.kmaebashi.sporder.controller;

import com.kmaebashi.jsonparser.ClassMapper;
import com.kmaebashi.jsonparser.JsonElement;
import com.kmaebashi.jsonparser.JsonParser;
import com.kmaebashi.nctfw.ControllerInvoker;
import com.kmaebashi.nctfw.NotFoundException;
import com.kmaebashi.nctfw.RoutingResult;
import com.kmaebashi.sporder.common.CookieKey;
import com.kmaebashi.sporder.common.Locale;
import com.kmaebashi.sporder.controller.data.PlaceOrderInfo;
import com.kmaebashi.sporder.service.OrderListService;
import jakarta.servlet.http.HttpServletRequest;

public class OrderListController {
    public static RoutingResult showPage(ControllerInvoker invoker) {
        return invoker.invoke((context) -> {
            HttpServletRequest request = context.getServletRequest();
            String rtId = request.getParameter("rt_id");
            if (rtId == null) {
                throw new NotFoundException("URLが不正です。");
            }
            String sessionToken = Util.searchCookie(request, CookieKey.AUTH_COOKIE).getValue();
            Locale locale = Util.getLocaleFromCookie(request);

            return OrderListService.showPage(context.getServiceInvoker(), rtId, sessionToken, locale);
        });
    }

    public static RoutingResult placeOrder(ControllerInvoker invoker) {
        return invoker.invoke((context) -> {
            HttpServletRequest request = context.getServletRequest();
            String sessionToken = Util.searchCookie(request, CookieKey.AUTH_COOKIE).getValue();

            try (JsonParser jsonParser = JsonParser.newInstance(request.getReader())) {
                JsonElement elem = jsonParser.parse();
                PlaceOrderInfo info = ClassMapper.toObject(elem, PlaceOrderInfo.class);

                return OrderListService.placeOrder(context.getServiceInvoker(), sessionToken, info);
            }
        });
    }
}
