package com.kmaebashi.sporder.controller;

import com.kmaebashi.nctfw.BadRequestException;
import com.kmaebashi.nctfw.ControllerInvoker;
import com.kmaebashi.nctfw.RoutingResult;
import com.kmaebashi.sporder.service.ImageService;
import jakarta.servlet.http.HttpServletRequest;

import java.nio.file.Path;

public class ImageController {
    private ImageController() {}

    public static RoutingResult getMenuThumbnail(ControllerInvoker invoker, Path imageRoot) {
        return invoker.invoke(context -> {
            HttpServletRequest request = context.getServletRequest();
            String rtId = request.getParameter("rt_id");
            String menuItemIdStr = request.getParameter("menu_item_id");
            if (rtId == null || menuItemIdStr == null) {
                throw new BadRequestException("不正なリクエストです。", true);
            }
            int menuItemId = Integer.parseInt(menuItemIdStr);
            return ImageService.getMenuThumbnail(context.getServiceInvoker(), imageRoot, rtId, menuItemId);
        });
    }

    public static RoutingResult getMenuFullsize(ControllerInvoker invoker, Path imageRoot) {
        return invoker.invoke(context -> {
            HttpServletRequest request = context.getServletRequest();
            String rtId = request.getParameter("rt_id");
            String menuItemIdStr = request.getParameter("menu_item_id");
            if (rtId == null || menuItemIdStr == null) {
                throw new BadRequestException("不正なリクエストです。", true);
            }
            int menuItemId = Integer.parseInt(menuItemIdStr);
            return ImageService.getMenuFullsize(context.getServiceInvoker(), imageRoot, rtId, menuItemId);
        });
    }
}
