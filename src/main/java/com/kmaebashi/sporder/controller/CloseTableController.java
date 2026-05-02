package com.kmaebashi.sporder.controller;

import com.kmaebashi.jsonparser.ClassMapper;
import com.kmaebashi.jsonparser.JsonElement;
import com.kmaebashi.jsonparser.JsonParser;
import com.kmaebashi.nctfw.ControllerInvoker;
import com.kmaebashi.nctfw.RoutingResult;
import com.kmaebashi.sporder.controller.data.CloseTableInfo;
import com.kmaebashi.sporder.service.CloseTableService;
import jakarta.servlet.http.HttpServletRequest;

public class CloseTableController {
    private CloseTableController() {}

    public static RoutingResult close(ControllerInvoker invoker) {
        return invoker.invokeApi((context) -> {
            HttpServletRequest request = context.getServletRequest();

            try (JsonParser jsonParser = JsonParser.newInstance(request.getReader())) {
                JsonElement elem = jsonParser.parse();
                CloseTableInfo info = ClassMapper.toObject(elem, CloseTableInfo.class);

                return CloseTableService.close(context.getServiceInvoker(), info.tableCode);
            }
        });
    }
}
