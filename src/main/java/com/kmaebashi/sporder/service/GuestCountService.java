package com.kmaebashi.sporder.service;

import com.kmaebashi.nctfw.DocumentResult;
import com.kmaebashi.nctfw.JsonResult;
import com.kmaebashi.nctfw.ServiceInvoker;
import com.kmaebashi.sporder.controller.data.GuestCountInfo;

public class GuestCountService {
    private GuestCountService() {}

    public static DocumentResult showPage(ServiceInvoker invoker, String deviceSessionId) {
        return invoker.invoke((context) -> {
           return null;
        });
    }

    public static JsonResult setGuestCount(ServiceInvoker invoker, GuestCountInfo info) {
        return invoker.invoke((context) -> {
            return null;
        });
    }
}
