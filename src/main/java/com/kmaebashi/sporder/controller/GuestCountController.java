package com.kmaebashi.sporder.controller;

import com.kmaebashi.jsonparser.ClassMapper;
import com.kmaebashi.jsonparser.JsonElement;
import com.kmaebashi.jsonparser.JsonParser;
import com.kmaebashi.nctfw.ControllerInvoker;
import com.kmaebashi.nctfw.NotFoundException;
import com.kmaebashi.nctfw.RoutingResult;
import com.kmaebashi.sporder.common.CookieKey;
import com.kmaebashi.sporder.common.Locale;
import com.kmaebashi.sporder.controller.data.GuestCountInfo;
import com.kmaebashi.sporder.service.GuestCountService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class GuestCountController {
    private GuestCountController() {}

    public static RoutingResult showPage(ControllerInvoker invoker) {
        return invoker.invoke((context) -> {
            HttpServletRequest request = context.getServletRequest();

            String rtId = request.getParameter("rt_id");
            if (rtId == null) {
                throw new NotFoundException("URLが不正です。");
            }
            Cookie[] cookies = request.getCookies();
            String sessionToken = Util.searchCookie(request, CookieKey.AUTH_COOKIE).getValue();
            Locale locale = Util.getLocaleFromCookie(request);

            return GuestCountService.showPage(context.getServiceInvoker(), rtId, sessionToken, locale);
        });
    }

    public static RoutingResult setGuestCount(ControllerInvoker invoker) {
        return invoker.invokeApi((context) -> {
            HttpServletRequest request = context.getServletRequest();
            HttpServletResponse response = context.getServletResponse();

            try (JsonParser jsonParser = JsonParser.newInstance(request.getReader())) {
                JsonElement elem = jsonParser.parse();
                GuestCountInfo info = ClassMapper.toObject(elem, GuestCountInfo.class);


                return GuestCountService.setGuestCount(context.getServiceInvoker(), info);
            }
        });
    }
}
