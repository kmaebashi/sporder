package com.kmaebashi.sporder.controller;

import com.kmaebashi.nctfw.ControllerInvoker;
import com.kmaebashi.nctfw.DocumentResult;
import com.kmaebashi.nctfw.NotFoundException;
import com.kmaebashi.nctfw.RoutingResult;
import com.kmaebashi.nctfw.ServiceInvoker;
import com.kmaebashi.sporder.common.CookieKey;
import com.kmaebashi.sporder.service.LoginService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginController {
    private LoginController() {}

    public static RoutingResult start(ControllerInvoker invoker) {
        return invoker.invoke((context) -> {
            HttpServletRequest request = context.getServletRequest();
            String rtId = request.getParameter("rt_id");
            if (rtId == null) {
                throw new NotFoundException("URLが不正です。");
            }
            String tableCode = request.getParameter("table_code");
            if (tableCode == null) {
                throw new NotFoundException("URLが不正です。");
            }
            return LoginService.start(context.getServiceInvoker(), rtId, tableCode);
        });
    }

    public static RoutingResult join(ControllerInvoker invoker) {
        return invoker.invokeApi((context) -> {
            HttpServletRequest request = context.getServletRequest();
            String rtId = request.getParameter("rt_id");
            String orderGroupId = request.getParameter("order_group_id");
            String joinTokenId = request.getParameter("join_token_id");
            String isFirstStr = request.getParameter("is_first");
            if (rtId == null || orderGroupId == null || joinTokenId == null ||
                    isFirstStr == null || !(isFirstStr.equals("true") || isFirstStr.equals("false"))) {
                throw new NotFoundException("リクエストが不正です。");
            }
            boolean isFirst = isFirstStr.equals("true");
            String[] outSessionToken = new String[1];

            RoutingResult ret = LoginService.join(context.getServiceInvoker(), rtId, orderGroupId, joinTokenId, isFirst,
                                                  outSessionToken);
            HttpServletResponse response = context.getServletResponse();
            Cookie newAuthCookie = new Cookie(CookieKey.AUTH_COOKIE, outSessionToken[0]);
            newAuthCookie.setMaxAge(60 * 60 * 24);
            newAuthCookie.setHttpOnly(true);
            newAuthCookie.setSecure(true);
            newAuthCookie.setPath(context.getServletRequest().getContextPath());
            context.getServletResponse().addCookie(newAuthCookie);

            return ret;
        });
    }
}
