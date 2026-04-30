package com.kmaebashi.sporder.service;

import com.kmaebashi.jsonparser.ClassMapper;
import com.kmaebashi.nctfw.BadRequestException;
import com.kmaebashi.nctfw.InvokerOption;
import com.kmaebashi.nctfw.JsonResult;
import com.kmaebashi.nctfw.ServiceInvoker;
import com.kmaebashi.sporder.controller.data.ApiResult;
import com.kmaebashi.sporder.controller.data.OrderInfo;
import com.kmaebashi.sporder.controller.data.PlaceOrderInfo;
import com.kmaebashi.sporder.dbaccess.LoginDbAccess;
import com.kmaebashi.sporder.dbaccess.OrderDbAccess;
import com.kmaebashi.sporder.dto.DeviceSessionDto;

import java.time.LocalDateTime;

public class OrderService {
    private OrderService() {}

    public static JsonResult order(ServiceInvoker invoker, String sessionToken, OrderInfo info) {
        return invoker.invoke((context) -> {
            if (info == null || info.count <= 0) {
                throw new BadRequestException("注文内容が不正です。");
            }

            DeviceSessionDto sessionDto = LoginDbAccess.getDeviceSessionBySessionTokenForUpdate(
                    context.getDbAccessInvoker(), sessionToken);
            if (sessionDto == null || sessionDto.expiresAt.isBefore(LocalDateTime.now())) {
                throw new BadRequestException("セッションが無効です。");
            }
            if (!sessionDto.rtId.equals(info.rtId) || !sessionDto.orderGroupId.equals(info.orderGroupId)) {
                throw new BadRequestException("リクエストが不正です。");
            }
            ensureOrderGroupOpen(context.getDbAccessInvoker(), info.rtId, info.orderGroupId);

            LocalDateTime now = LocalDateTime.now();
            LoginDbAccess.updateDeviceSessionExpiresAt(context.getDbAccessInvoker(), sessionDto.rtId,
                    sessionDto.deviceSessionId, now.plusHours(6));

            Integer orderId = OrderDbAccess.getNextOrderIdForUpdate(context.getDbAccessInvoker(), info.rtId);
            if (orderId == null) {
                throw new BadRequestException("注文IDを採番できません。");
            }
            OrderDbAccess.updateNextOrderId(context.getDbAccessInvoker(), info.rtId, orderId + 1, now);
            OrderDbAccess.insertOrder(context.getDbAccessInvoker(), info.rtId, info.orderGroupId, orderId,
                    info.menuItem, info.count, now);

            ApiResult result = new ApiResult("成功しました。");
            return new JsonResult(ClassMapper.toJson(result));
        }, InvokerOption.TRANSACTIONAL);
    }

    private static void ensureOrderGroupOpen(com.kmaebashi.nctfw.DbAccessInvoker invoker, String rtId,
                                             String orderGroupId) {
        LocalDateTime closedAt = LoginDbAccess.getOrderGroupClosedAt(invoker, rtId, orderGroupId);
        if (closedAt != null) {
            throw new BadRequestException("オーダーグループは終了しています。");
        }
    }

    public static JsonResult placeOrder(ServiceInvoker invoker, String sessionToken, PlaceOrderInfo info) {
        return invoker.invoke((context) -> {
            return null;
        });
    }

}
