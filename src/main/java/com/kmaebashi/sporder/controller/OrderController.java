package com.kmaebashi.sporder.controller;

import com.kmaebashi.jsonparser.ClassMapper;
import com.kmaebashi.jsonparser.JsonElement;
import com.kmaebashi.jsonparser.JsonParser;
import com.kmaebashi.nctfw.ControllerInvoker;
import com.kmaebashi.nctfw.RoutingResult;
import com.kmaebashi.sporder.common.CookieKey;
import com.kmaebashi.sporder.controller.data.OrderInfo;
import com.kmaebashi.sporder.controller.data.PlaceOrderInfo;
import com.kmaebashi.sporder.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;

public class OrderController {
    private OrderController() {}

    public static RoutingResult order(ControllerInvoker invoker) {
        return invoker.invoke((context) -> {
            HttpServletRequest request = context.getServletRequest();
            String sessionToken = Util.searchCookie(request, CookieKey.AUTH_COOKIE).getValue();

            try (JsonParser jsonParser = JsonParser.newInstance(request.getReader())) {
                JsonElement elem = jsonParser.parse();
                OrderInfo info = ClassMapper.toObject(elem, OrderInfo.class);

                return OrderService.order(context.getServiceInvoker(), sessionToken, info);
            }
        });
    }

}
