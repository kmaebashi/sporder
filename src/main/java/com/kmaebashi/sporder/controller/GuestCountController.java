package com.kmaebashi.sporder.controller;

import com.kmaebashi.jsonparser.ClassMapper;
import com.kmaebashi.jsonparser.JsonElement;
import com.kmaebashi.jsonparser.JsonParser;
import com.kmaebashi.nctfw.ControllerInvoker;
import com.kmaebashi.nctfw.RoutingResult;
import com.kmaebashi.sporder.controller.data.GuestCountInfo;
import com.kmaebashi.sporder.service.GuestCountService;
import jakarta.servlet.http.HttpServletRequest;

public class GuestCountController {
    private GuestCountController() {}

    public static RoutingResult showPage(ControllerInvoker invoker) {
        return invoker.invoke((context) -> {
            String deviceSessionId = null;
            return GuestCountService.showPage(context.getServiceInvoker(), deviceSessionId);
        });
    }

    public static RoutingResult setGuestCount(ControllerInvoker invoker) {
        return invoker.invokeApi((context) -> {
            HttpServletRequest request = context.getServletRequest();
            try (JsonParser jsonParser = JsonParser.newInstance(request.getReader())) {

                JsonElement elem = jsonParser.parse();
                GuestCountInfo info = ClassMapper.toObject(elem, GuestCountInfo.class);

                return GuestCountService.setGuestCount(context.getServiceInvoker(), info);
            }
        });
    }
}
