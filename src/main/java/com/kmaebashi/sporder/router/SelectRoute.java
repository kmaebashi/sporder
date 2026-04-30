package com.kmaebashi.sporder.router;

import com.kmaebashi.sporder.util.Log;

import java.util.HashMap;

public class SelectRoute {
    private SelectRoute() {}

    static Route select(String path, HashMap<String, Object> params) {
        Log.info("path..[" + path + "]");
        if (path.equals("qr")) {
            return Route.QR;
        } else if (path.equals("join")) {
            return Route.JOIN;
        } else if (path.equals("guestcount")) {
            return Route.GUEST_COUNT;
        } else if (path.equals("setguestcount")) {
            return Route.SET_GUEST_COUNT;
        } else if (path.equals("menu")) {
            return Route.MENU;
        } else if (path.equals("menuimages")) {
            return Route.MENU_IMAGE_S;
        } else if (path.equals("menuimagel")) {
            return Route.MENU_IMAGE_L;
        } else if (path.equals("getmenuiteminfo")) {
            return Route.GET_MENU_ITEM_INFO;
        } else if (path.equals("order")) {
            return Route.ORDER;
        } else if (path.equals("placeorder")) {
            return Route.PLACE_ORDER;
        } else if (path.equals("orderlist")) {
            return Route.ORDER_LIST;
        } else if (path.equals("orderhistory")) {
            return Route.ORDER_HISTORY;
        }
        return Route.NO_ROUTE;
    }

}
