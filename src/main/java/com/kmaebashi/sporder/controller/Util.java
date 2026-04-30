package com.kmaebashi.sporder.controller;

import com.kmaebashi.sporder.common.CookieKey;
import com.kmaebashi.sporder.common.Locale;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class Util {
    private Util() {}

    public static Cookie searchCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie;
            }
        }
        return null;
    }

    static Locale getLocaleFromCookie(HttpServletRequest request) {
        Cookie cookie = Util.searchCookie(request, CookieKey.LOCALE_COOKIE);
        Locale locale = Locale.fromCodeStr(cookie.getValue());

        return locale;
    }

    static void setCacheControl(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store");
    }
}
